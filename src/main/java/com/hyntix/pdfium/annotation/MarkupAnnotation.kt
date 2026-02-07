package com.hyntix.pdfium.annotation

/**
 * Base class for markup annotations (highlight, underline, strikeout, squiggly).
 * Markup annotations mark up text in a document.
 * 
 * @property type The type of annotation
 * @property contents Optional text content/comment for the annotation
 * @property author Author of the annotation
 * @property color Color of the annotation
 * @property quadPoints Points defining the highlighting region. Each quad point set has 8 values (4 points).
 */
abstract class MarkupAnnotation(
    open val type: AnnotationType,
    open val contents: String,
    open val author: String,
    open val color: AnnotationColor,
    open val quadPoints: List<DoubleArray>
) {
    /**
     * Check if the annotation has valid data.
     */
    abstract fun isValid(): Boolean

    /**
     * Validate that quad points are properly formatted.
     * Each quad point array should have 8 values (4 points with x,y coordinates).
     */
    protected fun validateQuadPoints(): Boolean {
        return quadPoints.isNotEmpty() && quadPoints.all { it.size == 8 }
    }
}
