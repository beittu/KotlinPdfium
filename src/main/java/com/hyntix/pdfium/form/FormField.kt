package com.hyntix.pdfium.form

import android.graphics.RectF

/**
 * Represents a form field in a PDF document.
 * 
 * @property name The fully qualified name of the form field
 * @property type The type of form field (text, checkbox, radio button, etc.)
 * @property value The current value of the form field
 * @property pageIndex The 0-based index of the page containing this field
 * @property rect The bounding rectangle of the field in page coordinates
 * @property annotPtr Internal pointer to the annotation object
 */
data class FormField(
    val name: String,
    val type: FormFieldType,
    val value: String,
    val pageIndex: Int,
    val rect: RectF,
    internal val annotPtr: Long = 0L
) {
    /**
     * Check if this is a text field.
     */
    fun isTextField(): Boolean = type == FormFieldType.TEXTFIELD
    
    /**
     * Check if this is a checkbox.
     */
    fun isCheckbox(): Boolean = type == FormFieldType.CHECKBOX
    
    /**
     * Check if this is a radio button.
     */
    fun isRadioButton(): Boolean = type == FormFieldType.RADIOBUTTON
    
    /**
     * Check if this is a combo box.
     */
    fun isComboBox(): Boolean = type == FormFieldType.COMBOBOX
    
    /**
     * Check if this is a list box.
     */
    fun isListBox(): Boolean = type == FormFieldType.LISTBOX
    
    /**
     * Check if this field supports multiple options (combo box or list box).
     */
    fun hasOptions(): Boolean = isComboBox() || isListBox()
    
    /**
     * Check if this is a push button.
     */
    fun isPushButton(): Boolean = type == FormFieldType.PUSHBUTTON
    
    /**
     * Check if this is a signature field.
     */
    fun isSignature(): Boolean = type == FormFieldType.SIGNATURE
}

/**
 * Represents an option in a combo box or list box form field.
 * 
 * @property label The display label of the option
 * @property value The export value of the option (may be different from label)
 * @property isSelected Whether this option is currently selected (read-only)
 * @property index The 0-based index of this option in the field
 */
data class FormFieldOption(
    val label: String,
    val value: String,
    val isSelected: Boolean = false,
    val index: Int = 0
)
