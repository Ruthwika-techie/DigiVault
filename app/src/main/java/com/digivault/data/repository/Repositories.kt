package com.digivault.data.repository

import com.digivault.data.local.*
import com.digivault.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao
) {
    fun getAllDocuments(): Flow<List<Document>> = documentDao.getAllDocuments()
    
    fun getDocumentsByFolder(folderId: Long): Flow<List<Document>> =
        documentDao.getDocumentsByFolder(folderId)
    
    fun getRecentDocuments(limit: Int = 5): Flow<List<Document>> =
        documentDao.getRecentDocuments(limit)
    
    fun getDocumentsWithExpiry(): Flow<List<Document>> =
        documentDao.getDocumentsWithExpiry()
    
    suspend fun getDocumentById(id: Long): Document? =
        documentDao.getDocumentById(id)
    
    suspend fun insertDocument(document: Document): Long =
        documentDao.insertDocument(document)
    
    suspend fun updateDocument(document: Document) =
        documentDao.updateDocument(document)
    
    suspend fun deleteDocument(document: Document) =
        documentDao.deleteDocument(document)
    
    fun searchDocuments(query: String): Flow<List<Document>> =
        documentDao.searchDocuments(query)
}

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao
) {
    fun getAllCards(): Flow<List<Card>> = cardDao.getAllCards()
    
    suspend fun getCardById(id: Long): Card? = cardDao.getCardById(id)
    
    fun getCardsByType(type: String): Flow<List<Card>> =
        cardDao.getCardsByType(type)
    
    fun getExpiringCards(): Flow<List<Card>> = cardDao.getExpiringCards()
    
    suspend fun insertCard(card: Card): Long = cardDao.insertCard(card)
    
    suspend fun updateCard(card: Card) = cardDao.updateCard(card)
    
    suspend fun deleteCard(card: Card) = cardDao.deleteCard(card)
}

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao
) {
    fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()
    
    suspend fun getFolderById(id: Long): Folder? = folderDao.getFolderById(id)
    
    suspend fun insertFolder(folder: Folder): Long = folderDao.insertFolder(folder)
    
    suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder)
    
    suspend fun deleteFolder(folder: Folder) = folderDao.deleteFolder(folder)
    
    suspend fun updateFolderCounts() = folderDao.updateFolderCounts()
}

@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val scheduleDocumentDao: ScheduleDocumentDao
) {
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()
    
    fun getSchedulesByCategory(category: ScheduleCategory): Flow<List<Schedule>> =
        scheduleDao.getSchedulesByCategory(category)
    
    suspend fun getScheduleById(id: Long): Schedule? =
        scheduleDao.getScheduleById(id)
    
    fun getUpcomingSchedules(): Flow<List<Schedule>> =
        scheduleDao.getUpcomingSchedules()
    
    fun getSchedulesByDateRange(startTime: Long, endTime: Long): Flow<List<Schedule>> =
        scheduleDao.getSchedulesByDateRange(startTime, endTime)
    
    suspend fun insertSchedule(schedule: Schedule): Long =
        scheduleDao.insertSchedule(schedule)
    
    suspend fun updateSchedule(schedule: Schedule) =
        scheduleDao.updateSchedule(schedule)
    
    suspend fun deleteSchedule(schedule: Schedule) =
        scheduleDao.deleteSchedule(schedule)
    
    fun getDocumentsForSchedule(scheduleId: Long): Flow<List<ScheduleDocument>> =
        scheduleDocumentDao.getDocumentsForSchedule(scheduleId)
    
    suspend fun insertScheduleDocument(document: ScheduleDocument): Long =
        scheduleDocumentDao.insertScheduleDocument(document)
    
    suspend fun deleteScheduleDocument(document: ScheduleDocument) =
        scheduleDocumentDao.deleteScheduleDocument(document)
    
    suspend fun getScheduleWithDocuments(scheduleId: Long): ScheduleWithDocuments? =
        scheduleDao.getScheduleWithDocuments(scheduleId)
}
