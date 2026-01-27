package com.digivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digivault.data.model.*
import com.digivault.ui.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit,
    onAddSchedule: () -> Unit,
    onScheduleClick: (Long) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val schedules by viewModel.schedules.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule & Events") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSchedule,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Schedule")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Tabs
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            // Schedules List
            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No events scheduled",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(schedules) { schedule ->
                        ScheduleItemCard(
                            schedule = schedule,
                            onClick = { onScheduleClick(schedule.id) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: ScheduleCategory?,
    onCategorySelected: (ScheduleCategory?) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = when (selectedCategory) {
            null -> 0
            ScheduleCategory.JOB_INTERVIEW -> 1
            ScheduleCategory.SCHOLARSHIP -> 2
            ScheduleCategory.EXAM -> 3
            ScheduleCategory.ASSIGNMENT -> 4
            ScheduleCategory.OTHER -> 5
        },
        containerColor = MaterialTheme.colorScheme.surface,
        edgePadding = 16.dp
    ) {
        Tab(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            text = { Text("All") }
        )
        Tab(
            selected = selectedCategory == ScheduleCategory.JOB_INTERVIEW,
            onClick = { onCategorySelected(ScheduleCategory.JOB_INTERVIEW) },
            text = { Text("Jobs") },
            icon = { Icon(Icons.Default.Work, contentDescription = null) }
        )
        Tab(
            selected = selectedCategory == ScheduleCategory.SCHOLARSHIP,
            onClick = { onCategorySelected(ScheduleCategory.SCHOLARSHIP) },
            text = { Text("Scholarships") },
            icon = { Icon(Icons.Default.School, contentDescription = null) }
        )
        Tab(
            selected = selectedCategory == ScheduleCategory.EXAM,
            onClick = { onCategorySelected(ScheduleCategory.EXAM) },
            text = { Text("Exams") },
            icon = { Icon(Icons.Default.Edit, contentDescription = null) }
        )
        Tab(
            selected = selectedCategory == ScheduleCategory.ASSIGNMENT,
            onClick = { onCategorySelected(ScheduleCategory.ASSIGNMENT) },
            text = { Text("Assignments") },
            icon = { Icon(Icons.Default.Assignment, contentDescription = null) }
        )
        Tab(
            selected = selectedCategory == ScheduleCategory.OTHER,
            onClick = { onCategorySelected(ScheduleCategory.OTHER) },
            text = { Text("Other") }
        )
    }
}

@Composable
fun ScheduleItemCard(
    schedule: Schedule,
    onClick: () -> Unit,
    viewModel: ScheduleViewModel
) {
    val daysUntil = viewModel.getDaysUntilEvent(schedule.eventDate)
    val urgencyStatus = viewModel.getUrgencyStatus(daysUntil)
    
    val urgencyColor = when {
        daysUntil == 0 -> Color.Red
        daysUntil == 1 -> Color(0xFFFF5722)
        daysUntil <= 3 -> Color(0xFFFF9800)
        daysUntil <= 7 -> Color(0xFFFFC107)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Category Icon
                Surface(
                    shape = CircleShape,
                    color = urgencyColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (schedule.category) {
                                ScheduleCategory.JOB_INTERVIEW -> Icons.Default.Work
                                ScheduleCategory.SCHOLARSHIP -> Icons.Default.School
                                ScheduleCategory.EXAM -> Icons.Default.Edit
                                ScheduleCategory.ASSIGNMENT -> Icons.Default.Assignment
                                ScheduleCategory.OTHER -> Icons.Default.Event
                            },
                            contentDescription = null,
                            tint = urgencyColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Urgency Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = urgencyColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = urgencyStatus,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = urgencyColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            if (schedule.description.isNotBlank()) {
                Text(
                    text = schedule.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Date and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(Date(schedule.eventDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!schedule.location.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = schedule.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            // Documents indicator
            if (!schedule.attachedDocuments.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${schedule.attachedDocuments.split(",").size} documents attached",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Notification indicator
            if (schedule.reminderEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Reminder ${schedule.notificationTime} days before",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    onNavigateBack: () -> Unit,
    onSave: (Schedule, List<ScheduleDocument>) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ScheduleCategory.JOB_INTERVIEW) }
    var eventDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var location by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var notificationDays by remember { mutableStateOf(7) }
    val attachedDocuments = remember { mutableStateListOf<ScheduleDocument>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val schedule = Schedule(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                eventDate = eventDate,
                                location = location.ifBlank { null },
                                reminderEnabled = reminderEnabled,
                                notificationTime = notificationDays
                            )
                            onSave(schedule, attachedDocuments)
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            item {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Optional)") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Reminder")
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { reminderEnabled = it }
                            )
                        }
                        
                        if (reminderEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Notify me $notificationDays days before",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Attached Documents",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                AttachDocumentButton(
                    category = selectedCategory,
                    onDocumentAdded = { doc -> attachedDocuments.add(doc) }
                )
            }

            items(attachedDocuments) { doc ->
                AttachedDocumentItem(
                    document = doc,
                    onRemove = { attachedDocuments.remove(doc) }
                )
            }
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: ScheduleCategory,
    onCategorySelected: (ScheduleCategory) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryOption(
            category = ScheduleCategory.JOB_INTERVIEW,
            icon = Icons.Default.Work,
            label = "Job Interview",
            selected = selectedCategory == ScheduleCategory.JOB_INTERVIEW,
            onClick = { onCategorySelected(ScheduleCategory.JOB_INTERVIEW) }
        )
        CategoryOption(
            category = ScheduleCategory.SCHOLARSHIP,
            icon = Icons.Default.School,
            label = "Scholarship",
            selected = selectedCategory == ScheduleCategory.SCHOLARSHIP,
            onClick = { onCategorySelected(ScheduleCategory.SCHOLARSHIP) }
        )
        CategoryOption(
            category = ScheduleCategory.EXAM,
            icon = Icons.Default.Edit,
            label = "Exam",
            selected = selectedCategory == ScheduleCategory.EXAM,
            onClick = { onCategorySelected(ScheduleCategory.EXAM) }
        )
        CategoryOption(
            category = ScheduleCategory.ASSIGNMENT,
            icon = Icons.Default.Assignment,
            label = "Assignment",
            selected = selectedCategory == ScheduleCategory.ASSIGNMENT,
            onClick = { onCategorySelected(ScheduleCategory.ASSIGNMENT) }
        )
    }
}

@Composable
fun CategoryOption(
    category: ScheduleCategory,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AttachDocumentButton(
    category: ScheduleCategory,
    onDocumentAdded: (ScheduleDocument) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Open document picker */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AttachFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (category) {
                    ScheduleCategory.JOB_INTERVIEW -> "Attach Resume/Portfolio"
                    ScheduleCategory.SCHOLARSHIP -> "Attach Certificates/Transcripts"
                    else -> "Attach Documents"
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AttachedDocumentItem(
    document: ScheduleDocument,
    onRemove: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.documentName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = document.documentType.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
