# DigiVault - Digital Document & Card Organizer

A comprehensive Android application for managing documents, cards, and schedules with intelligent notifications.

## Features

### 📄 Document Management
- Upload documents from device or camera
- Organize documents in custom folders
- Add tags and expiry reminders
- Search and filter documents
- View documents with share/download/QR options

### 💳 Card Manager
- Store ID cards, passports, licenses, credit cards
- Track card expiry dates
- Add card metadata (ID number, holder name)
- Link to official websites
- View card images (front/back)

### 📅 Schedule & Events
- **Job Interviews**: Attach resumes and portfolios
- **Scholarships**: Track deadlines and attach certificates
- **Exams**: Manage exam schedules with study materials
- **Assignments**: Track submission dates with documents
- Smart notifications (7 days before events)
- Color-coded urgency indicators
- Category-based organization

### 🔐 Security
- Biometric authentication (fingerprint/face)
- PIN code protection
- Secure local storage

### ⚙️ Settings
- Cloud sync toggle (requires Supabase setup)
- Language selection
- Theme preferences
- Notification management

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Async Operations**: Kotlin Coroutines & Flow
- **Background Tasks**: WorkManager
- **Camera**: CameraX
- **Authentication**: Biometric API
- **Navigation**: Jetpack Navigation Compose

## Project Structure

```
app/src/main/java/com/digivault/
├── data/
│   ├── local/
│   │   ├── DigiVaultDatabase.kt    # Room database
│   │   └── Daos.kt                 # Data Access Objects
│   ├── model/
│   │   └── Models.kt               # Data models
│   └── repository/
│       └── Repositories.kt         # Repository layer
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt           # Main dashboard
│   │   ├── ScheduleScreen.kt       # Events & schedules
│   │   └── ...                     # Other screens
│   ├── viewmodel/
│   │   └── ViewModels.kt           # UI logic
│   └── theme/
│       └── Theme.kt                # Material Design theme
├── utils/
│   └── NotificationWorker.kt       # Background notifications
├── di/
│   └── DatabaseModule.kt           # Dependency injection
├── DigiVaultApplication.kt         # Application class
└── MainActivity.kt                 # Entry point

```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum Android version: 7.0 (API 24)

### Installation

1. **Clone or extract the project**

2. **Open in Android Studio**
   - File → Open → Select the `digivault_android` folder

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - If prompted, accept SDK licenses

4. **Add Required Resources**

   Create `app/src/main/res/values/strings.xml`:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="app_name">DigiVault</string>
   </resources>
   ```

   Create `app/src/main/res/drawable/ic_notification.xml`:
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

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click Run (Shift + F10)

## Key Features Implementation

### 1. Schedule Notifications
The app uses WorkManager to check for upcoming events daily and sends notifications 7 days before scheduled events:

```kotlin
// Automatically scheduled in MainActivity
ScheduleNotificationWorker.scheduleNotificationCheck(context)
```

### 2. Document Categories
Documents can be organized into folders with custom colors:
- Work Documents
- Personal IDs
- Financial
- Medical Records
- Education
- Custom folders

### 3. Schedule Categories
Events are categorized as:
- **Job Interviews**: For career opportunities
- **Scholarships**: For educational funding
- **Exams**: For academic assessments
- **Assignments**: For coursework
- **Other**: For miscellaneous events

### 4. Document Attachments
Each schedule can have multiple attached documents:
- Resumes (for job interviews)
- Portfolios (for job interviews)
- Cover Letters (for applications)
- Certificates (for scholarships)
- Transcripts (for academic programs)

## Database Schema

### Documents Table
- id, name, filePath, fileType, folderId, tags, expiryDate, createdAt, updatedAt

### Cards Table
- id, cardName, cardNumber, cardType, holderName, issueDate, expiryDate, frontImagePath, backImagePath, officialWebsite, notes

### Folders Table
- id, name, icon, color, createdAt, documentCount

### Schedules Table
- id, title, description, category, eventDate, reminderEnabled, notificationTime, location, attachedDocuments, status

### Schedule Documents Table
- id, scheduleId, documentName, documentType, filePath, uploadedAt

## Customization

### Notification Timing
To change the default notification time (currently 7 days before):

```kotlin
// In AddScheduleScreen.kt
var notificationDays by remember { mutableStateOf(7) }
```

### Theme Colors
Modify colors in `ui/theme/Theme.kt`:

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),  // Change primary color
    // ... other colors
)
```

### Schedule Categories
Add new categories in `data/model/Models.kt`:

```kotlin
enum class ScheduleCategory {
    JOB_INTERVIEW,
    SCHOLARSHIP,
    EXAM,
    ASSIGNMENT,
    INTERNSHIP,  // New category
    OTHER
}
```

## Future Enhancements

1. **Cloud Sync with Supabase**
   - Backend integration for cross-device sync
   - Secure cloud storage
   - Real-time collaboration

2. **OCR Document Scanning**
   - Extract text from scanned documents
   - Auto-fill card details from images

3. **Advanced Search**
   - Full-text search across documents
   - Filter by multiple criteria

4. **Export/Import**
   - Backup data to external storage
   - Import from other apps

5. **Dark Mode**
   - Complete dark theme support
   - AMOLED black theme option

6. **Widgets**
   - Home screen widgets for quick access
   - Today's schedule widget

## Permissions Explained

- **CAMERA**: For scanning documents and cards
- **READ/WRITE_EXTERNAL_STORAGE**: For accessing device documents
- **READ_MEDIA_***: For accessing media files on Android 13+
- **POST_NOTIFICATIONS**: For schedule reminders
- **USE_BIOMETRIC**: For fingerprint/face authentication
- **INTERNET**: For future cloud sync features

## Troubleshooting

### Build Errors
1. Clean and rebuild: `Build → Clean Project → Rebuild Project`
2. Invalidate caches: `File → Invalidate Caches / Restart`
3. Update Gradle: Check for Android Studio updates

### Database Issues
The app uses `fallbackToDestructiveMigration()`, so the database will be recreated if schema changes. Production apps should use proper migrations.

### Notification Not Showing
1. Check notification permissions in device settings
2. Ensure battery optimization is disabled for the app
3. Verify WorkManager is running: `adb shell dumpsys jobscheduler`

## Contributing

This is a template project. Feel free to:
- Add new features
- Improve UI/UX
- Optimize performance
- Add tests
- Fix bugs

## License

This project is provided as a template for educational purposes.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review the code comments
3. Consult Android documentation: https://developer.android.com

---

Built with ❤️ using Jetpack Compose
