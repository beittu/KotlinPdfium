package com.hyntix.pdfium.signature

import android.graphics.RectF

/**
 * Represents a signature field in a PDF document.
 *
 * @property name The name of the signature field
 * @property rect The bounding rectangle of the signature field
 * @property status The status of the signature
 * @property reason The reason for signing (if available)
 * @property location The location where the document was signed (if available)
 * @property signDate The date/time when the document was signed (if available)
 * @property certificateInfo Information about the signing certificate (if available)
 */
data class SignatureField(
    val name: String,
    val rect: RectF,
    val status: SignatureStatus,
    val reason: String = "",
    val location: String = "",
    val signDate: String = "",
    val certificateInfo: String = ""
) {
    /**
     * Check if this signature field is signed.
     */
    fun isSigned(): Boolean = status == SignatureStatus.SIGNED
    
    /**
     * Check if the document has been modified after signing.
     */
    fun isModified(): Boolean = status == SignatureStatus.MODIFIED
}
