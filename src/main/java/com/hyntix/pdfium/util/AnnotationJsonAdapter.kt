package com.hyntix.pdfium.util

import com.hyntix.pdfium.annotation.PdfAnnotation
import org.json.JSONObject

/**
 * JSON adapter for annotation serialization and deserialization.
 */
object AnnotationJsonAdapter {
    
    /**
     * Serialize annotation to JSON string.
     *
     * Note: This is a basic implementation that serializes basic annotation properties.
     * Complex annotation types may require additional serialization logic.
     *
     * @param annotation The annotation to serialize
     * @return JSON string representation
     */
    fun serialize(annotation: PdfAnnotation): String {
        val json = JSONObject().apply {
            put("type", annotation.type.value)
            put("pageIndex", annotation.pageIndex)
            val rect = annotation.rect
            put("rect", JSONObject().apply {
                put("left", rect.left)
                put("top", rect.top)
                put("right", rect.right)
                put("bottom", rect.bottom)
            })
            put("contents", annotation.contents)
        }
        return json.toString(2)
    }
    
    /**
     * Deserialize annotation from JSON string.
     *
     * Note: This creates a basic PdfAnnotation object. Full deserialization
     * would require recreating the annotation in the PDF using PDFium APIs.
     *
     * @param json The JSON string to deserialize
     * @return PdfAnnotation instance (note: this is not a full reconstruction)
     * @throws org.json.JSONException if the JSON is malformed
     */
    fun deserialize(json: String): PdfAnnotation {
        val jsonObject = JSONObject(json)
        // This is a placeholder - actual deserialization would need to
        // recreate the annotation in a PDF document
        throw UnsupportedOperationException(
            "Annotation deserialization requires a document context. " +
            "Use PdfPage.addAnnotation() methods to create annotations."
        )
    }
}
