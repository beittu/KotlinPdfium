package com.hyntix.pdfium.annotation

/**
 * Represents a single stroke path in an ink annotation.
 * 
 * @property points List of points where each point is [x, y]
 */
data class InkPath(val points: List<DoubleArray>) {
    /**
     * Check if the path is valid.
     */
    fun isValid(): Boolean = points.isNotEmpty() && points.all { it.size == 2 }
}

/**
 * Ink annotation - freehand drawing/sketching.
 * 
 * @property contents Optional text content/comment
 * @property author Author of the annotation
 * @property color Color of the ink (default: black)
 * @property inkList List of stroke paths
 */
class InkAnnotation(
    val contents: String = "",
    val author: String = "",
    val color: AnnotationColor = AnnotationColor(0, 0, 0, 255),
    val inkList: List<InkPath> = emptyList()
) {
    /**
     * Check if the annotation has valid ink paths.
     */
    fun isValid(): Boolean = inkList.isNotEmpty() && inkList.all { it.isValid() }
}
