package com.hyntix.pdfium.annotation

/**
 * PDF annotation types as defined in the PDF specification.
 * Values correspond to FPDF_ANNOT_* constants in fpdf_annot.h
 */
enum class AnnotationType(val value: Int) {
    UNKNOWN(0),
    TEXT(1),
    LINK(2),
    FREETEXT(3),
    LINE(4),
    SQUARE(5),
    CIRCLE(6),
    POLYGON(7),
    POLYLINE(8),
    HIGHLIGHT(9),
    UNDERLINE(10),
    SQUIGGLY(11),
    STRIKEOUT(12),
    STAMP(13),
    CARET(14),
    INK(15),
    POPUP(16),
    FILEATTACHMENT(17),
    SOUND(18),
    MOVIE(19),
    WIDGET(20),
    SCREEN(21),
    PRINTERMARK(22),
    TRAPNET(23),
    WATERMARK(24),
    LINK3D(25),
    REDACT(26);

    companion object {
        /**
         * Convert an integer value to an AnnotationType.
         */
        fun fromValue(value: Int): AnnotationType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}
