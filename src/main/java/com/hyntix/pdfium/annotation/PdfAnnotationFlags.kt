data class PdfAnnotationFlags(
    var flag: Int = 0
) {
    companion object {
        const val FLAG_PRINT: Int = 0x0001
        const val FLAG_HIDDEN: Int = 0x0002
        const val FLAG_INVISIBLE: Int = 0x0003
        const val FLAG_NOZOOM: Int = 0x0004
        const val FLAG_NOPAN: Int = 0x0005
    }
}