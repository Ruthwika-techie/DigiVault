package com.digivault.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    
    private const val DOCUMENTS_DIR = "documents"
    private const val CARDS_DIR = "cards"
    private const val THUMBNAILS_DIR = "thumbnails"
    private const val SCHEDULES_DIR = "schedules"
    
    /**
     * Get the internal storage directory for documents
     */
    fun getDocumentsDirectory(context: Context): File {
        val dir = File(context.filesDir, DOCUMENTS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    
    /**
     * Get the internal storage directory for cards
     */
    fun getCardsDirectory(context: Context): File {
        val dir = File(context.filesDir, CARDS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    
    /**
     * Get the internal storage directory for thumbnails
     */
    fun getThumbnailsDirectory(context: Context): File {
        val dir = File(context.filesDir, THUMBNAILS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    
    /**
     * Get the internal storage directory for schedule documents
     */
    fun getSchedulesDirectory(context: Context): File {
        val dir = File(context.filesDir, SCHEDULES_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
    
    /**
     * Copy file from URI to internal storage
     */
    fun copyFileToInternalStorage(
        context: Context,
        uri: Uri,
        destinationDir: File,
        newFileName: String? = null
    ): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            val fileName = newFileName ?: getFileNameFromUri(context, uri) 
                ?: "file_${System.currentTimeMillis()}"
            
            val destinationFile = File(destinationDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            destinationFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get file name from URI
     */
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
        
        if (fileName == null) {
            fileName = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) path.substring(cut + 1) else path
            }
        }
        
        return fileName
    }
    
    /**
     * Get file extension from URI
     */
    fun getFileExtension(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
    
    /**
     * Get file type from URI (pdf, image, doc, etc.)
     */
    fun getFileType(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri) ?: return "unknown"
        
        return when {
            mimeType.startsWith("image/") -> "image"
            mimeType == "application/pdf" -> "pdf"
            mimeType.contains("word") || mimeType.contains("document") -> "doc"
            mimeType.contains("sheet") || mimeType.contains("excel") -> "xlsx"
            mimeType.contains("presentation") || mimeType.contains("powerpoint") -> "pptx"
            else -> "other"
        }
    }
    
    /**
     * Generate thumbnail for image
     */
    fun generateThumbnail(
        context: Context,
        imagePath: String,
        maxWidth: Int = 200,
        maxHeight: Int = 200
    ): File? {
        return try {
            val originalBitmap = BitmapFactory.decodeFile(imagePath)
            
            val ratio = Math.min(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            )
            
            val width = (originalBitmap.width * ratio).toInt()
            val height = (originalBitmap.height * ratio).toInt()
            
            val thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
            
            val thumbnailDir = getThumbnailsDirectory(context)
            val thumbnailFile = File(
                thumbnailDir, 
                "thumb_${System.currentTimeMillis()}.jpg"
            )
            
            FileOutputStream(thumbnailFile).use { out ->
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            
            originalBitmap.recycle()
            thumbnailBitmap.recycle()
            
            thumbnailFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Format file size to human readable string
     */
    fun formatFileSize(sizeInBytes: Long): String {
        if (sizeInBytes < 1024) return "$sizeInBytes B"
        
        val kb = sizeInBytes / 1024.0
        if (kb < 1024) return String.format("%.2f KB", kb)
        
        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.2f MB", mb)
        
        val gb = mb / 1024.0
        return String.format("%.2f GB", gb)
    }
    
    /**
     * Delete file safely
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Check if file exists
     */
    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }
    
    /**
     * Generate unique file name
     */
    fun generateUniqueFileName(originalName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val extension = originalName.substringAfterLast(".", "")
        val nameWithoutExt = originalName.substringBeforeLast(".")
        
        return if (extension.isNotEmpty()) {
            "${nameWithoutExt}_${timestamp}.$extension"
        } else {
            "${originalName}_${timestamp}"
        }
    }
}
