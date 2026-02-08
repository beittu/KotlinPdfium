package com.hyntix.pdfium.annotation

import android.util.Base64

/**
 * Represents the appearance stream of an annotation.
 *
 * @property stream The binary appearance stream data
 * @property fontSize Font size used in the appearance (0 if not applicable)
 * @property fontName Font name used in the appearance
 * @property textColor Text color used in the appearance
 * @property backgroundColor Background color (null if transparent)
 */
data class AnnotationAppearance(
    val stream: ByteArray,
    val fontSize: Float = 0f,
    val fontName: String = "Helv",
    val textColor: AnnotationColor = AnnotationColor(0, 0, 0),
    val backgroundColor: AnnotationColor? = null
) {
    /**
     * Convert the appearance stream to Base64 encoding.
     *
     * @return Base64-encoded string
     */
    fun toBase64(): String {
        return Base64.encodeToString(stream, Base64.DEFAULT)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationAppearance) return false
        
        if (!stream.contentEquals(other.stream)) return false
        if (fontSize != other.fontSize) return false
        if (fontName != other.fontName) return false
        if (textColor != other.textColor) return false
        if (backgroundColor != other.backgroundColor) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = stream.contentHashCode()
        result = 31 * result + fontSize.hashCode()
        result = 31 * result + fontName.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + (backgroundColor?.hashCode() ?: 0)
        return result
    }
    
    companion object {
        /**
         * Create an AnnotationAppearance from a Base64-encoded string.
         *
         * @param encoded The Base64-encoded appearance stream
         * @return AnnotationAppearance instance
         */
        fun fromBase64(encoded: String): AnnotationAppearance {
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            return AnnotationAppearance(bytes)
        }
    }
}
