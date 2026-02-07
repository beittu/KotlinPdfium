# Form Field Operations - Usage Guide

This guide demonstrates how to use the form field enumeration and value operations in KotlinPdfium.

## Table of Contents
- [Overview](#overview)
- [Initializing Form Support](#initializing-form-support)
- [Reading Form Fields](#reading-form-fields)
- [Setting Form Field Values](#setting-form-field-values)
- [Working with Dropdown/Listbox Options](#working-with-dropdownlistbox-options)
- [Memory Management](#memory-management)
- [Complete Example](#complete-example)

## Overview

KotlinPdfium provides comprehensive support for PDF form fields (AcroForms). You can:
- Enumerate all form fields on a page
- Read form field values
- Set form field values
- Work with dropdown and listbox options
- Support all 8 form field types

## Initializing Form Support

Before working with form fields, you must initialize the form fill environment:

```kotlin
val pdfiumCore = PdfiumCore()
pdfiumCore.initLibrary()

// Open a PDF document
val document = pdfiumCore.openDocument("/path/to/form.pdf")

// Initialize form support
val form = document?.initForm()

if (form != null) {
    // Now you can work with form fields
}
```

## Reading Form Fields

### Get All Form Fields on a Page

```kotlin
val page = document.openPage(0)

// Get all form fields on this page
val fields = form.getFields(page)

for (field in fields) {
    println("Field: ${field.name}")
    println("Type: ${field.type}")
    println("Value: ${field.value}")
    println("Page: ${field.pageIndex}")
    println("Rect: ${field.rect}")
}

// Important: Close fields when done to prevent memory leaks
page.closeFormFields(fields)
page.close()
```

### Get a Specific Form Field by Name

```kotlin
val page = document.openPage(0)

// Find a specific field
val field = form.getField(page, "firstName")

if (field != null) {
    println("Field type: ${field.type}")
    println("Current value: ${field.value}")
    
    // Check field type
    when {
        field.isTextField() -> println("This is a text field")
        field.isCheckbox() -> println("This is a checkbox")
        field.isComboBox() -> println("This is a combo box")
    }
    
    // Important: Close field when done
    page.closeFormField(field)
}

page.close()
```

## Setting Form Field Values

### Set Value by Field Name

```kotlin
val page = document.openPage(0)

// Simple way - field is automatically closed
val success = page.setFormFieldValue(form.formPtr, "firstName", "John")

if (success) {
    println("Field value updated successfully")
}

page.close()
```

### Set Value by Field Object

```kotlin
val page = document.openPage(0)

val field = form.getField(page, "email")
if (field != null) {
    val success = form.setFieldValue(page, field, "john@example.com")
    
    if (success) {
        println("Email updated")
    }
    
    // Don't forget to close the field
    page.closeFormField(field)
}

page.close()
```

## Working with Dropdown/Listbox Options

For combo boxes and list boxes, you can enumerate and work with options:

```kotlin
val page = document.openPage(0)

val field = form.getField(page, "country")

if (field != null && field.hasOptions()) {
    // Get all options
    val options = form.getFieldOptions(page, field)
    
    println("Available options:")
    for (option in options) {
        println("  ${option.index}: ${option.label}")
        if (option.isSelected) {
            println("    ^ Currently selected")
        }
    }
    
    // Note: Setting individual options is not yet supported in PDFium
    // You can set the value directly instead
    form.setFieldValue(page, field, "United States")
    
    page.closeFormField(field)
}

page.close()
```

## Memory Management

**Important**: Form fields contain native annotation pointers that must be closed to prevent memory leaks.

### Best Practices

1. **Always close individual fields when done:**
```kotlin
val field = form.getField(page, "fieldName")
if (field != null) {
    // Use the field...
    page.closeFormField(field)
}
```

2. **Close multiple fields at once:**
```kotlin
val fields = form.getFields(page)
// Use the fields...
page.closeFormFields(fields)
```

3. **Convenience methods handle cleanup automatically:**
```kotlin
// This method closes the field automatically
page.setFormFieldValue(form.formPtr, "fieldName", "value")
```

4. **Always close the form when done:**
```kotlin
form.close()
```

## Complete Example

Here's a complete example that reads form fields, updates values, and properly manages memory:

```kotlin
import com.hyntix.pdfium.PdfiumCore
import com.hyntix.pdfium.form.FormFieldType

fun processFormFields(pdfPath: String) {
    val pdfiumCore = PdfiumCore()
    pdfiumCore.initLibrary()
    
    try {
        // Open document and initialize form
        val document = pdfiumCore.openDocument(pdfPath) 
            ?: throw IllegalStateException("Failed to open PDF")
            
        val form = document.initForm() 
            ?: throw IllegalStateException("Failed to initialize form")
        
        try {
            // Process first page
            val page = document.openPage(0)
            
            try {
                // Read all fields
                val fields = form.getFields(page)
                
                println("Found ${fields.size} form fields:")
                
                for (field in fields) {
                    println("\nField: ${field.name}")
                    println("  Type: ${field.type}")
                    println("  Value: ${field.value}")
                    
                    // Update specific fields
                    when (field.name) {
                        "firstName" -> {
                            form.setFieldValue(page, field, "John")
                        }
                        "lastName" -> {
                            form.setFieldValue(page, field, "Doe")
                        }
                        "email" -> {
                            form.setFieldValue(page, field, "john.doe@example.com")
                        }
                    }
                    
                    // Handle dropdowns
                    if (field.hasOptions()) {
                        val options = form.getFieldOptions(page, field)
                        println("  Options: ${options.map { it.label }}")
                    }
                }
                
                // Clean up fields
                page.closeFormFields(fields)
                
                // Save the modified document
                document.saveAs("/path/to/output.pdf")
                
            } finally {
                page.close()
            }
            
        } finally {
            form.close()
            document.close()
        }
        
    } finally {
        pdfiumCore.destroyLibrary()
    }
    
    println("\nForm processing complete!")
}
```

## Supported Form Field Types

The library supports all 8 PDF form field types:

| Type | Enum Value | Description |
|------|------------|-------------|
| Unknown | `UNKNOWN` | Unknown or unsupported type |
| Push Button | `PUSHBUTTON` | Button that triggers an action |
| Checkbox | `CHECKBOX` | Boolean checkbox field |
| Radio Button | `RADIOBUTTON` | Single selection from group |
| Combo Box | `COMBOBOX` | Dropdown with editable text |
| List Box | `LISTBOX` | Dropdown selection only |
| Text Field | `TEXTFIELD` | Single or multi-line text input |
| Signature | `SIGNATURE` | Digital signature field |

## Error Handling

Always check for null returns and handle errors appropriately:

```kotlin
val document = pdfiumCore.openDocument(path)
if (document == null) {
    val error = pdfiumCore.getLastError()
    println("Failed to open document: $error")
    return
}

val form = document.initForm()
if (form == null) {
    println("This PDF does not contain a form")
    document.close()
    return
}

// Continue processing...
```

## Performance Considerations

1. **Batch Operations**: When updating multiple fields, do all updates before closing fields
2. **Page Management**: Only open pages you need and close them when done
3. **Memory**: Always close form fields and the form handle to prevent leaks
4. **Direct Search**: Use `getField(page, name)` for single field lookup instead of iterating all fields

## Limitations

1. **No Undo/Redo**: PDFium doesn't provide undo/redo APIs for form operations
2. **Option Selection**: Individual option selection API is not available in PDFium; use direct value setting instead
3. **Read-Only**: Some form fields may be read-only and cannot be modified
4. **Annotations**: Not all annotations are form fields; the library filters appropriately

## Next Steps

- Explore the [API documentation](./src/main/java/com/hyntix/pdfium/form/)
- Check out the form field data classes: `FormField`, `FormFieldType`, `FormFieldOption`
- Review the `PdfForm`, `PdfPage`, and `PdfDocument` classes for available methods
