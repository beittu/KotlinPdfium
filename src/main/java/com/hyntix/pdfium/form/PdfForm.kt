package com.hyntix.pdfium.form

import com.hyntix.pdfium.PdfiumCore
import com.hyntix.pdfium.PdfPage
import java.io.Closeable

/**
 * Represents a PDF form (interactive form) in a document.
 * 
 * This class provides high-level access to form fields and operations.
 * Create instances using PdfDocument.initForm() or similar methods.
 * Always call [close] when done to release native resources.
 * 
 * @property core The PdfiumCore instance for native operations
 * @property docPtr The native document pointer
 * @property formPtr The native form handle pointer
 */
class PdfForm(
    val core: PdfiumCore,
    val docPtr: Long,
    val formPtr: Long
) : Closeable {
    
    private var isClosed = false
    
    /**
     * Get the form type.
     * 
     * @return The form type constant
     */
    fun getFormType(): Int {
        checkNotClosed()
        // Note: PDFium doesn't have a direct API for form type
        // This would need to be implemented if needed
        return 0
    }
    
    /**
     * Get all form fields on a specific page.
     * 
     * @param page The page to get form fields from
     * @return List of all form fields on the page
     */
    fun getFields(page: PdfPage): List<FormField> {
        checkNotClosed()
        return page.getFormFields(formPtr)
    }
    
    /**
     * Get a specific form field by name on a page.
     * 
     * @param page The page to search
     * @param name The name of the form field
     * @return The form field if found, null otherwise
     */
    fun getField(page: PdfPage, name: String): FormField? {
        checkNotClosed()
        return page.getFormFieldByName(formPtr, name)
    }
    
    /**
     * Set the value of a form field.
     * 
     * @param page The page containing the form field
     * @param field The form field to update
     * @param value The new value
     * @return True if successful
     */
    fun setFieldValue(page: PdfPage, field: FormField, value: String): Boolean {
        checkNotClosed()
        return page.setFormFieldValue(formPtr, field, value)
    }
    
    /**
     * Get all options for a combo box or list box field.
     * 
     * @param page The page containing the form field
     * @param field The form field to get options for
     * @return List of options
     */
    fun getFieldOptions(page: PdfPage, field: FormField): List<FormFieldOption> {
        checkNotClosed()
        return page.getFormFieldOptions(formPtr, field)
    }
    
    /**
     * Undo the last change on a page.
     * 
     * Note: PDFium's undo/redo support is limited and may not work as expected.
     * 
     * @param page The page to undo changes on
     * @return True if successful
     */
    fun undo(page: PdfPage): Boolean {
        checkNotClosed()
        // Note: PDFium doesn't have a direct undo API for forms
        // This would need to be implemented via form callbacks if supported
        return false
    }
    
    /**
     * Redo the last undone change on a page.
     * 
     * Note: PDFium's undo/redo support is limited and may not work as expected.
     * 
     * @param page The page to redo changes on
     * @return True if successful
     */
    fun redo(page: PdfPage): Boolean {
        checkNotClosed()
        // Note: PDFium doesn't have a direct redo API for forms
        // This would need to be implemented via form callbacks if supported
        return false
    }
    
    /**
     * Check if undo is available for a page.
     * 
     * @param page The page to check
     * @return True if undo is available
     */
    fun canUndo(page: PdfPage): Boolean {
        checkNotClosed()
        // Note: PDFium doesn't have a direct undo API
        return false
    }
    
    /**
     * Check if redo is available for a page.
     * 
     * @param page The page to check
     * @return True if redo is available
     */
    fun canRedo(page: PdfPage): Boolean {
        checkNotClosed()
        // Note: PDFium doesn't have a direct redo API
        return false
    }
    
    /**
     * Close the form and release native resources.
     */
    override fun close() {
        if (!isClosed) {
            core.exitFormFillEnvironment(formPtr)
            isClosed = true
        }
    }
    
    /**
     * Check if the form has been closed.
     */
    fun isClosed(): Boolean = isClosed
    
    private fun checkNotClosed() {
        check(!isClosed) { "Form has been closed" }
    }
}
