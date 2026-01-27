package com.digivault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digivault.data.model.*
import com.digivault.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val cardRepository: CardRepository,
    private val folderRepository: FolderRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val recentDocuments: StateFlow<List<Document>> = documentRepository
        .getRecentDocuments(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val folders: StateFlow<List<Folder>> = folderRepository
        .getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiringCards: StateFlow<List<Card>> = cardRepository
        .getExpiringCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingSchedules: StateFlow<List<Schedule>> = scheduleRepository
        .getUpcomingSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshData() {
        viewModelScope.launch {
            folderRepository.updateFolderCounts()
        }
    }
}

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _selectedFolder = MutableStateFlow<Folder?>(null)
    val selectedFolder: StateFlow<Folder?> = _selectedFolder

    val documents: StateFlow<List<Document>> = _selectedFolder
        .flatMapLatest { folder ->
            if (folder != null) {
                documentRepository.getDocumentsByFolder(folder.id)
            } else {
                documentRepository.getAllDocuments()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectFolder(folder: Folder?) {
        _selectedFolder.value = folder
    }

    fun addDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.insertDocument(document)
            folderRepository.updateFolderCounts()
        }
    }

    fun updateDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.updateDocument(document)
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.deleteDocument(document)
            folderRepository.updateFolderCounts()
        }
    }

    fun searchDocuments(query: String): Flow<List<Document>> {
        return documentRepository.searchDocuments(query)
    }
}

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {

    val folders: StateFlow<List<Folder>> = folderRepository
        .getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.insertFolder(folder)
        }
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.updateFolder(folder)
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.deleteFolder(folder)
        }
    }
}

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    val cards: StateFlow<List<Card>> = cardRepository
        .getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCard(card: Card) {
        viewModelScope.launch {
            cardRepository.insertCard(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            cardRepository.updateCard(card)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }

    suspend fun getCardById(id: Long): Card? {
        return cardRepository.getCardById(id)
    }
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<ScheduleCategory?>(null)
    val selectedCategory: StateFlow<ScheduleCategory?> = _selectedCategory

    val schedules: StateFlow<List<Schedule>> = _selectedCategory
        .flatMapLatest { category ->
            if (category != null) {
                scheduleRepository.getSchedulesByCategory(category)
            } else {
                scheduleRepository.getAllSchedules()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingSchedules: StateFlow<List<Schedule>> = scheduleRepository
        .getUpcomingSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: ScheduleCategory?) {
        _selectedCategory.value = category
    }

    fun addSchedule(schedule: Schedule, documents: List<ScheduleDocument> = emptyList()) {
        viewModelScope.launch {
            val scheduleId = scheduleRepository.insertSchedule(schedule)
            documents.forEach { doc ->
                scheduleRepository.insertScheduleDocument(doc.copy(scheduleId = scheduleId))
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
        }
    }

    fun getScheduleWithDocuments(scheduleId: Long): Flow<ScheduleWithDocuments?> = flow {
        emit(scheduleRepository.getScheduleWithDocuments(scheduleId))
    }

    fun addDocumentToSchedule(scheduleId: Long, document: ScheduleDocument) {
        viewModelScope.launch {
            scheduleRepository.insertScheduleDocument(document.copy(scheduleId = scheduleId))
        }
    }

    fun getDaysUntilEvent(eventDate: Long): Int {
        val diff = eventDate - System.currentTimeMillis()
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    fun getUrgencyStatus(daysUntil: Int): String {
        return when {
            daysUntil == 0 -> "Today"
            daysUntil == 1 -> "Tomorrow"
            daysUntil <= 7 -> "In $daysUntil days"
            else -> "Upcoming"
        }
    }
}
