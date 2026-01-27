package com.digivault.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "documents")
@Parcelize
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val fileType: String, // pdf, image, doc, etc.
    val folderId: Long?,
    val tags: String, // comma-separated
    val expiryDate: Long?, // timestamp
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null
) : Parcelable

@Entity(tableName = "cards")
@Parcelize
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardName: String,
    val cardNumber: String,
    val cardType: String, // ID Card, Passport, License, Credit Card, etc.
    val holderName: String,
    val issueDate: Long?,
    val expiryDate: Long?,
    val frontImagePath: String?,
    val backImagePath: String?,
    val officialWebsite: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

@Entity(tableName = "folders")
@Parcelize
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // icon identifier
    val color: String, // hex color
    val createdAt: Long = System.currentTimeMillis(),
    val documentCount: Int = 0
) : Parcelable

@Entity(tableName = "schedules")
@Parcelize
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: ScheduleCategory,
    val eventDate: Long, // timestamp
    val reminderEnabled: Boolean = true,
    val notificationTime: Int = 7, // days before
    val location: String?,
    val attachedDocuments: String?, // comma-separated document IDs
    val status: ScheduleStatus = ScheduleStatus.UPCOMING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ScheduleCategory {
    JOB_INTERVIEW,
    SCHOLARSHIP,
    EXAM,
    ASSIGNMENT,
    OTHER
}

enum class ScheduleStatus {
    UPCOMING,
    TODAY,
    COMPLETED,
    MISSED
}

@Entity(tableName = "schedule_documents")
@Parcelize
data class ScheduleDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scheduleId: Long,
    val documentName: String,
    val documentType: ScheduleDocumentType,
    val filePath: String,
    val uploadedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ScheduleDocumentType {
    RESUME,
    PORTFOLIO,
    COVER_LETTER,
    CERTIFICATE,
    TRANSCRIPT,
    OTHER
}

@Parcelize
data class ScheduleWithDocuments(
    val schedule: Schedule,
    val documents: List<ScheduleDocument>
) : Parcelable

// User preferences
data class UserPreferences(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val pinCode: String? = null,
    val isCloudSyncEnabled: Boolean = false,
    val language: String = "en",
    val theme: String = "system",
    val notificationsEnabled: Boolean = true
)
