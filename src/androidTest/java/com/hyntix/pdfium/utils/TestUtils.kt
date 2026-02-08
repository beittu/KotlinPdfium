package com.hyntix.pdfium.utils

import android.content.Context
import android.graphics.RectF
import androidx.test.platform.app.InstrumentationRegistry
import com.hyntix.pdfium.PdfiumCore
import com.hyntix.pdfium.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Test utilities for KotlinPdfium instrumentation tests.
 * 
 * Provides helper methods for:
 * - Loading test PDFs from assets
 * - Creating temporary test files
 * - Common test assertions
 * - Resource cleanup
 */
object TestUtils {
    
    /**
     * Get the test context.
     */
    fun getTestContext(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
    
    /**
     * Get the instrumentation context (for accessing test assets).
     */
    fun getInstrumentationContext(): Context {
        return InstrumentationRegistry.getInstrumentation().context
    }
    
    /**
     * Load a PDF file from test assets.
     * 
     * @param assetPath Path to the PDF in the test assets directory
     * @return File object pointing to the copied PDF
     */
    fun loadPdfFromAssets(assetPath: String): File {
        val context = getInstrumentationContext()
        val inputStream: InputStream = context.assets.open(assetPath)
        
        val tempFile = File.createTempFile("test_pdf", ".pdf", getTestContext().cacheDir)
        tempFile.deleteOnExit()
        
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        
        return tempFile
    }
    
    /**
     * Open a PDF document from assets.
     * 
     * @param core PdfiumCore instance
     * @param assetPath Path to the PDF in assets
     * @param password Optional password for encrypted PDFs
     * @return Opened PdfDocument
     */
    fun openPdfFromAssets(
        core: PdfiumCore,
        assetPath: String,
        password: String? = null
    ): PdfDocument {
        val file = loadPdfFromAssets(assetPath)
        return core.newDocument(file, password)
    }
    
    /**
     * Create a temporary PDF file for testing.
     * 
     * @param content PDF content as ByteArray
     * @return Temporary file containing the PDF
     */
    fun createTempPdf(content: ByteArray): File {
        val tempFile = File.createTempFile("test_pdf", ".pdf", getTestContext().cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { it.write(content) }
        return tempFile
    }
    
    /**
     * Assert that two RectF objects are approximately equal.
     * 
     * @param expected Expected rectangle
     * @param actual Actual rectangle
     * @param delta Maximum allowed difference for each coordinate
     */
    fun assertRectEquals(expected: RectF, actual: RectF, delta: Float = 0.01f) {
        assert(Math.abs(expected.left - actual.left) < delta) {
            "Left mismatch: expected ${expected.left}, got ${actual.left}"
        }
        assert(Math.abs(expected.top - actual.top) < delta) {
            "Top mismatch: expected ${expected.top}, got ${actual.top}"
        }
        assert(Math.abs(expected.right - actual.right) < delta) {
            "Right mismatch: expected ${expected.right}, got ${actual.right}"
        }
        assert(Math.abs(expected.bottom - actual.bottom) < delta) {
            "Bottom mismatch: expected ${expected.bottom}, got ${actual.bottom}"
        }
    }
    
    /**
     * Clean up test files and resources.
     * 
     * @param files Files to delete
     */
    fun cleanupFiles(vararg files: File) {
        files.forEach { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }
    
    /**
     * Assert that a string contains a substring (case-insensitive).
     */
    fun assertContains(haystack: String, needle: String, message: String = "") {
        assert(haystack.contains(needle, ignoreCase = true)) {
            if (message.isNotEmpty()) message else "Expected '$haystack' to contain '$needle'"
        }
    }
    
    /**
     * Assert that a list is not empty.
     */
    fun <T> assertNotEmpty(list: List<T>, message: String = "List should not be empty") {
        assert(list.isNotEmpty()) { message }
    }
    
    /**
     * Assert that a list has expected size.
     */
    fun <T> assertSize(list: List<T>, expectedSize: Int, message: String = "") {
        assert(list.size == expectedSize) {
            if (message.isNotEmpty()) message 
            else "Expected size $expectedSize, got ${list.size}"
        }
    }
}
