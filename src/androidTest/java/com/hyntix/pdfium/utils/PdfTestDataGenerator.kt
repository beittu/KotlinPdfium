package com.hyntix.pdfium.utils

import java.io.ByteArrayOutputStream

/**
 * Generates minimal valid PDF documents for testing purposes.
 * 
 * This class creates basic PDFs programmatically without external dependencies.
 * The PDFs are minimal but valid according to PDF specification.
 * 
 * Note: For production use, consider using a proper PDF library.
 * These are simplified test PDFs for validation purposes only.
 */
object PdfTestDataGenerator {
    
    /**
     * Generate a simple PDF with one blank page.
     * 
     * @return ByteArray containing the PDF content
     */
    fun generateSimplePdf(): ByteArray {
        val output = ByteArrayOutputStream()
        
        // PDF Header
        output.write("%PDF-1.4\n".toByteArray())
        output.write("%âãÏÓ\n".toByteArray()) // Binary marker
        
        // Objects
        val objects = mutableListOf<ByteArray>()
        
        // Object 1: Catalog
        objects.add("""
            1 0 obj
            <<
            /Type /Catalog
            /Pages 2 0 R
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 2: Pages
        objects.add("""
            2 0 obj
            <<
            /Type /Pages
            /Kids [3 0 R]
            /Count 1
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 3: Page
        objects.add("""
            3 0 obj
            <<
            /Type /Page
            /Parent 2 0 R
            /MediaBox [0 0 612 792]
            /Contents 4 0 R
            /Resources <<
            /ProcSet [/PDF]
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 4: Contents (empty)
        objects.add("""
            4 0 obj
            <<
            /Length 0
            >>
            stream
            endstream
            endobj
        """.trimIndent().toByteArray())
        
        // Write objects and track offsets
        val offsets = mutableListOf<Long>()
        objects.forEach { obj ->
            offsets.add(output.size().toLong())
            output.write(obj)
            output.write("\n".toByteArray())
        }
        
        // Write xref table
        val xrefPos = output.size()
        output.write("xref\n".toByteArray())
        output.write("0 ${objects.size + 1}\n".toByteArray())
        output.write("0000000000 65535 f \n".toByteArray())
        offsets.forEach { offset ->
            output.write(String.format("%010d 00000 n \n", offset).toByteArray())
        }
        
        // Write trailer
        output.write("""
            trailer
            <<
            /Size ${objects.size + 1}
            /Root 1 0 R
            >>
            startxref
            $xrefPos
            %%EOF
        """.trimIndent().toByteArray())
        
        return output.toByteArray()
    }
    
    /**
     * Generate a PDF with a simple text annotation.
     * 
     * @return ByteArray containing the PDF content
     */
    fun generateAnnotatedPdf(): ByteArray {
        val output = ByteArrayOutputStream()
        
        // PDF Header
        output.write("%PDF-1.4\n".toByteArray())
        output.write("%âãÏÓ\n".toByteArray())
        
        val objects = mutableListOf<ByteArray>()
        
        // Object 1: Catalog
        objects.add("""
            1 0 obj
            <<
            /Type /Catalog
            /Pages 2 0 R
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 2: Pages
        objects.add("""
            2 0 obj
            <<
            /Type /Pages
            /Kids [3 0 R]
            /Count 1
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 3: Page with annotation
        objects.add("""
            3 0 obj
            <<
            /Type /Page
            /Parent 2 0 R
            /MediaBox [0 0 612 792]
            /Contents 4 0 R
            /Annots [5 0 R]
            /Resources <<
            /ProcSet [/PDF /Text]
            /Font <<
            /F1 <<
            /Type /Font
            /Subtype /Type1
            /BaseFont /Helvetica
            >>
            >>
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 4: Contents
        objects.add("""
            4 0 obj
            <<
            /Length 44
            >>
            stream
            BT
            /F1 12 Tf
            50 700 Td
            (Test) Tj
            ET
            endstream
            endobj
        """.trimIndent().toByteArray())
        
        // Object 5: Text Annotation
        objects.add("""
            5 0 obj
            <<
            /Type /Annot
            /Subtype /Text
            /Rect [100 700 120 720]
            /Contents (Test annotation)
            /Name /Comment
            /C [1.0 1.0 0.0]
            /CA 1.0
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Write objects
        val offsets = mutableListOf<Long>()
        objects.forEach { obj ->
            offsets.add(output.size().toLong())
            output.write(obj)
            output.write("\n".toByteArray())
        }
        
        // Write xref
        val xrefPos = output.size()
        output.write("xref\n".toByteArray())
        output.write("0 ${objects.size + 1}\n".toByteArray())
        output.write("0000000000 65535 f \n".toByteArray())
        offsets.forEach { offset ->
            output.write(String.format("%010d 00000 n \n", offset).toByteArray())
        }
        
        // Write trailer
        output.write("""
            trailer
            <<
            /Size ${objects.size + 1}
            /Root 1 0 R
            >>
            startxref
            $xrefPos
            %%EOF
        """.trimIndent().toByteArray())
        
        return output.toByteArray()
    }
    
    /**
     * Generate a PDF with form fields.
     * 
     * @return ByteArray containing the PDF content
     */
    fun generateFormPdf(): ByteArray {
        val output = ByteArrayOutputStream()
        
        // PDF Header
        output.write("%PDF-1.4\n".toByteArray())
        output.write("%âãÏÓ\n".toByteArray())
        
        val objects = mutableListOf<ByteArray>()
        
        // Object 1: Catalog with AcroForm
        objects.add("""
            1 0 obj
            <<
            /Type /Catalog
            /Pages 2 0 R
            /AcroForm <<
            /Fields [5 0 R 6 0 R]
            /NeedAppearances true
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 2: Pages
        objects.add("""
            2 0 obj
            <<
            /Type /Pages
            /Kids [3 0 R]
            /Count 1
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 3: Page with form fields
        objects.add("""
            3 0 obj
            <<
            /Type /Page
            /Parent 2 0 R
            /MediaBox [0 0 612 792]
            /Contents 4 0 R
            /Annots [5 0 R 6 0 R]
            /Resources <<
            /ProcSet [/PDF /Text]
            /Font <<
            /F1 <<
            /Type /Font
            /Subtype /Type1
            /BaseFont /Helvetica
            >>
            >>
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 4: Contents
        objects.add("""
            4 0 obj
            <<
            /Length 0
            >>
            stream
            endstream
            endobj
        """.trimIndent().toByteArray())
        
        // Object 5: Text Field
        objects.add("""
            5 0 obj
            <<
            /Type /Annot
            /Subtype /Widget
            /FT /Tx
            /T (textfield1)
            /V (Default Text)
            /Rect [50 700 250 720]
            /F 4
            /MK <<
            /BC [0 0 0]
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 6: Checkbox
        objects.add("""
            6 0 obj
            <<
            /Type /Annot
            /Subtype /Widget
            /FT /Btn
            /T (checkbox1)
            /V /Off
            /Rect [50 650 70 670]
            /F 4
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Write objects
        val offsets = mutableListOf<Long>()
        objects.forEach { obj ->
            offsets.add(output.size().toLong())
            output.write(obj)
            output.write("\n".toByteArray())
        }
        
        // Write xref
        val xrefPos = output.size()
        output.write("xref\n".toByteArray())
        output.write("0 ${objects.size + 1}\n".toByteArray())
        output.write("0000000000 65535 f \n".toByteArray())
        offsets.forEach { offset ->
            output.write(String.format("%010d 00000 n \n", offset).toByteArray())
        }
        
        // Write trailer
        output.write("""
            trailer
            <<
            /Size ${objects.size + 1}
            /Root 1 0 R
            >>
            startxref
            $xrefPos
            %%EOF
        """.trimIndent().toByteArray())
        
        return output.toByteArray()
    }
    
    /**
     * Generate a simple PDF that mimics XFA structure.
     * Note: True XFA forms require complex XML structure.
     * This is a simplified version for testing detection.
     * 
     * @return ByteArray containing the PDF content
     */
    fun generateXfaPdf(): ByteArray {
        val output = ByteArrayOutputStream()
        
        // PDF Header
        output.write("%PDF-1.5\n".toByteArray())
        output.write("%âãÏÓ\n".toByteArray())
        
        val objects = mutableListOf<ByteArray>()
        
        // Simple XFA XML content
        val xfaContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xdp:xdp xmlns:xdp="http://ns.adobe.com/xdp/">
                <template xmlns="http://www.xfa.org/schema/xfa-template/3.3/">
                    <subform name="form1">
                        <field name="TextField1">
                            <ui>
                                <textEdit/>
                            </ui>
                        </field>
                    </subform>
                </template>
            </xdp:xdp>
        """.trimIndent()
        
        // Object 1: Catalog with XFA
        objects.add("""
            1 0 obj
            <<
            /Type /Catalog
            /Pages 2 0 R
            /AcroForm <<
            /Fields []
            /XFA [
            (template) 5 0 R
            ]
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 2: Pages
        objects.add("""
            2 0 obj
            <<
            /Type /Pages
            /Kids [3 0 R]
            /Count 1
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 3: Page
        objects.add("""
            3 0 obj
            <<
            /Type /Page
            /Parent 2 0 R
            /MediaBox [0 0 612 792]
            /Contents 4 0 R
            /Resources <<
            /ProcSet [/PDF]
            >>
            >>
            endobj
        """.trimIndent().toByteArray())
        
        // Object 4: Contents
        objects.add("""
            4 0 obj
            <<
            /Length 0
            >>
            stream
            endstream
            endobj
        """.trimIndent().toByteArray())
        
        // Object 5: XFA Stream
        val xfaBytes = xfaContent.toByteArray()
        objects.add("""
            5 0 obj
            <<
            /Length ${xfaBytes.size}
            >>
            stream
            $xfaContent
            endstream
            endobj
        """.trimIndent().toByteArray())
        
        // Write objects
        val offsets = mutableListOf<Long>()
        objects.forEach { obj ->
            offsets.add(output.size().toLong())
            output.write(obj)
            output.write("\n".toByteArray())
        }
        
        // Write xref
        val xrefPos = output.size()
        output.write("xref\n".toByteArray())
        output.write("0 ${objects.size + 1}\n".toByteArray())
        output.write("0000000000 65535 f \n".toByteArray())
        offsets.forEach { offset ->
            output.write(String.format("%010d 00000 n \n", offset).toByteArray())
        }
        
        // Write trailer
        output.write("""
            trailer
            <<
            /Size ${objects.size + 1}
            /Root 1 0 R
            >>
            startxref
            $xrefPos
            %%EOF
        """.trimIndent().toByteArray())
        
        return output.toByteArray()
    }
}
