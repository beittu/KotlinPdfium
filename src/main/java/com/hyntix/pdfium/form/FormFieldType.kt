package com.hyntix.pdfium.form

/**
 * Form field types as defined in PDFium (fpdf_formfill.h).
 * 
 * These values match the FPDF_FORMFIELD_* constants from the PDFium library.
 */
enum class FormFieldType(val value: Int) {
    /** Unknown form field type */
    UNKNOWN(0),
    
    /** Push button */
    PUSHBUTTON(1),
    
    /** Checkbox */
    CHECKBOX(2),
    
    /** Radio button */
    RADIOBUTTON(3),
    
    /** Combo box (dropdown with editable text) */
    COMBOBOX(4),
    
    /** List box (dropdown selection) */
    LISTBOX(5),
    
    /** Text field */
    TEXTFIELD(6),
    
    /** Signature field */
    SIGNATURE(7);
    
    companion object {
        /**
         * Convert an integer value to FormFieldType.
         * Returns UNKNOWN if the value doesn't match any known type.
         */
        fun fromValue(value: Int): FormFieldType {
            return entries.firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}
