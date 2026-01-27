package com.digivault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digivault.data.model.*
import com.digivault.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDocuments: () -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToFolders: () -> Unit,
    onNavigateToSchedules: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentDocuments by viewModel.recentDocuments.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val expiringCards by viewModel.expiringCards.collectAsState()
    val upcomingSchedules by viewModel.upcomingSchedules.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DigiVault", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToDocuments,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Upcoming Schedules Section
            if (upcomingSchedules.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Upcoming Events",
                        icon = Icons.Default.Notifications,
                        onViewAll = onNavigateToSchedules
                    )
                }
                
                items(upcomingSchedules.take(3)) { schedule ->
                    ScheduleCard(schedule = schedule)
                }
            }

            // Quick Actions
            item {
                QuickActionsRow(
                    onDocumentsClick = onNavigateToDocuments,
                    onCardsClick = onNavigateToCards,
                    onFoldersClick = onNavigateToFolders,
                    onSchedulesClick = onNavigateToSchedules
                )
            }

            // Recent Documents
            item {
                SectionHeader(
                    title = "Recent Documents",
                    icon = Icons.Default.Description,
                    onViewAll = onNavigateToDocuments
                )
            }

            if (recentDocuments.isEmpty()) {
                item {
                    EmptyStateCard("No documents yet")
                }
            } else {
                items(recentDocuments) { document ->
                    DocumentCard(document = document)
                }
            }

            // Folders
            item {
                SectionHeader(
                    title = "Folders",
                    icon = Icons.Default.Folder,
                    onViewAll = onNavigateToFolders
                )
            }

            if (folders.isEmpty()) {
                item {
                    EmptyStateCard("No folders created")
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(folders) { folder ->
                            FolderCard(folder = folder)
                        }
                    }
                }
            }

            // Expiring Cards
            if (expiringCards.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Cards Expiring Soon",
                        icon = Icons.Default.CreditCard,
                        onViewAll = onNavigateToCards
                    )
                }

                items(expiringCards) { card ->
                    ExpiringCardItem(card = card)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onViewAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(onClick = onViewAll) {
            Text("View All")
        }
    }
}

@Composable
fun QuickActionsRow(
    onDocumentsClick: () -> Unit,
    onCardsClick: () -> Unit,
    onFoldersClick: () -> Unit,
    onSchedulesClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionButton(
            icon = Icons.Default.Description,
            label = "Documents",
            onClick = onDocumentsClick
        )
        QuickActionButton(
            icon = Icons.Default.CreditCard,
            label = "Cards",
            onClick = onCardsClick
        )
        QuickActionButton(
            icon = Icons.Default.Folder,
            label = "Folders",
            onClick = onFoldersClick
        )
        QuickActionButton(
            icon = Icons.Default.CalendarToday,
            label = "Schedule",
            onClick = onSchedulesClick
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DocumentCard(document: Document) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to document details */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = document.fileType.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FolderCard(folder: Folder) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { /* Navigate to folder */ },
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(folder.color))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${folder.documentCount} docs",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule) {
    val daysUntil = ((schedule.eventDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    val urgencyColor = when {
        daysUntil == 0 -> Color.Red
        daysUntil <= 3 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = urgencyColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(urgencyColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        daysUntil == 0 -> "Today"
                        daysUntil == 1 -> "1d"
                        else -> "${daysUntil}d"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = schedule.category.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ExpiringCardItem(card: Card) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = card.cardName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                card.expiryDate?.let {
                    Text(
                        text = "Expires: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
