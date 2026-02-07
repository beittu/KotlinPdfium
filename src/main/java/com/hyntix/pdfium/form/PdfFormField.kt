// PdfFormField.kt

package com.hyntix.pdfium.form

// Base form field class
abstract class PdfFormField {
    abstract val name: String
    abstract val value: String
    abstract fun isValid(): Boolean
}

// Text form field class
class TextFormField(override val name: String, var textValue: String) : PdfFormField() {
    override val value: String
        get() = textValue

    override fun isValid(): Boolean {
        return textValue.isNotBlank()
    }
}

// Check box form field class
class CheckBoxFormField(override val name: String, var isChecked: Boolean) : PdfFormField() {
    override val value: String
        get() = if (isChecked) "Checked" else "Unchecked"

    override fun isValid(): Boolean {
        return true // Always valid since it’s a binary choice
    }
}

// Radio button form field class
class RadioButtonFormField(override val name: String, var options: List<String>, var selectedOption: String?) : PdfFormField() {
    override val value: String
        get() = selectedOption ?: "No selection"

    override fun isValid(): Boolean {
        return selectedOption in options
    }
}