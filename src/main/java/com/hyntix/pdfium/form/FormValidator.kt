package com.hyntix.pdfium.form

/**
 * Validates form data according to field requirements.
 *
 * @property snapshot The form data snapshot to validate
 */
class FormValidator(private val snapshot: FormDataSnapshot) {
    
    /**
     * Validate all form fields.
     *
     * @return Validation result with field-level errors
     */
    fun validate(): FormValidationResult {
        val fieldErrors = mutableMapOf<String, MutableList<String>>()
        
        for (field in snapshot.fields) {
            val errors = validateFieldInternal(field)
            if (errors.isNotEmpty()) {
                fieldErrors[field.name] = errors
            }
        }
        
        return FormValidationResult(
            isValid = fieldErrors.isEmpty(),
            fieldErrors = fieldErrors
        )
    }
    
    /**
     * Validate a specific form field by name.
     *
     * @param fieldName The name of the field to validate
     * @return Field validation result
     */
    fun validateField(fieldName: String): FieldValidationResult {
        val field = snapshot.fields.find { it.name == fieldName }
            ?: return FieldValidationResult(
                fieldName = fieldName,
                isValid = false,
                errors = listOf("Field not found: $fieldName")
            )
        
        val errors = validateFieldInternal(field)
        return FieldValidationResult(
            fieldName = fieldName,
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    /**
     * Check if there are any validation errors.
     */
    fun hasErrors(): Boolean {
        return !validate().isValid
    }
    
    /**
     * Get all validation errors as a list of messages.
     */
    fun getErrors(): List<String> {
        val result = validate()
        val errorList = mutableListOf<String>()
        
        result.fieldErrors.forEach { (fieldName, errors) ->
            errors.forEach { error ->
                errorList.add("$fieldName: $error")
            }
        }
        
        return errorList
    }
    
    /**
     * Internal method to validate a single field.
     */
    private fun validateFieldInternal(field: FormFieldData): MutableList<String> {
        val errors = mutableListOf<String>()
        
        // Check required field
        if (field.isRequired && field.value.isEmpty()) {
            errors.add("This field is required")
        }
        
        // Check max length for text fields
        if (field.type == FormFieldType.TEXTFIELD && field.maxLength > 0) {
            if (field.value.length > field.maxLength) {
                errors.add("Value exceeds maximum length of ${field.maxLength}")
            }
        }
        
        // Additional validation can be added here for specific field types
        
        return errors
    }
}

/**
 * Result of validating all form fields.
 *
 * @property isValid True if all fields are valid
 * @property fieldErrors Map of field names to their error messages
 */
data class FormValidationResult(
    val isValid: Boolean,
    val fieldErrors: Map<String, List<String>>
)

/**
 * Result of validating a single form field.
 *
 * @property fieldName The name of the validated field
 * @property isValid True if the field is valid
 * @property errors List of error messages for this field
 */
data class FieldValidationResult(
    val fieldName: String,
    val isValid: Boolean,
    val errors: List<String>
)
