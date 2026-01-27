package com.digivault.data.local

import androidx.room.*
import com.digivault.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE folderId = :folderId ORDER BY updatedAt DESC")
    fun getDocumentsByFolder(folderId: Long): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): Document?
    
    @Query("SELECT * FROM documents ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentDocuments(limit: Int = 5): Flow<List<Document>>
    
    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate > :currentTime ORDER BY expiryDate ASC")
    fun getDocumentsWithExpiry(currentTime: Long = System.currentTimeMillis()): Flow<List<Document>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long
    
    @Update
    suspend fun updateDocument(document: Document)
    
    @Delete
    suspend fun deleteDocument(document: Document)
    
    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)
    
    @Query("SELECT * FROM documents WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchDocuments(query: String): Flow<List<Document>>
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<Card>>
    
    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): Card?
    
    @Query("SELECT * FROM cards WHERE cardType = :type ORDER BY updatedAt DESC")
    fun getCardsByType(type: String): Flow<List<Card>>
    
    @Query("SELECT * FROM cards WHERE expiryDate IS NOT NULL AND expiryDate > :currentTime ORDER BY expiryDate ASC LIMIT 3")
    fun getExpiringCards(currentTime: Long = System.currentTimeMillis()): Flow<List<Card>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long
    
    @Update
    suspend fun updateCard(card: Card)
    
    @Delete
    suspend fun deleteCard(card: Card)
    
    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)
}

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY createdAt DESC")
    fun getAllFolders(): Flow<List<Folder>>
    
    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Long): Folder?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long
    
    @Update
    suspend fun updateFolder(folder: Folder)
    
    @Delete
    suspend fun deleteFolder(folder: Folder)
    
    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: Long)
    
    @Query("UPDATE folders SET documentCount = (SELECT COUNT(*) FROM documents WHERE folderId = folders.id)")
    suspend fun updateFolderCounts()
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY eventDate ASC")
    fun getAllSchedules(): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE category = :category ORDER BY eventDate ASC")
    fun getSchedulesByCategory(category: ScheduleCategory): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): Schedule?
    
    @Query("SELECT * FROM schedules WHERE eventDate >= :startTime AND eventDate <= :endTime AND status = :status ORDER BY eventDate ASC")
    fun getSchedulesByDateRange(
        startTime: Long,
        endTime: Long,
        status: ScheduleStatus = ScheduleStatus.UPCOMING
    ): Flow<List<Schedule>>
    
    @Query("SELECT * FROM schedules WHERE eventDate >= :currentTime AND eventDate <= :sevenDaysLater AND status = :status ORDER BY eventDate ASC")
    fun getUpcomingSchedules(
        currentTime: Long = System.currentTimeMillis(),
        sevenDaysLater: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000),
        status: ScheduleStatus = ScheduleStatus.UPCOMING
    ): Flow<List<Schedule>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long
    
    @Update
    suspend fun updateSchedule(schedule: Schedule)
    
    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
    
    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)
    
    @Transaction
    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    suspend fun getScheduleWithDocuments(scheduleId: Long): ScheduleWithDocuments?
}

@Dao
interface ScheduleDocumentDao {
    @Query("SELECT * FROM schedule_documents WHERE scheduleId = :scheduleId")
    fun getDocumentsForSchedule(scheduleId: Long): Flow<List<ScheduleDocument>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleDocument(document: ScheduleDocument): Long
    
    @Delete
    suspend fun deleteScheduleDocument(document: ScheduleDocument)
    
    @Query("DELETE FROM schedule_documents WHERE scheduleId = :scheduleId")
    suspend fun deleteAllDocumentsForSchedule(scheduleId: Long)
}
