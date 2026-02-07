package com.hyntix.pdfium.annotation

import android.graphics.RectF

/**
 * Base class for PDF annotations providing common properties.
 * 
 * This is an abstract representation of annotation data that can be
 * read from or written to PDF documents.
 */
abstract class PdfAnnotationBase {
    /** Type of the annotation */
    abstract val type: AnnotationType
    
    /** Bounding rectangle in page coordinates */
    abstract val rect: RectF
    
    /** Text contents or comment */
    abstract var contents: String
    
    /** Author/creator of the annotation */
    abstract var author: String
    
    /** Subject of the annotation */
    abstract var subject: String
    
    /** Creation date (ISO 8601 format) */
    abstract val creationDate: String
    
    /** Last modification date (ISO 8601 format) */
    abstract var modificationDate: String
    
    /** Color of the annotation */
    abstract var color: AnnotationColor
    
    /** Opacity/alpha value (0.0 to 1.0) */
    abstract var opacity: Float
    
    /** Annotation flags (see FPDF_ANNOT_FLAG_* constants) */
    abstract var flags: Int
}
