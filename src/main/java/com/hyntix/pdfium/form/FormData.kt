package com.hyntix.pdfium.form

import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents the data of a single form field.
 *
 * @property name The fully qualified name of the form field
 * @property type The type of form field
 * @property value The current value of the form field
 * @property defaultValue The default value of the form field
 * @property isRequired Whether the field is required
 * @property isReadOnly Whether the field is read-only
 * @property maxLength Maximum length for text fields (-1 if not applicable)
 * @property options List of options for combo box or list box fields
 */
data class FormFieldData(
    val name: String,
    val type: FormFieldType,
    val value: String,
    val defaultValue: String,
    val isRequired: Boolean,
    val isReadOnly: Boolean,
    val maxLength: Int = -1,
    val options: List<FormFieldOption> = emptyList()
) {
    /**
     * Convert to JSON object for serialization.
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("type", type.value)
            put("value", value)
            put("defaultValue", defaultValue)
            put("isRequired", isRequired)
            put("isReadOnly", isReadOnly)
            put("maxLength", maxLength)
            if (options.isNotEmpty()) {
                put("options", JSONArray().apply {
                    options.forEach { option ->
                        put(JSONObject().apply {
                            put("label", option.label)
                            put("value", option.value)
                            put("isSelected", option.isSelected)
                            put("index", option.index)
                        })
                    }
                })
            }
        }
    }

    companion object {
        /**
         * Create from JSON object.
         */
        fun fromJson(json: JSONObject): FormFieldData {
            val optionsArray = json.optJSONArray("options")
            val options = mutableListOf<FormFieldOption>()
            
            if (optionsArray != null) {
                for (i in 0 until optionsArray.length()) {
                    val optionJson = optionsArray.getJSONObject(i)
                    options.add(
                        FormFieldOption(
                            label = optionJson.getString("label"),
                            value = optionJson.getString("value"),
                            isSelected = optionJson.getBoolean("isSelected"),
                            index = optionJson.getInt("index")
                        )
                    )
                }
            }
            
            return FormFieldData(
                name = json.getString("name"),
                type = FormFieldType.fromValue(json.getInt("type")),
                value = json.getString("value"),
                defaultValue = json.getString("defaultValue"),
                isRequired = json.getBoolean("isRequired"),
                isReadOnly = json.getBoolean("isReadOnly"),
                maxLength = json.optInt("maxLength", -1),
                options = options
            )
        }
    }
}

/**
 * Represents a snapshot of all form data in a document.
 *
 * @property formType The type of form (AcroForm, XFA, etc.)
 * @property fields List of all form fields
 * @property timestamp Timestamp when the snapshot was created
 */
data class FormDataSnapshot(
    val formType: Int,
    val fields: List<FormFieldData>,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Export to JSON string.
     */
    fun toJson(): String {
        val json = JSONObject().apply {
            put("formType", formType)
            put("timestamp", timestamp)
            put("fields", JSONArray().apply {
                fields.forEach { field ->
                    put(field.toJson())
                }
            })
        }
        return json.toString(2) // Pretty print with 2-space indent
    }

    companion object {
        /**
         * Import from JSON string.
         */
        fun fromJson(json: String): FormDataSnapshot {
            val jsonObject = JSONObject(json)
            val fieldsArray = jsonObject.getJSONArray("fields")
            val fields = mutableListOf<FormFieldData>()
            
            for (i in 0 until fieldsArray.length()) {
                fields.add(FormFieldData.fromJson(fieldsArray.getJSONObject(i)))
            }
            
            return FormDataSnapshot(
                formType = jsonObject.getInt("formType"),
                fields = fields,
                timestamp = jsonObject.optLong("timestamp", System.currentTimeMillis())
            )
        }
    }
}
