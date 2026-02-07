package com.hyntix.pdfium.annotation

/**
 * Strikeout annotation - strikes through text in a document.
 * 
 * @property contents Optional text content/comment
 * @property author Author of the annotation
 * @property color Color of the strikeout (default: red with 50% opacity)
 * @property quadPoints Points defining the strikeout region
 */
class StrikeoutAnnotation(
    override val contents: String = "",
    override val author: String = "",
    override val color: AnnotationColor = AnnotationColor(255, 0, 0, 128),
    override val quadPoints: List<DoubleArray> = emptyList()
) : MarkupAnnotation(AnnotationType.STRIKEOUT, contents, author, color, quadPoints) {
    
    /**
     * Check if the annotation has valid quad points.
     */
    override fun isValid(): Boolean = validateQuadPoints()
}
