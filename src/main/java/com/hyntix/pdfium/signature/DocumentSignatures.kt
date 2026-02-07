package com.hyntix.pdfium.signature

import android.graphics.RectF
import com.hyntix.pdfium.PdfiumCore

/**
 * Provides access to signature information in a PDF document.
 *
 * @property core The PdfiumCore instance for native operations
 * @property docPtr The native document pointer
 */
class DocumentSignatures(
    private val core: PdfiumCore,
    private val docPtr: Long
) {
    
    /**
     * Get the total number of signatures in the document.
     *
     * @return Number of signatures, or -1 on error
     */
    fun getSignatureCount(): Int {
        return core.getSignatureCount(docPtr)
    }
    
    /**
     * Get all signatures in the document.
     *
     * @return List of signature fields
     */
    fun getSignatures(): List<SignatureField> {
        val count = getSignatureCount()
        if (count <= 0) return emptyList()
        
        val signatures = mutableListOf<SignatureField>()
        for (i in 0 until count) {
            getSignature(i)?.let { signatures.add(it) }
        }
        
        return signatures
    }
    
    /**
     * Get a specific signature by index.
     *
     * @param index The 0-based index of the signature
     * @return The signature field, or null if not found
     */
    fun getSignature(index: Int): SignatureField? {
        val sigPtr = core.getSignatureAtIndex(docPtr, index)
        if (sigPtr == 0L) return null
        
        // Get signature details using PDFium signature APIs
        val reason = core.getSignatureReason(sigPtr) ?: ""
        val time = core.getSignatureTime(sigPtr) ?: ""
        
        // Since we don't have location and certificate info from PDFium,
        // we return empty strings for those fields
        return SignatureField(
            name = "Signature${index + 1}",
            rect = RectF(0f, 0f, 0f, 0f), // PDFium doesn't provide rect for signature objects
            status = SignatureStatus.SIGNED, // Assume signed if signature object exists
            reason = reason,
            location = "",
            signDate = time,
            certificateInfo = ""
        )
    }
    
    /**
     * Check if the document has any signatures.
     *
     * @return True if the document is signed
     */
    fun isDocumentSigned(): Boolean {
        return getSignatureCount() > 0
    }
    
    /**
     * Check if any signature indicates the document has been modified.
     *
     * @return True if any signature shows modification
     */
    fun isDocumentModified(): Boolean {
        // PDFium doesn't provide signature validation, so we return false
        // A proper implementation would need to verify signatures
        return false
    }
}
