package com.hyntix.pdfium.xfa

import com.hyntix.pdfium.PdfiumCore
import java.nio.charset.StandardCharsets

/**
 * Represents an XFA packet in a PDF document.
 *
 * @property name The name of the XFA packet
 * @property content The binary content of the XFA packet
 */
data class XFAPacket(
    val name: String,
    val content: ByteArray
) {
    /**
     * Get the content as a UTF-8 string (for XML packets).
     */
    fun getContentAsString(): String {
        return String(content, StandardCharsets.UTF_8)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is XFAPacket) return false
        
        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

/**
 * Provides access to XFA form data in a PDF document.
 *
 * @property core The PdfiumCore instance for native operations
 * @property docPtr The native document pointer
 */
class XFAForms(
    private val core: PdfiumCore,
    private val docPtr: Long
) {
    
    /**
     * Check if the document has XFA forms.
     *
     * @return True if the document contains XFA forms
     */
    fun hasXFAForms(): Boolean {
        return core.hasXFAForms(docPtr)
    }
    
    /**
     * Get the number of XFA packets in the document.
     *
     * @return Number of XFA packets
     */
    fun getPacketCount(): Int {
        return core.getXFAPacketCount(docPtr)
    }
    
    /**
     * Get all XFA packets in the document.
     *
     * @return List of XFA packets
     */
    fun getPackets(): List<XFAPacket> {
        val count = getPacketCount()
        if (count <= 0) return emptyList()
        
        val packets = mutableListOf<XFAPacket>()
        for (i in 0 until count) {
            getPacket(i)?.let { packets.add(it) }
        }
        
        return packets
    }
    
    /**
     * Get a specific XFA packet by index.
     *
     * @param index The 0-based index of the packet
     * @return The XFA packet, or null if not found
     */
    fun getPacket(index: Int): XFAPacket? {
        val name = core.getXFAPacketName(docPtr, index) ?: return null
        val content = core.getXFAPacketContent(docPtr, index) ?: return null
        
        return XFAPacket(name, content)
    }
    
    /**
     * Get a specific XFA packet by name.
     *
     * @param name The name of the packet to find
     * @return The XFA packet, or null if not found
     */
    fun getPacketByName(name: String): XFAPacket? {
        val packets = getPackets()
        return packets.find { it.name == name }
    }
    
    /**
     * Export all XFA packets as a single XML string.
     *
     * This combines all XFA packets into a single XML document.
     *
     * @return The combined XML string, or empty string if no XFA forms
     */
    fun exportXML(): String {
        val packets = getPackets()
        if (packets.isEmpty()) return ""
        
        val builder = StringBuilder()
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        builder.append("<xfa:data xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\">\n")
        
        packets.forEach { packet ->
            builder.append("  <!-- XFA Packet: ${packet.name} -->\n")
            builder.append(packet.getContentAsString())
            builder.append("\n")
        }
        
        builder.append("</xfa:data>\n")
        
        return builder.toString()
    }
}
