package com.hyntix.pdfium.signature

/**
 * Status of a signature field.
 *
 * @property value Integer value as used by PDFium
 */
enum class SignatureStatus(val value: Int) {
    /** Signature field is not signed */
    UNSIGNED(0),
    
    /** Signature field is signed and valid */
    SIGNED(1),
    
    /** Signature field is signed but document has been modified */
    MODIFIED(2),
    
    /** Error checking signature status */
    ERROR(3);
    
    companion object {
        /**
         * Convert integer value to SignatureStatus.
         */
        fun fromValue(value: Int): SignatureStatus {
            return entries.find { it.value == value } ?: ERROR
        }
    }
}
