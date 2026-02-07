package com.hyntix.pdfium.annotation

/**
 * Highlight annotation - highlights text in a document.
 * 
 * @property contents Optional text content/comment
 * @property author Author of the annotation
 * @property color Color of the highlight (default: yellow with 50% opacity)
 * @property quadPoints Points defining the highlighting region
 */
class HighlightAnnotation(
    override val contents: String = "",
    override val author: String = "",
    override val color: AnnotationColor = AnnotationColor(255, 255, 0, 128),
    override val quadPoints: List<DoubleArray> = emptyList()
) : MarkupAnnotation(AnnotationType.HIGHLIGHT, contents, author, color, quadPoints) {
    
    /**
     * Check if the annotation has valid quad points.
     */
    override fun isValid(): Boolean = validateQuadPoints()
}
