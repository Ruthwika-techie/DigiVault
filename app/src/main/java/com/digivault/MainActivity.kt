package com.digivault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digivault.ui.screens.*
import com.digivault.ui.theme.DigiVaultTheme
import com.digivault.utils.ScheduleNotificationWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule notification worker
        ScheduleNotificationWorker.scheduleNotificationCheck(this)
        
        setContent {
            DigiVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DigiVaultApp()
                }
            }
        }
    }
}

@Composable
fun DigiVaultApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        composable("auth") {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                onNavigateToDocuments = { navController.navigate("documents") },
                onNavigateToCards = { navController.navigate("cards") },
                onNavigateToFolders = { navController.navigate("folders") },
                onNavigateToSchedules = { navController.navigate("schedules") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("documents") {
            // DocumentsScreen would be implemented here
            navController.popBackStack()
        }
        
        composable("cards") {
            // CardsScreen would be implemented here
            navController.popBackStack()
        }
        
        composable("folders") {
            // FoldersScreen would be implemented here
            navController.popBackStack()
        }
        
        composable("schedules") {
            ScheduleScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddSchedule = { navController.navigate("add_schedule") },
                onScheduleClick = { scheduleId ->
                    navController.navigate("schedule_detail/$scheduleId")
                }
            )
        }
        
        composable("add_schedule") {
            AddScheduleScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { schedule, documents ->
                    // Save logic handled by ViewModel
                    navController.popBackStack()
                }
            )
        }
        
        composable("settings") {
            // SettingsScreen would be implemented here
            navController.popBackStack()
        }
    }
}

// Placeholder screens for quick implementation

@Composable
fun SplashScreen(onNavigateToAuth: () -> Unit) {
    // Simple splash implementation
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "DigiVault",
            style = MaterialTheme.typography.displayLarge
        )
    }
    
    // Auto-navigate after 2 seconds
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onNavigateToAuth()
    }
}

@Composable
fun AuthScreen(onNavigateToHome: () -> Unit) {
    // Simple auth screen
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = "Welcome to DigiVault",
            style = MaterialTheme.typography.headlineLarge
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))
        androidx.compose.material3.Button(onClick = onNavigateToHome) {
            androidx.compose.material3.Text("Get Started")
        }
    }
}
