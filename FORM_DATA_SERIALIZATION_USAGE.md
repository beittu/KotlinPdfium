# Form Data Serialization Usage Guide

This guide demonstrates how to use the comprehensive form data serialization, XFA support, signature handling, and appearance stream management features in KotlinPdfium.

## Table of Contents
- [Form Data Export/Import](#form-data-exportimport)
- [Form Validation](#form-validation)
- [Signature Field Detection](#signature-field-detection)
- [XFA Form Support](#xfa-form-support)
- [Form Field Highlighting](#form-field-highlighting)
- [Advanced Usage](#advanced-usage)

## Form Data Export/Import

### Export Form Data to JSON

```kotlin
import com.hyntix.pdfium.PdfiumCore

val core = PdfiumCore()
core.initLibrary()

// Open document with forms
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    // Export all form data as JSON
    val json = doc.exportFormDataAsJson()
    
    // Save to file
    if (json != null) {
        File("/path/to/form-data.json").writeText(json)
        println("Form data exported successfully")
    }
}
```

### Import Form Data from JSON

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    // Read JSON from file
    val json = File("/path/to/form-data.json").readText()
    
    // Import form data
    val success = doc.importFormData(json)
    if (success) {
        println("Form data imported successfully")
        
        // Save the modified document
        doc.saveAs("/path/to/filled-form.pdf")
    }
}
```

### Using FormDataSnapshot Directly

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    // Get form data snapshot
    val snapshot = doc.getFormData()
    
    if (snapshot != null) {
        println("Form type: ${snapshot.formType}")
        println("Field count: ${snapshot.fields.size}")
        
        // Iterate through fields
        snapshot.fields.forEach { field ->
            println("Field: ${field.name}")
            println("  Type: ${field.type}")
            println("  Value: ${field.value}")
            println("  Required: ${field.isRequired}")
            println("  Read-only: ${field.isReadOnly}")
        }
        
        // Export to JSON
        val json = snapshot.toJson()
        
        // Import from another snapshot
        val newSnapshot = FormDataSnapshot.fromJson(json)
        doc.importFormData(newSnapshot)
    }
}
```

### Working with Form Field Options

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    val snapshot = doc.getFormData()
    
    // Find combo box or list box fields
    snapshot?.fields?.forEach { field ->
        if (field.type == FormFieldType.COMBOBOX || 
            field.type == FormFieldType.LISTBOX) {
            
            println("Field: ${field.name}")
            println("  Options:")
            field.options.forEach { option ->
                println("    ${option.label} = ${option.value}")
                println("      Selected: ${option.isSelected}")
            }
        }
    }
}
```

## Form Validation

### Validate All Form Fields

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    // Validate all form fields
    val result = doc.validateFormData()
    
    if (!result.isValid) {
        println("Form has validation errors:")
        result.fieldErrors.forEach { (fieldName, errors) ->
            println("  $fieldName:")
            errors.forEach { error ->
                println("    - $error")
            }
        }
    } else {
        println("Form is valid!")
    }
}
```

### Validate Specific Field

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    // Validate a specific field
    val result = doc.validateFormField("firstname")
    
    if (!result.isValid) {
        println("Field '${result.fieldName}' has errors:")
        result.errors.forEach { error ->
            println("  - $error")
        }
    }
}
```

### Custom Validation with FormValidator

```kotlin
import com.hyntix.pdfium.form.FormValidator

val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    val snapshot = doc.getFormData()
    
    if (snapshot != null) {
        val validator = FormValidator(snapshot)
        
        // Check if there are any errors
        if (validator.hasErrors()) {
            // Get all error messages
            val errors = validator.getErrors()
            errors.forEach { error ->
                println(error)
            }
        }
        
        // Validate all fields
        val result = validator.validate()
        println("Form is valid: ${result.isValid}")
    }
}
```

## Signature Field Detection

### Get All Signatures in Document

```kotlin
val document = core.openDocument("/path/to/signed.pdf")
document?.use { doc ->
    val signatures = doc.getSignatures()
    
    if (signatures.isDocumentSigned()) {
        println("Document is signed")
        println("Number of signatures: ${signatures.getSignatureCount()}")
        
        // Get all signatures
        signatures.getSignatures().forEach { sig ->
            println("\nSignature: ${sig.name}")
            println("  Status: ${sig.status}")
            println("  Reason: ${sig.reason}")
            println("  Location: ${sig.location}")
            println("  Date: ${sig.signDate}")
            println("  Is signed: ${sig.isSigned()}")
            println("  Is modified: ${sig.isModified()}")
        }
    }
}
```

### Get Signatures on Specific Page

```kotlin
val document = core.openDocument("/path/to/signed.pdf")
document?.use { doc ->
    doc.openPage(0).use { page ->
        // Get all signature fields on this page
        val signatures = page.getSignatureFields()
        
        println("Signatures on page 0: ${signatures.size}")
        signatures.forEach { sig ->
            println("  ${sig.name}: ${sig.status}")
        }
        
        // Get overall signature status for page
        val pageStatus = page.getSignatureStatus()
        println("Page signature status: $pageStatus")
    }
}
```

### Check Signature Status

```kotlin
import com.hyntix.pdfium.signature.SignatureStatus

val document = core.openDocument("/path/to/signed.pdf")
document?.use { doc ->
    val signatures = doc.getSignatures()
    
    signatures.getSignatures().forEach { sig ->
        when (sig.status) {
            SignatureStatus.UNSIGNED -> 
                println("${sig.name}: Not signed")
            SignatureStatus.SIGNED -> 
                println("${sig.name}: Signed and valid")
            SignatureStatus.MODIFIED -> 
                println("${sig.name}: Signed but document modified")
            SignatureStatus.ERROR -> 
                println("${sig.name}: Error checking signature")
        }
    }
}
```

## XFA Form Support

### Check for XFA Forms

```kotlin
val document = core.openDocument("/path/to/xfa-form.pdf")
document?.use { doc ->
    if (doc.hasXFAForms()) {
        println("Document contains XFA forms")
        
        val xfaForms = doc.getXFAForms()
        xfaForms?.let { xfa ->
            println("XFA packet count: ${xfa.getPacketCount()}")
        }
    } else {
        println("Document does not contain XFA forms")
    }
}
```

### Extract XFA Packets

```kotlin
val document = core.openDocument("/path/to/xfa-form.pdf")
document?.use { doc ->
    val xfaForms = doc.getXFAForms()
    
    xfaForms?.let { xfa ->
        // Get all packets
        val packets = xfa.getPackets()
        
        packets.forEach { packet ->
            println("Packet: ${packet.name}")
            println("  Size: ${packet.content.size} bytes")
            
            // Get content as string (for XML packets)
            val xmlContent = packet.getContentAsString()
            println("  Content preview: ${xmlContent.take(100)}...")
        }
    }
}
```

### Get Specific XFA Packet by Name

```kotlin
val document = core.openDocument("/path/to/xfa-form.pdf")
document?.use { doc ->
    val xfaForms = doc.getXFAForms()
    
    xfaForms?.let { xfa ->
        // Get specific packet by name
        val templatePacket = xfa.getPacketByName("template")
        
        if (templatePacket != null) {
            println("Found template packet")
            val xml = templatePacket.getContentAsString()
            // Process XML...
        }
    }
}
```

### Export XFA Data as XML

```kotlin
val document = core.openDocument("/path/to/xfa-form.pdf")
document?.use { doc ->
    val xfaForms = doc.getXFAForms()
    
    xfaForms?.let { xfa ->
        // Export all XFA packets as combined XML
        val xml = xfa.exportXML()
        
        // Save to file
        File("/path/to/xfa-data.xml").writeText(xml)
        println("XFA data exported")
    }
}
```

## Form Field Highlighting

### Set Form Field Highlight Color

```kotlin
val document = core.openDocument("/path/to/form.pdf")
document?.use { doc ->
    val form = doc.initForm()
    
    form?.use { pdfForm ->
        // Set highlight color (RGB + Alpha)
        // Red with 50% transparency
        pdfForm.setFormFieldHighlightColor(
            r = 255,
            g = 0,
            b = 0,
            a = 128
        )
        
        // Now render pages to see highlighted fields
        doc.openPage(0).use { page ->
            val bitmap = Bitmap.createBitmap(
                page.width.toInt(),
                page.height.toInt(),
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, renderAnnot = true)
            // Form fields will be highlighted in red
        }
    }
}
```

### Adjust Highlight Transparency

```kotlin
val form = doc.initForm()
form?.use { pdfForm ->
    // Set highlight color first
    pdfForm.setFormFieldHighlightColor(0, 255, 0, 128) // Green
    
    // Adjust alpha (transparency)
    pdfForm.setFormFieldHighlightAlpha(200) // More opaque
    
    // Remove highlight completely
    pdfForm.removeFormFieldHighlight()
}
```

## Advanced Usage

### Complete Form Processing Pipeline

```kotlin
import com.hyntix.pdfium.util.FormDataExporter

fun processForm(inputPath: String, outputPath: String, dataPath: String) {
    val core = PdfiumCore()
    core.initLibrary()
    
    try {
        val document = core.openDocument(inputPath)
        document?.use { doc ->
            // 1. Export form data
            val snapshot = doc.getFormData()
            snapshot?.let { data ->
                FormDataExporter.exportToFile(data, dataPath)
                println("Form data exported to $dataPath")
            }
            
            // 2. Validate form
            val validation = doc.validateFormData()
            if (!validation.isValid) {
                println("Validation errors found:")
                validation.fieldErrors.forEach { (field, errors) ->
                    println("  $field: ${errors.joinToString()}")
                }
                return
            }
            
            // 3. Check signatures
            val signatures = doc.getSignatures()
            if (signatures.isDocumentSigned()) {
                println("Document has ${signatures.getSignatureCount()} signatures")
            }
            
            // 4. Check for XFA
            if (doc.hasXFAForms()) {
                println("Document contains XFA forms")
                val xfa = doc.getXFAForms()
                xfa?.let { 
                    val xml = it.exportXML()
                    File("$dataPath.xfa.xml").writeText(xml)
                }
            }
            
            // 5. Save processed document
            doc.saveAs(outputPath)
            println("Document saved to $outputPath")
        }
    } finally {
        core.destroyLibrary()
    }
}
```

### Batch Form Data Processing

```kotlin
import com.hyntix.pdfium.util.FormDataJsonAdapter

fun mergeForms(pdfPaths: List<String>): FormDataSnapshot? {
    val core = PdfiumCore()
    core.initLibrary()
    
    val allFields = mutableListOf<FormFieldData>()
    var formType = 0
    
    try {
        pdfPaths.forEach { path ->
            val document = core.openDocument(path)
            document?.use { doc ->
                val snapshot = doc.getFormData()
                snapshot?.let {
                    formType = it.formType
                    allFields.addAll(it.fields)
                }
            }
        }
        
        return FormDataSnapshot(formType, allFields)
    } finally {
        core.destroyLibrary()
    }
}
```

### Form Data Comparison

```kotlin
fun compareFormData(json1: String, json2: String): Map<String, Pair<String, String>> {
    val snapshot1 = FormDataSnapshot.fromJson(json1)
    val snapshot2 = FormDataSnapshot.fromJson(json2)
    
    val differences = mutableMapOf<String, Pair<String, String>>()
    
    snapshot1.fields.forEach { field1 ->
        val field2 = snapshot2.fields.find { it.name == field1.name }
        if (field2 != null && field1.value != field2.value) {
            differences[field1.name] = Pair(field1.value, field2.value)
        }
    }
    
    return differences
}
```

## Error Handling

### Robust Form Processing

```kotlin
fun safeFormProcessing(path: String) {
    val core = PdfiumCore()
    core.initLibrary()
    
    try {
        val document = core.openDocument(path)
        if (document == null) {
            println("Failed to open document")
            return
        }
        
        document.use { doc ->
            try {
                // Export with error handling
                val json = doc.exportFormDataAsJson()
                if (json == null) {
                    println("Document has no forms")
                    return
                }
                
                // Validate with error handling
                val validation = doc.validateFormData()
                if (!validation.isValid) {
                    // Handle validation errors
                    println("Validation failed")
                }
                
            } catch (e: Exception) {
                println("Error processing form: ${e.message}")
            }
        }
    } catch (e: Exception) {
        println("Error opening document: ${e.message}")
    } finally {
        core.destroyLibrary()
    }
}
```

## Best Practices

1. **Always use `.use { }` blocks** to ensure resources are properly closed
2. **Validate form data** before processing to catch errors early
3. **Handle null returns** from API methods gracefully
4. **Export form data before modifications** to maintain backups
5. **Check for XFA forms** separately as they have different handling requirements
6. **Use FormDataExporter** for file-based operations to simplify I/O
7. **Verify signature status** after opening signed documents
8. **Clean up PdfiumCore** when done to free native resources

## See Also

- [FORM_FIELDS_USAGE.md](FORM_FIELDS_USAGE.md) - Basic form field operations
- [ANNOTATION_USAGE.md](ANNOTATION_USAGE.md) - Annotation handling
- [TASK3_IMPLEMENTATION_SUMMARY.md](TASK3_IMPLEMENTATION_SUMMARY.md) - Implementation details
- [README.md](README.md) - General library overview
