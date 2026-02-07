package com.hyntix.pdfium.form

import com.hyntix.pdfium.PdfiumCore
import com.hyntix.pdfium.PdfPage
import java.io.Closeable

/**
 * Represents a PDF form (interactive form) in a document.
 * 
 * This class provides high-level access to form fields and operations.
 * Create instances using PdfDocument.initForm().
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
     * Get all form fields on a specific page.
     * 
     * NOTE: Remember to close form fields when done using page.closeFormFields() 
     * to prevent memory leaks.
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
     * NOTE: Remember to close the form field when done using page.closeFormField() 
     * to prevent memory leaks.
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
     * Set the selection state of a specific option in a combo box or list box field.
     * 
     * @param page The page containing the form field
     * @param field The form field to update
     * @param optionIndex The index of the option to select/deselect
     * @param selected True to select, false to deselect
     * @return True if successful
     */
    fun setFormFieldOptionSelection(
        page: PdfPage,
        field: FormField,
        optionIndex: Int,
        selected: Boolean
    ): Boolean {
        checkNotClosed()
        if (field.annotPtr == 0L) return false
        return core.setFormFieldOptionSelection(formPtr, page.getPointer(), field.annotPtr, optionIndex, selected)
    }
    
    /**
     * Get the indices of all selected options in a combo box or list box field.
     * 
     * @param page The page containing the form field
     * @param field The form field to query
     * @return List of selected option indices
     */
    fun getFormFieldSelectedOptions(page: PdfPage, field: FormField): List<Int> {
        checkNotClosed()
        val options = getFieldOptions(page, field)
        return options.filter { it.isSelected }.map { it.index }
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
    
    // --- Form Data Export/Import ---
    
    /**
     * Export all form data as a snapshot.
     *
     * @return FormDataSnapshot containing all form fields
     */
    fun exportFormData(): FormDataSnapshot {
        checkNotClosed()
        val fields = mutableListOf<FormFieldData>()
        
        // Iterate through all pages to collect form fields
        val pageCount = core.getPageCount(docPtr)
        for (pageIndex in 0 until pageCount) {
            val pagePtr = core.loadPage(docPtr, pageIndex)
            if (pagePtr != 0L) {
                try {
                    val fieldCount = core.getFormFieldCount(formPtr, pagePtr)
                    for (i in 0 until fieldCount) {
                        val annotPtr = core.getFormFieldAtIndex(formPtr, pagePtr, i)
                        if (annotPtr != 0L) {
                            val name = core.getFormFieldName(formPtr, annotPtr) ?: ""
                            val type = FormFieldType.fromValue(core.getFormFieldType(formPtr, annotPtr))
                            val value = core.getFormFieldValue(formPtr, annotPtr) ?: ""
                            val defaultValue = core.getFormFieldDefaultValue(formPtr, annotPtr) ?: ""
                            val isRequired = core.isFormFieldRequired(formPtr, annotPtr)
                            val isReadOnly = core.isFormFieldReadOnly(formPtr, annotPtr)
                            val maxLength = core.getFormFieldMaxLength(formPtr, annotPtr)
                            
                            // Get options for combo box and list box fields
                            val options = if (type == FormFieldType.COMBOBOX || type == FormFieldType.LISTBOX) {
                                val optionCount = core.getFormFieldOptionCount(formPtr, annotPtr)
                                (0 until optionCount).mapNotNull { idx ->
                                    val label = core.getFormFieldOptionLabel(formPtr, annotPtr, idx)
                                    val optValue = core.getFormFieldOptionValue(formPtr, annotPtr, idx)
                                    val isSelected = core.isFormFieldOptionSelected(formPtr, annotPtr, idx)
                                    if (label != null && optValue != null) {
                                        FormFieldOption(label, optValue, isSelected, idx)
                                    } else null
                                }
                            } else {
                                emptyList()
                            }
                            
                            fields.add(
                                FormFieldData(
                                    name = name,
                                    type = type,
                                    value = value,
                                    defaultValue = defaultValue,
                                    isRequired = isRequired,
                                    isReadOnly = isReadOnly,
                                    maxLength = maxLength,
                                    options = options
                                )
                            )
                        }
                    }
                } finally {
                    core.closePage(pagePtr)
                }
            }
        }
        
        val formType = core.getFormType(docPtr)
        return FormDataSnapshot(formType, fields)
    }
    
    /**
     * Export form data as JSON string.
     *
     * @return JSON string representation of form data
     */
    fun exportAsJson(): String {
        checkNotClosed()
        return exportFormData().toJson()
    }
    
    /**
     * Import form data from JSON string.
     *
     * @param json JSON string containing form data
     * @return True if import was successful
     */
    fun importFromJson(json: String): Boolean {
        checkNotClosed()
        return try {
            val snapshot = FormDataSnapshot.fromJson(json)
            restoreFromSnapshot(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Create a snapshot of current form data.
     *
     * @return FormDataSnapshot
     */
    fun createFormDataSnapshot(): FormDataSnapshot {
        return exportFormData()
    }
    
    /**
     * Restore form data from a snapshot.
     *
     * @param snapshot FormDataSnapshot to restore
     * @return True if restoration was successful
     */
    fun restoreFromSnapshot(snapshot: FormDataSnapshot): Boolean {
        checkNotClosed()
        
        try {
            val pageCount = core.getPageCount(docPtr)
            for (pageIndex in 0 until pageCount) {
                val pagePtr = core.loadPage(docPtr, pageIndex)
                if (pagePtr != 0L) {
                    try {
                        val fieldCount = core.getFormFieldCount(formPtr, pagePtr)
                        for (i in 0 until fieldCount) {
                            val annotPtr = core.getFormFieldAtIndex(formPtr, pagePtr, i)
                            if (annotPtr != 0L) {
                                val name = core.getFormFieldName(formPtr, annotPtr)
                                val fieldData = snapshot.fields.find { it.name == name }
                                
                                if (fieldData != null) {
                                    // Set the field value
                                    core.setFormFieldValue(formPtr, pagePtr, annotPtr, fieldData.value)
                                    
                                    // For combo box and list box, set selected options
                                    if (fieldData.type == FormFieldType.COMBOBOX || 
                                        fieldData.type == FormFieldType.LISTBOX) {
                                        fieldData.options.forEach { option ->
                                            core.setFormFieldOptionSelection(
                                                formPtr, pagePtr, annotPtr, 
                                                option.index, option.isSelected
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } finally {
                        core.closePage(pagePtr)
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    // --- Appearance Settings ---
    
    /**
     * Set form field highlight color.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @param a Alpha component (0-255)
     */
    fun setFormFieldHighlightColor(r: Int, g: Int, b: Int, a: Int) {
        checkNotClosed()
        core.setFormFieldHighlightColor(formPtr, r, g, b, a)
    }
    
    /**
     * Set form field highlight alpha (opacity).
     *
     * @param alpha Alpha value (0-255, where 255 is fully opaque)
     */
    fun setFormFieldHighlightAlpha(alpha: Int) {
        checkNotClosed()
        core.setFormFieldHighlightAlpha(formPtr, alpha)
    }
    
    /**
     * Remove form field highlight.
     */
    fun removeFormFieldHighlight() {
        checkNotClosed()
        core.removeFormFieldHighlight(formPtr)
    }
    
    /**
     * Get the form type.
     *
     * @return Form type value
     */
    fun getFormType(): Int {
        checkNotClosed()
        return core.getFormType(docPtr)
    }
    
    private fun checkNotClosed() {
        check(!isClosed) { "Form has been closed" }
    }
}
