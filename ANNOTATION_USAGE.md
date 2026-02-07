# Annotation Operations Usage Guide

This guide demonstrates how to use the advanced annotation operations and form field options in KotlinPdfium.

## Table of Contents
- [Creating Annotations](#creating-annotations)
- [Updating Annotations](#updating-annotations)
- [Removing Annotations](#removing-annotations)
- [Working with Form Field Options](#working-with-form-field-options)

## Creating Annotations

### Highlight Annotation

```kotlin
import com.hyntix.pdfium.annotation.AnnotationColor

// Define the highlighting region with quad points
// Each quad point set has 8 values: 4 points with x,y coordinates
val quadPoints = listOf(
    doubleArrayOf(100.0, 100.0, 200.0, 100.0, 100.0, 120.0, 200.0, 120.0)
)

// Create a highlight annotation with custom color
val success = page.createHighlightAnnotation(
    quadPoints = quadPoints,
    contents = "This is an important point",
    author = "John Doe",
    color = AnnotationColor(255, 255, 0, 128) // Yellow with 50% opacity
)
```

### Underline Annotation

```kotlin
val quadPoints = listOf(
    doubleArrayOf(100.0, 100.0, 200.0, 100.0, 100.0, 120.0, 200.0, 120.0)
)

val success = page.createUnderlineAnnotation(
    quadPoints = quadPoints,
    contents = "Key term",
    author = "Jane Smith",
    color = AnnotationColor(0, 0, 255, 128) // Blue with 50% opacity
)
```

### Strikeout Annotation

```kotlin
val quadPoints = listOf(
    doubleArrayOf(100.0, 100.0, 200.0, 100.0, 100.0, 120.0, 200.0, 120.0)
)

val success = page.createStrikeoutAnnotation(
    quadPoints = quadPoints,
    contents = "Removed text",
    author = "Editor",
    color = AnnotationColor(255, 0, 0, 128) // Red with 50% opacity
)
```

### Ink Annotation (Freehand Drawing)

```kotlin
import com.hyntix.pdfium.annotation.InkPath

// Define multiple stroke paths
val path1 = InkPath(
    points = listOf(
        doubleArrayOf(100.0, 100.0),
        doubleArrayOf(150.0, 150.0),
        doubleArrayOf(200.0, 100.0)
    )
)

val path2 = InkPath(
    points = listOf(
        doubleArrayOf(100.0, 120.0),
        doubleArrayOf(200.0, 120.0)
    )
)

val success = page.createInkAnnotation(
    inkList = listOf(path1, path2),
    contents = "Hand-drawn note",
    author = "Artist",
    color = AnnotationColor(0, 0, 0, 255) // Black
)
```

## Updating Annotations

### Update Annotation Contents

```kotlin
// Update the contents of the first annotation on the page
val success = page.updateAnnotationContents(
    index = 0,
    contents = "Updated comment text"
)
```

### Update Annotation Author

```kotlin
val success = page.updateAnnotationAuthor(
    index = 0,
    author = "New Author Name"
)
```

### Update Annotation Color

```kotlin
val newColor = AnnotationColor(0, 255, 0, 200) // Green with higher opacity
val success = page.updateAnnotationColor(
    index = 0,
    color = newColor
)
```

## Removing Annotations

```kotlin
// Remove the first annotation from the page
val success = page.removeAnnotation(index = 0)
```

## Working with Form Field Options

### Get All Options from a Dropdown/Listbox

```kotlin
// Assuming you have a PdfForm and PdfPage instance
val form = document.initForm()
val page = document.openPage(0)

// Get a form field
val field = page.getFormFieldByName(form.formPtr, "CountrySelection")

// Get all options
val options = form.getFieldOptions(page, field)

options.forEach { option ->
    println("Label: ${option.label}")
    println("Value: ${option.value}")
    println("Selected: ${option.isSelected}")
    println("Index: ${option.index}")
}

// Clean up
page.closeFormField(field)
```

### Select/Deselect Options

```kotlin
val form = document.initForm()
val page = document.openPage(0)
val field = page.getFormFieldByName(form.formPtr, "CountrySelection")

// Select the option at index 2
val success = form.setFormFieldOptionSelection(
    page = page,
    field = field,
    optionIndex = 2,
    selected = true
)

// Deselect the option at index 0
form.setFormFieldOptionSelection(
    page = page,
    field = field,
    optionIndex = 0,
    selected = false
)

page.closeFormField(field)
```

### Get Selected Options

```kotlin
val form = document.initForm()
val page = document.openPage(0)
val field = page.getFormFieldByName(form.formPtr, "CountrySelection")

// Get indices of all selected options
val selectedIndices = form.getFormFieldSelectedOptions(page, field)

println("Selected option indices: $selectedIndices")

page.closeFormField(field)
```

## Complete Example

```kotlin
import com.hyntix.pdfium.PdfiumCore
import com.hyntix.pdfium.annotation.AnnotationColor
import com.hyntix.pdfium.annotation.InkPath

fun demonstrateAnnotations() {
    val core = PdfiumCore()
    core.initLibrary()
    
    try {
        // Open document
        val document = core.openDocument("/path/to/document.pdf")
            ?: throw IllegalStateException("Failed to open document")
        
        document.use {
            val page = document.openPage(0)
            
            page.use {
                // Create a highlight annotation
                val quadPoints = listOf(
                    doubleArrayOf(100.0, 500.0, 300.0, 500.0, 100.0, 520.0, 300.0, 520.0)
                )
                
                page.createHighlightAnnotation(
                    quadPoints = quadPoints,
                    contents = "Important section",
                    author = "Reviewer",
                    color = AnnotationColor(255, 255, 0, 128)
                )
                
                // Create an ink annotation
                val inkPath = InkPath(
                    points = listOf(
                        doubleArrayOf(100.0, 400.0),
                        doubleArrayOf(150.0, 450.0),
                        doubleArrayOf(200.0, 400.0)
                    )
                )
                
                page.createInkAnnotation(
                    inkList = listOf(inkPath),
                    contents = "Check mark",
                    author = "Reviewer"
                )
                
                // Get existing annotations
                val annotations = page.getAnnotations()
                println("Total annotations: ${annotations.size}")
                
                // Update the first annotation
                if (annotations.isNotEmpty()) {
                    page.updateAnnotationContents(0, "Updated comment")
                }
            }
            
            // Save the document with new annotations
            document.save("/path/to/output.pdf")
        }
    } finally {
        core.destroyLibrary()
    }
}

fun demonstrateFormFieldOptions() {
    val core = PdfiumCore()
    core.initLibrary()
    
    try {
        val document = core.openDocument("/path/to/form.pdf")
            ?: throw IllegalStateException("Failed to open document")
        
        document.use {
            val form = document.initForm()
            val page = document.openPage(0)
            
            page.use {
                // Get a dropdown field
                val field = page.getFormFieldByName(form.formPtr, "country")
                
                if (field != null) {
                    // List all options
                    val options = form.getFieldOptions(page, field)
                    options.forEach { option ->
                        println("${option.index}: ${option.label} = ${option.value} (${if (option.isSelected) "selected" else "not selected"})")
                    }
                    
                    // Select an option
                    form.setFormFieldOptionSelection(page, field, 2, true)
                    
                    // Get selected options
                    val selected = form.getFormFieldSelectedOptions(page, field)
                    println("Selected indices: $selected")
                    
                    page.closeFormField(field)
                }
            }
            
            form.close()
        }
    } finally {
        core.destroyLibrary()
    }
}
```

## Important Notes

1. **Memory Management**: Always close annotations, pages, and forms when done to prevent memory leaks.

2. **Quad Points Format**: Each quad point set consists of 8 doubles representing 4 points (x1, y1, x2, y2, x3, y3, x4, y4) in page coordinates.

3. **Color Format**: Colors use RGBA format with values 0-255. The alpha channel controls opacity (255 = fully opaque).

4. **Form Field Options**: The value property represents the export value used when the form is submitted, which may differ from the display label.

5. **PDFium Limitation**: Due to PDFium API limitations, `getFormFieldOptionValue` currently returns the label in cases where the API doesn't distinguish between label and value. This is the common case in most PDF forms.

6. **Thread Safety**: These operations are not thread-safe. Use appropriate synchronization if accessing from multiple threads.

## Error Handling

All annotation creation and modification methods return `Boolean` indicating success or failure:

```kotlin
val success = page.createHighlightAnnotation(...)
if (!success) {
    // Handle error - could be invalid parameters or PDF limitations
    println("Failed to create annotation")
}
```
