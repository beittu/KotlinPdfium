package com.hyntix.pdfium.util

import com.hyntix.pdfium.form.FormDataSnapshot
import java.io.File

/**
 * Utility for exporting and importing form data to/from files.
 */
object FormDataExporter {
    
    /**
     * Export form data snapshot to a JSON file.
     *
     * @param data The form data snapshot to export
     * @param filePath The path where the JSON file should be saved
     * @return True if export was successful, false otherwise
     */
    fun exportToFile(data: FormDataSnapshot, filePath: String): Boolean {
        return try {
            val json = exportToJson(data)
            File(filePath).writeText(json)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Import form data snapshot from a JSON file.
     *
     * @param filePath The path to the JSON file to import
     * @return FormDataSnapshot if successful, null otherwise
     */
    fun importFromFile(filePath: String): FormDataSnapshot? {
        return try {
            val json = File(filePath).readText()
            importFromJson(json)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Export form data snapshot to a JSON string.
     *
     * @param data The form data snapshot to export
     * @return JSON string representation
     */
    fun exportToJson(data: FormDataSnapshot): String {
        return FormDataJsonAdapter.serialize(data)
    }
    
    /**
     * Import form data snapshot from a JSON string.
     *
     * @param json The JSON string to import
     * @return FormDataSnapshot if successful, null otherwise
     */
    fun importFromJson(json: String): FormDataSnapshot? {
        return try {
            FormDataJsonAdapter.deserialize(json)
        } catch (e: Exception) {
            null
        }
    }
}
