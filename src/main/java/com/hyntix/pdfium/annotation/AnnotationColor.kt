package com.hyntix.pdfium.annotation

/**
 * Represents an RGBA color for PDF annotations.
 * 
 * @property red Red component (0-255)
 * @property green Green component (0-255)
 * @property blue Blue component (0-255)
 * @property alpha Alpha/opacity component (0-255, where 255 is fully opaque)
 */
data class AnnotationColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255
) {
    init {
        require(red in 0..255) { "Red must be between 0 and 255" }
        require(green in 0..255) { "Green must be between 0 and 255" }
        require(blue in 0..255) { "Blue must be between 0 and 255" }
        require(alpha in 0..255) { "Alpha must be between 0 and 255" }
    }

    /**
     * Convert to integer array for JNI calls.
     */
    fun toIntArray(): IntArray = intArrayOf(red, green, blue, alpha)

    companion object {
        /**
         * Create from integer array.
         */
        fun fromIntArray(array: IntArray): AnnotationColor {
            require(array.size >= 4) { "Array must have at least 4 elements" }
            return AnnotationColor(array[0], array[1], array[2], array[3])
        }
    }
}
