package com.digivault.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRCodeGenerator {
    
    /**
     * Generate QR code bitmap from text
     * @param content Text to encode in QR code
     * @param size Size of the QR code (width and height)
     * @return Bitmap of the QR code
     */
    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 1)
            }
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, 
                        y, 
                        if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    )
                }
            }
            
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Generate QR code for document sharing
     */
    fun generateDocumentQRCode(documentId: Long, documentName: String): Bitmap? {
        val qrContent = "digivault://document/$documentId?name=$documentName"
        return generateQRCode(qrContent)
    }
    
    /**
     * Generate QR code for card information
     */
    fun generateCardQRCode(cardId: Long, cardNumber: String): Bitmap? {
        val qrContent = "digivault://card/$cardId?number=$cardNumber"
        return generateQRCode(qrContent)
    }
}
