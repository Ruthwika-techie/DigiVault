# DigiVault Implementation Guide

## Complete Android Studio Setup Guide

### Step 1: Project Setup

1. **Create New Project Structure**
   ```bash
   # Extract the provided code to your desired location
   # The structure should look like:
   DigiVault/
   ├── app/
   │   ├── build.gradle.kts
   │   ├── src/
   │   └── ...
   ├── build.gradle.kts
   ├── settings.gradle.kts
   └── gradle.properties
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the DigiVault folder
   - Wait for Gradle sync to complete

### Step 2: Add Missing Resources

#### Create strings.xml
Location: `app/src/main/res/values/strings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">DigiVault</string>
    <string name="notification_channel_name">Schedule Reminders</string>
    <string name="notification_channel_description">Notifications for upcoming events and schedules</string>
</resources>
```

#### Create themes.xml
Location: `app/src/main/res/values/themes.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.DigiVault" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

#### Create notification icon
Location: `app/src/main/res/drawable/ic_notification.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.89,2 2,2zM18,16v-5c0,-3.07 -1.64,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5v0.68C7.63,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
```

#### Create backup and data extraction rules
Location: `app/src/main/res/xml/backup_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <include domain="file" path="." />
</full-backup-content>
```

Location: `app/src/main/res/xml/data_extraction_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <include domain="file" path="." />
    </cloud-backup>
</data-extraction-rules>
```

### Step 3: Using the Schedule Feature

#### Creating a Schedule with Documents

```kotlin
// In your ViewModel or Activity
val scheduleViewModel: ScheduleViewModel = hiltViewModel()

// Create a job interview schedule
val jobInterview = Schedule(
    title = "Software Engineer Interview",
    description = "Technical interview with Google",
    category = ScheduleCategory.JOB_INTERVIEW,
    eventDate = System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000), // 10 days from now
    location = "Google Campus, Mountain View",
    reminderEnabled = true,
    notificationTime = 7 // Notify 7 days before
)

// Attach resume and portfolio
val resume = ScheduleDocument(
    scheduleId = 0, // Will be set after saving
    documentName = "John_Doe_Resume.pdf",
    documentType = ScheduleDocumentType.RESUME,
    filePath = "/path/to/resume.pdf"
)

val portfolio = ScheduleDocument(
    scheduleId = 0,
    documentName = "Portfolio_Website.pdf",
    documentType = ScheduleDocumentType.PORTFOLIO,
    filePath = "/path/to/portfolio.pdf"
)

// Save schedule with documents
scheduleViewModel.addSchedule(jobInterview, listOf(resume, portfolio))
```

#### Creating Different Types of Schedules

**Scholarship Application:**
```kotlin
val scholarship = Schedule(
    title = "Gates Scholarship Application",
    description = "Submit all required documents",
    category = ScheduleCategory.SCHOLARSHIP,
    eventDate = submissionDeadline,
    location = "Online",
    reminderEnabled = true,
    notificationTime = 14 // Notify 2 weeks before
)

val transcript = ScheduleDocument(
    scheduleId = 0,
    documentName = "Official_Transcript.pdf",
    documentType = ScheduleDocumentType.TRANSCRIPT,
    filePath = "/path/to/transcript.pdf"
)

val certificate = ScheduleDocument(
    scheduleId = 0,
    documentName = "Achievement_Certificate.pdf",
    documentType = ScheduleDocumentType.CERTIFICATE,
    filePath = "/path/to/certificate.pdf"
)

scheduleViewModel.addSchedule(scholarship, listOf(transcript, certificate))
```

**Exam Schedule:**
```kotlin
val exam = Schedule(
    title = "Data Structures Final Exam",
    description = "Comprehensive exam covering all topics",
    category = ScheduleCategory.EXAM,
    eventDate = examDate,
    location = "Room 101, Engineering Building",
    reminderEnabled = true,
    notificationTime = 3 // Notify 3 days before
)

scheduleViewModel.addSchedule(exam, emptyList())
```

### Step 4: Working with Documents

#### Uploading a Document

```kotlin
// Using FileUtils
val fileUtils = FileUtils
val context = LocalContext.current

// When user selects a file
fun handleFileSelection(uri: Uri) {
    val documentsDir = fileUtils.getDocumentsDirectory(context)
    val copiedFile = fileUtils.copyFileToInternalStorage(
        context,
        uri,
        documentsDir,
        fileUtils.generateUniqueFileName("my_document.pdf")
    )
    
    if (copiedFile != null) {
        val document = Document(
            name = copiedFile.name,
            filePath = copiedFile.absolutePath,
            fileType = fileUtils.getFileType(context, uri),
            folderId = selectedFolderId,
            tags = "important, work",
            expiryDate = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000) // 30 days
        )
        
        documentViewModel.addDocument(document)
    }
}
```

#### Generating QR Code for Document

```kotlin
// In your composable or activity
val qrBitmap = remember(documentId) {
    QRCodeGenerator.generateDocumentQRCode(documentId, documentName)
}

// Display QR code
qrBitmap?.let { bitmap ->
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Document QR Code",
        modifier = Modifier.size(200.dp)
    )
}
```

### Step 5: Implementing Biometric Authentication

```kotlin
// In your AuthViewModel or Activity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun setupBiometricAuth(activity: FragmentActivity, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Handle failure
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Handle error
            }
        }
    )
    
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authenticate to DigiVault")
        .setSubtitle("Use your fingerprint or face")
        .setNegativeButtonText("Use PIN")
        .build()
    
    biometricPrompt.authenticate(promptInfo)
}
```

### Step 6: Setting Up Notifications

The notification system is automatically set up when the app starts. Here's how it works:

```kotlin
// In MainActivity onCreate()
ScheduleNotificationWorker.scheduleNotificationCheck(this)

// This will:
// 1. Check for upcoming events every 24 hours
// 2. Send notifications 7 days (or custom days) before events
// 3. Show color-coded urgency (Today/Tomorrow/Soon)
```

#### Manual Notification for Specific Schedule

```kotlin
// Schedule notification for a specific event
NotificationHelper.scheduleNotificationForSchedule(context, schedule)

// Cancel notification if event is deleted
NotificationHelper.cancelNotificationForSchedule(context, scheduleId)
```

### Step 7: Database Queries Examples

#### Get Upcoming Events

```kotlin
// In your ViewModel
fun getUpcomingEvents() {
    viewModelScope.launch {
        scheduleRepository.getUpcomingSchedules()
            .collect { schedules ->
                // Filter by urgency
                val todayEvents = schedules.filter { isToday(it.eventDate) }
                val thisWeekEvents = schedules.filter { 
                    getDaysUntil(it.eventDate) <= 7 
                }
                
                // Update UI
                _upcomingEvents.value = schedules
            }
    }
}
```

#### Search Documents

```kotlin
fun searchDocuments(query: String) {
    viewModelScope.launch {
        documentRepository.searchDocuments(query)
            .collect { results ->
                _searchResults.value = results
            }
    }
}
```

#### Get Documents by Folder

```kotlin
fun loadFolderDocuments(folderId: Long) {
    viewModelScope.launch {
        documentRepository.getDocumentsByFolder(folderId)
            .collect { documents ->
                _folderDocuments.value = documents
            }
    }
}
```

### Step 8: Customizing the App

#### Change Primary Colors

In `ui/theme/Theme.kt`:

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),        // Blue
    secondary = Color(0xFF388E3C),      // Green
    tertiary = Color(0xFFE91E63),       // Pink
    // ... other colors
)
```

#### Add New Schedule Category

1. Add to enum in `Models.kt`:
```kotlin
enum class ScheduleCategory {
    JOB_INTERVIEW,
    SCHOLARSHIP,
    EXAM,
    ASSIGNMENT,
    INTERNSHIP,  // New
    CONFERENCE,  // New
    OTHER
}
```

2. Add icon in `ScheduleScreen.kt`:
```kotlin
ScheduleCategory.INTERNSHIP -> Icons.Default.BusinessCenter
ScheduleCategory.CONFERENCE -> Icons.Default.Event
```

3. Add tab in `CategoryTabs`:
```kotlin
Tab(
    selected = selectedCategory == ScheduleCategory.INTERNSHIP,
    onClick = { onCategorySelected(ScheduleCategory.INTERNSHIP) },
    text = { Text("Internships") },
    icon = { Icon(Icons.Default.BusinessCenter, contentDescription = null) }
)
```

#### Change Notification Timing

In `AddScheduleScreen.kt`, modify the default:

```kotlin
var notificationDays by remember { mutableStateOf(14) } // 2 weeks before instead of 7 days
```

### Step 9: Testing the App

#### Test Schedule Notifications

```kotlin
// Create a test schedule for tomorrow
val testSchedule = Schedule(
    title = "Test Event",
    description = "Testing notification system",
    category = ScheduleCategory.OTHER,
    eventDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // Tomorrow
    reminderEnabled = true,
    notificationTime = 1 // Notify 1 day before (which is today)
)

scheduleViewModel.addSchedule(testSchedule)
```

#### Debug Database

```kotlin
// In Android Studio, use Database Inspector:
// View -> Tool Windows -> App Inspection -> Database Inspector
// You can see all tables, run queries, and inspect data
```

### Step 10: Common Issues and Solutions

#### Issue: Notifications Not Showing

**Solution 1: Check Permissions**
```kotlin
// In MainActivity
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) 
        != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}
```

**Solution 2: Disable Battery Optimization**
```kotlin
val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
    data = Uri.parse("package:$packageName")
}
startActivity(intent)
```

#### Issue: Database Migration Errors

The app uses `fallbackToDestructiveMigration()`, which means:
- Database will be recreated if structure changes
- All data will be lost during migration
- For production, implement proper migrations

```kotlin
// In DigiVaultDatabase.kt
// Replace fallbackToDestructiveMigration() with proper migrations:
.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
```

#### Issue: File Permission Errors

```kotlin
// Request permissions before accessing files
val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.CAMERA
    )
} else {
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
}

ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
```

### Step 11: Advanced Features Implementation

#### Cloud Sync with Supabase (Optional)

```kotlin
// Add to build.gradle.kts
implementation("io.github.jan-tennert.supabase:postgrest-kt:1.0.0")
implementation("io.github.jan-tennert.supabase:storage-kt:1.0.0")

// Create SupabaseClient
val supabase = createSupabaseClient(
    supabaseUrl = "YOUR_SUPABASE_URL",
    supabaseKey = "YOUR_SUPABASE_KEY"
) {
    install(Postgrest)
    install(Storage)
}

// Sync documents
suspend fun syncDocuments() {
    val localDocuments = documentRepository.getAllDocuments().first()
    
    localDocuments.forEach { document ->
        supabase.postgrest["documents"].insert(document)
    }
}
```

#### OCR Text Extraction (Optional)

```kotlin
// Add ML Kit dependency
implementation("com.google.mlkit:text-recognition:16.0.0")

// Extract text from image
fun extractTextFromImage(bitmap: Bitmap, onSuccess: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bitmap, 0)
    
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            onSuccess(visionText.text)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}
```

### Step 12: Building Release APK

1. **Generate Signing Key**
   ```bash
   keytool -genkey -v -keystore digivault.keystore -alias digivault -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configure Signing in build.gradle.kts**
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("digivault.keystore")
               storePassword = "your_password"
               keyAlias = "digivault"
               keyPassword = "your_password"
           }
       }
       
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               isMinifyEnabled = true
               proguardFiles(
                   getDefaultProguardFile("proguard-android-optimize.txt"),
                   "proguard-rules.pro"
               )
           }
       }
   }
   ```

3. **Build APK**
   ```bash
   ./gradlew assembleRelease
   # APK will be in: app/build/outputs/apk/release/
   ```

### Tips for Success

1. **Use Type-Safe Navigation**: Consider migrating to Navigation Compose with type-safe arguments
2. **Add Unit Tests**: Test ViewModels and Repositories
3. **Add UI Tests**: Test critical user flows
4. **Implement Proper Error Handling**: Show user-friendly error messages
5. **Add Analytics**: Track feature usage (with user consent)
6. **Optimize Performance**: Use LazyColumn efficiently, cache images
7. **Accessibility**: Add content descriptions, support TalkBack
8. **Localization**: Add support for multiple languages
9. **Dark Theme**: Complete dark mode implementation
10. **Backup/Restore**: Allow users to export/import data

---

For more help, refer to:
- [Android Developers Guide](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [WorkManager Documentation](https://developer.android.com/topic/libraries/architecture/workmanager)
