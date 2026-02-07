package com.hyntix.pdfium.util

import com.hyntix.pdfium.form.FormDataSnapshot

/**
 * JSON adapter for form data serialization and deserialization.
 */
object FormDataJsonAdapter {
    
    /**
     * Serialize form data snapshot to JSON string.
     *
     * @param data The form data snapshot to serialize
     * @return JSON string representation
     */
    fun serialize(data: FormDataSnapshot): String {
        return data.toJson()
    }
    
    /**
     * Deserialize form data snapshot from JSON string.
     *
     * @param json The JSON string to deserialize
     * @return FormDataSnapshot instance
     * @throws org.json.JSONException if the JSON is malformed
     */
    fun deserialize(json: String): FormDataSnapshot {
        return FormDataSnapshot.fromJson(json)
    }
}
