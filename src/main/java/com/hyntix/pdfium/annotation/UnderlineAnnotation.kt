package com.hyntix.pdfium.annotation

/**
 * Underline annotation - underlines text in a document.
 * 
 * @property contents Optional text content/comment
 * @property author Author of the annotation
 * @property color Color of the underline (default: blue with 50% opacity)
 * @property quadPoints Points defining the underlining region
 */
class UnderlineAnnotation(
    override val contents: String = "",
    override val author: String = "",
    override val color: AnnotationColor = AnnotationColor(0, 0, 255, 128),
    override val quadPoints: List<DoubleArray> = emptyList()
) : MarkupAnnotation(AnnotationType.UNDERLINE, contents, author, color, quadPoints) {
    
    /**
     * Check if the annotation has valid quad points.
     */
    override fun isValid(): Boolean = validateQuadPoints()
}
