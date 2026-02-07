package com.hyntix.pdfium

import android.graphics.Bitmap
import java.io.Closeable
import kotlin.math.min
import kotlin.math.max

/**
 * Represents a page in a PDF document.
 * 
 * Create instances using [PdfDocument.openPage].
 * Always call [close] when done to release native resources.
 */
class PdfPage internal constructor(
    private val core: PdfiumCore,
    private val docPtr: Long,
    private val pagePtr: Long,
    val index: Int
) : Closeable {

    private var isClosed = false


    /**
     * Page width in points (1/72 inch).
     */
    val width: Double by lazy {
        checkNotClosed()
        core.getPageWidth(pagePtr)
    }

    /**
     * Page height in points (1/72 inch).
     */
    val height: Double by lazy {
        checkNotClosed()
        core.getPageHeight(pagePtr)
    }

    /**
     * Render the page to an Android Bitmap.
     * 
     * @param bitmap The target bitmap. Must be mutable and configured as ARGB_8888.
     * @param startX X-coordinate of the upper-left corner of the drawing area.
     * @param startY Y-coordinate of the upper-left corner of the drawing area.
     * @param drawWidth Width of the drawing area.
     * @param drawHeight Height of the drawing area.
     * @param renderAnnot Whether to render annotations.
     */
    fun render(
        bitmap: Bitmap,
        startX: Int = 0,
        startY: Int = 0,
        drawWidth: Int = bitmap.width,
        drawHeight: Int = bitmap.height,
        renderAnnot: Boolean = true
    ) {
        checkNotClosed()
        core.renderPageBitmap(pagePtr, bitmap, startX, startY, drawWidth, drawHeight, renderAnnot)
    }

    /**
     * Check if the page has been closed.
     */
    fun isClosed(): Boolean = isClosed
    
    fun openTextPage(): PdfTextPage {
        checkNotClosed()
        val textPagePtr = core.loadTextPage(docPtr, pagePtr)
        if (textPagePtr == 0L) {
            throw IllegalStateException("Failed to load text page")
        }
        return PdfTextPage(core, docPtr, pagePtr, textPagePtr)
    }

    /**
     * Get all annotations on the page.
     */
    fun getAnnotations(): List<PdfAnnotation> {
        checkNotClosed()
        val count = core.getAnnotCount(pagePtr)
        val annotations = ArrayList<PdfAnnotation>(count)
        
        for (i in 0 until count) {
            val annotPtr = core.getAnnot(pagePtr, i)
            if (annotPtr != 0L) {
                val subtype = core.getAnnotSubtype(annotPtr)
                val rectArray = core.getAnnotRect(annotPtr)
                val rect = android.graphics.RectF(
                    rectArray[0].toFloat(),
                    rectArray[1].toFloat(),
                    rectArray[2].toFloat(),
                    rectArray[3].toFloat()
                )
                annotations.add(PdfAnnotation(rect, subtype))
                core.closeAnnot(annotPtr)
            }
        }
        return annotations
    }

    /**
     * Get all links on the page.
     */
    fun getLinks(): List<PdfLink> {
        checkNotClosed()
        val count = core.getAnnotCount(pagePtr)
        val links = ArrayList<PdfLink>()

        for (i in 0 until count) {
            val annotPtr = core.getAnnot(pagePtr, i)
            if (annotPtr != 0L) {
                val subtype = core.getAnnotSubtype(annotPtr)
                if (subtype == 2) { // FPDF_ANNOT_LINK
                    val rectArray = core.getAnnotRect(annotPtr)
                    val rect = android.graphics.RectF(
                        rectArray[0].toFloat(), rectArray[1].toFloat(),
                        rectArray[2].toFloat(), rectArray[3].toFloat()
                    )

                    val linkPtr = core.getLinkFromAnnot(annotPtr)
                    val destIndex = core.getLinkDestIndex(docPtr, linkPtr)
                    val uri = core.getLinkURI(docPtr, linkPtr)

                    links.add(PdfLink(rect, destIndex, uri))
                }
                core.closeAnnot(annotPtr)
            }
        }
        return links
    }

    /**
     * Get a link at the specified coordinates.
     */
    fun getLinkAt(x: Double, y: Double): PdfLink? {
        checkNotClosed()
        val linkPtr = core.getLinkAtPoint(pagePtr, x, y)
        if (linkPtr != 0L) {
            val destIndex = core.getLinkDestIndex(docPtr, linkPtr)
            val uri = core.getLinkURI(docPtr, linkPtr)
            val rectArray = core.getLinkRect(linkPtr)
            val rect = android.graphics.RectF(
                rectArray[0].toFloat(), rectArray[1].toFloat(),
                rectArray[2].toFloat(), rectArray[3].toFloat()
            )
            return PdfLink(rect, destIndex, uri)
        }
        return null
    }

    /**
     * Close the page and release native resources.
     */
    override fun close() {
        if (!isClosed) {
            core.closePage(pagePtr)
            isClosed = true
        }
    }

    private fun checkNotClosed() {
        check(!isClosed) { "Page has been closed" }
    }
    
    /**
     * Map a rectangle from page coordinates to device coordinates.
     */
    fun mapRectToDevice(
        startX: Int, startY: Int,
        sizeX: Int, sizeY: Int,
        rect: android.graphics.RectF
    ): android.graphics.RectF {
        checkNotClosed()
        val leftTop = core.mapPageToDevice(pagePtr, startX, startY, sizeX, sizeY, 0, rect.left.toDouble(), rect.top.toDouble())
        val rightBottom = core.mapPageToDevice(pagePtr, startX, startY, sizeX, sizeY, 0, rect.right.toDouble(), rect.bottom.toDouble())
        
        val x1 = leftTop[0].toFloat()
        val y1 = leftTop[1].toFloat()
        val x2 = rightBottom[0].toFloat()
        val y2 = rightBottom[1].toFloat()

        return android.graphics.RectF(
            min(x1, x2),
            min(y1, y2),
            max(x1, x2),
            max(y1, y2)
        )
    }

    /**
     * Map device (screen) coordinates to page (PDF) coordinates.
     */
    fun mapDeviceToPage(
        startX: Int, startY: Int,
        sizeX: Int, sizeY: Int,
        deviceX: Int, deviceY: Int
    ): DoubleArray {
        checkNotClosed()
        return core.mapDeviceToPage(pagePtr, startX, startY, sizeX, sizeY, 0, deviceX, deviceY)
    }

    /**
     * Get all form fields on this page.
     * Requires a form handle to be initialized via PdfForm.
     * 
     * NOTE: The returned FormField objects contain annotation pointers that must be
     * closed after use to prevent memory leaks. Call closeFormField() when done with each field.
     * 
     * @param formPtr The form handle pointer from PdfForm
     * @return List of all form fields on this page
     */
    fun getFormFields(formPtr: Long): List<com.hyntix.pdfium.form.FormField> {
        checkNotClosed()
        if (formPtr == 0L) return emptyList()
        
        val count = core.getFormFieldCount(formPtr, pagePtr)
        val fields = ArrayList<com.hyntix.pdfium.form.FormField>(count)
        
        for (i in 0 until count) {
            val annotPtr = core.getFormFieldAtIndex(formPtr, pagePtr, i)
            if (annotPtr != 0L) {
                val typeValue = core.getFormFieldType(formPtr, annotPtr)
                val type = com.hyntix.pdfium.form.FormFieldType.fromValue(typeValue)
                
                // Only process actual form fields (not all annotations)
                if (type != com.hyntix.pdfium.form.FormFieldType.UNKNOWN) {
                    val name = core.getFormFieldName(formPtr, annotPtr)
                    val value = core.getFormFieldValue(formPtr, annotPtr)
                    val rectArray = core.getAnnotRect(annotPtr)
                    val rect = android.graphics.RectF(
                        rectArray[0].toFloat(),
                        rectArray[1].toFloat(),
                        rectArray[2].toFloat(),
                        rectArray[3].toFloat()
                    )
                    
                    fields.add(com.hyntix.pdfium.form.FormField(
                        name = name,
                        type = type,
                        value = value,
                        pageIndex = index,
                        rect = rect,
                        annotPtr = annotPtr
                    ))
                } else {
                    // Close annotations that are not form fields
                    core.closeAnnot(annotPtr)
                }
            }
        }
        
        return fields
    }

    /**
     * Close a form field and release its native resources.
     * Call this when you're done using a FormField to prevent memory leaks.
     * 
     * @param field The form field to close
     */
    fun closeFormField(field: com.hyntix.pdfium.form.FormField) {
        if (field.annotPtr != 0L) {
            core.closeAnnot(field.annotPtr)
        }
    }

    /**
     * Close multiple form fields.
     * 
     * @param fields The form fields to close
     */
    fun closeFormFields(fields: List<com.hyntix.pdfium.form.FormField>) {
        fields.forEach { closeFormField(it) }
    }

    /**
     * Get a specific form field by name on this page.
     * 
     * NOTE: The returned FormField contains an annotation pointer that must be
     * closed after use. Call closeFormField() when done with the field.
     * 
     * @param formPtr The form handle pointer from PdfForm
     * @param name The name of the form field to find
     * @return The form field if found, null otherwise
     */
    fun getFormFieldByName(formPtr: Long, name: String): com.hyntix.pdfium.form.FormField? {
        checkNotClosed()
        if (formPtr == 0L) return null
        
        val count = core.getFormFieldCount(formPtr, pagePtr)
        
        for (i in 0 until count) {
            val annotPtr = core.getFormFieldAtIndex(formPtr, pagePtr, i)
            if (annotPtr != 0L) {
                val typeValue = core.getFormFieldType(formPtr, annotPtr)
                val type = com.hyntix.pdfium.form.FormFieldType.fromValue(typeValue)
                
                // Only process actual form fields (not all annotations)
                if (type != com.hyntix.pdfium.form.FormFieldType.UNKNOWN) {
                    val fieldName = core.getFormFieldName(formPtr, annotPtr)
                    
                    if (fieldName == name) {
                        // Found the field - get its details
                        val value = core.getFormFieldValue(formPtr, annotPtr)
                        val rectArray = core.getAnnotRect(annotPtr)
                        val rect = android.graphics.RectF(
                            rectArray[0].toFloat(),
                            rectArray[1].toFloat(),
                            rectArray[2].toFloat(),
                            rectArray[3].toFloat()
                        )
                        
                        return com.hyntix.pdfium.form.FormField(
                            name = fieldName,
                            type = type,
                            value = value,
                            pageIndex = index,
                            rect = rect,
                            annotPtr = annotPtr
                        )
                    } else {
                        // Not the field we're looking for, close it
                        core.closeAnnot(annotPtr)
                    }
                } else {
                    // Not a form field, close it
                    core.closeAnnot(annotPtr)
                }
            }
        }
        
        return null
    }

    /**
     * Set the value of a form field by name.
     * 
     * This method finds the field, sets its value, and automatically closes the field.
     * 
     * @param formPtr The form handle pointer from PdfForm
     * @param fieldName The name of the form field
     * @param value The value to set
     * @return True if successful
     */
    fun setFormFieldValue(formPtr: Long, fieldName: String, value: String): Boolean {
        checkNotClosed()
        val field = getFormFieldByName(formPtr, fieldName)
        if (field != null) {
            val success = setFormFieldValue(formPtr, field, value)
            closeFormField(field)
            return success
        }
        return false
    }

    /**
     * Set the value of a form field.
     * 
     * @param formPtr The form handle pointer from PdfForm
     * @param field The form field to update
     * @param value The value to set
     * @return True if successful
     */
    fun setFormFieldValue(formPtr: Long, field: com.hyntix.pdfium.form.FormField, value: String): Boolean {
        checkNotClosed()
        if (field.annotPtr == 0L) return false
        return core.setFormFieldValue(formPtr, pagePtr, field.annotPtr, value)
    }

    /**
     * Get all options for a combo box or list box form field.
     * 
     * @param formPtr The form handle pointer from PdfForm
     * @param field The form field to get options for
     * @return List of options, empty if field doesn't support options
     */
    fun getFormFieldOptions(formPtr: Long, field: com.hyntix.pdfium.form.FormField): List<com.hyntix.pdfium.form.FormFieldOption> {
        checkNotClosed()
        if (!field.hasOptions() || field.annotPtr == 0L) return emptyList()
        
        val count = core.getFormFieldOptionCount(formPtr, field.annotPtr)
        val options = ArrayList<com.hyntix.pdfium.form.FormFieldOption>(count)
        
        for (i in 0 until count) {
            val label = core.getFormFieldOptionLabel(formPtr, field.annotPtr, i)
            val isSelected = core.isFormFieldOptionSelected(formPtr, field.annotPtr, i)
            options.add(com.hyntix.pdfium.form.FormFieldOption(label, isSelected, i))
        }
        
        return options
    }

    internal fun getPointer(): Long {
        checkNotClosed()
        return pagePtr
    }
}

