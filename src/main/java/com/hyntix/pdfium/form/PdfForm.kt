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
