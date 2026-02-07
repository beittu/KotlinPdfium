package com.hyntix.pdfium.annotation

// Data classes representing annotations in PDF documents

data class PdfAnnotation(
    val id: String,
    val type: String,
    val content: String,
    val pageIndex: Int,
    val rect: Rect
)

data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)