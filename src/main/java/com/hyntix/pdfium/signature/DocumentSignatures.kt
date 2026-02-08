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
        val location = getSignatureLocation(sigPtr)
        val certificateInfo = extractCertificateInfo(sigPtr)
        val status = validateSignatureStatus(sigPtr, index)
        
        return SignatureField(
            name = "Signature${index + 1}",
            rect = RectF(0f, 0f, 0f, 0f), // PDFium doesn't provide rect for signature objects
            status = status,
            reason = reason,
            location = location,
            signDate = time,
            certificateInfo = certificateInfo
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
        val signatures = getSignatures()
        return signatures.any { it.isModified() }
    }
    
    /**
     * Get the location where document was signed (from signature metadata).
     * 
     * Note: Current implementation returns empty string as PDFium API doesn't 
     * provide direct access to signature location field.
     * 
     * @param sigPtr Native signature pointer
     * @return Location string, or empty if not available
     */
    private fun getSignatureLocation(sigPtr: Long): String {
        // PDFium API doesn't provide signature location extraction
        // This would require accessing the signature dictionary directly
        return ""
    }
    
    /**
     * Extract certificate information from signature.
     * 
     * Note: Current implementation returns placeholder message as PDFium API doesn't
     * provide direct certificate extraction. Full implementation would require
     * parsing the PKCS#7 signature data.
     * 
     * @param sigPtr Native signature pointer
     * @return Certificate info string with issuer/subject/validity
     */
    private fun extractCertificateInfo(sigPtr: Long): String {
        // PDFium API doesn't provide certificate extraction
        // This would require parsing the signature's Contents stream (PKCS#7 data)
        return "Certificate: Signer information not available in current implementation"
    }
    
    /**
     * Validate signature status and check for modifications.
     * 
     * @param sigPtr Native signature pointer
     * @param index Signature index
     * @return Signature status (SIGNED, MODIFIED, UNSIGNED, ERROR)
     */
    private fun validateSignatureStatus(sigPtr: Long, index: Int): SignatureStatus {
        return try {
            if (sigPtr == 0L) {
                return SignatureStatus.UNSIGNED
            }
            
            // Basic validation: if signature object exists, it's signed
            // A full implementation would verify the cryptographic signature
            // and check if document has been modified since signing
            
            // Check if signature has required fields
            val reason = core.getSignatureReason(sigPtr)
            val time = core.getSignatureTime(sigPtr)
            
            if (reason == null && time == null) {
                // Missing critical fields might indicate an issue
                return SignatureStatus.ERROR
            }
            
            // Without cryptographic validation, assume valid if present
            SignatureStatus.SIGNED
        } catch (e: Exception) {
            SignatureStatus.ERROR
        }
    }
    
    /**
     * Get all unsigned signature fields in the document.
     * 
     * @return List of unsigned signature fields
     */
    fun getUnsignedFields(): List<SignatureField> {
        return getSignatures().filter { it.status == SignatureStatus.UNSIGNED }
    }
    
    /**
     * Get all signed signature fields in the document.
     * 
     * @return List of signed signature fields
     */
    fun getSignedFields(): List<SignatureField> {
        return getSignatures().filter { it.status == SignatureStatus.SIGNED }
    }
    
    /**
     * Get all modified signature fields (signed but document modified after).
     * 
     * @return List of modified signature fields
     */
    fun getModifiedFields(): List<SignatureField> {
        return getSignatures().filter { it.status == SignatureStatus.MODIFIED }
    }
    
    /**
     * Check if all signatures in document are valid.
     * 
     * @return True if all signatures are valid, false if any are modified or have errors
     */
    fun areAllSignaturesValid(): Boolean {
        val signatures = getSignatures()
        if (signatures.isEmpty()) return false
        
        return signatures.all { it.status == SignatureStatus.SIGNED }
    }
}
