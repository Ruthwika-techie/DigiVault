# DigiVault
DigiVault is a secure digital locker that stores and organizes important documents in one place, making them easy to access anytime while keeping them safe.The main purpose of DigiVault is to eliminate the need for physical files and scattered digital folders. Instead of searching across devices or worrying about losing important documents, users can keep everything stored securely within the app. DigiVault ensures privacy through safe login authentication and protected storage mechanisms that prevent unauthorized access.
# Problem Statement
Today everyone handles many important documents: driving license, Aadhaar/PAN, insurance papers, college certificates, vehicle documents, travel documents, resumes, scholarship forms, interview schedules, etc.
The issue is not storing these documents. Anyone can keep them in Google Drive or their phone gallery.
The real problem is:
- forgetting expiry dates
- missing renewals
- paying fines because something expired
- losing track of interview or scholarship dates
- documents being scattered everywhere
- needing a document urgently when there is no internet
Most apps store files, but they don’t solve the main difficulty: reminding users at the right time and helping them stay organized.
# Idea Description — DigiVault
DigiVault is a simple and smart document-management app designed to help people stay organised. Instead of only storing images or PDFs, the app mainly focuses on tracking expiry dates and reminding the user before something is due.
The app works offline and keeps all files safely on the user’s device. The intention is to build something lightweight and practical for everyday use. No unnecessary complexity, just features that actually solve the problem.
# Base Features (Non-Negotiables)
- Document Storage (Offline)
Users can store photos or PDFs of their documents in clear categories.
- Expiry Tracking
When a user adds a document, they select the expiry date. The app schedules reminders automatically.
- Smart Notifications
Reminders are sent before expiry — one month before, one week before, and three days before.
- Notes Section
Users can add important notes such as scholarship deadlines, interview dates, exam reminders, and attach related documents like resumes or portfolios.
The app sends a reminder one week before.
- Biometric Lock
Fingerprint or face unlock to keep documents private.
- Offline Access
Everything works without internet, so documents are always reachable.
- Clean Dashboard
Shows all upcoming expiry dates and urgent documents clearly.
- Category System
IDs, Vehicle, Education, Insurance, Travel, Work-related, etc.
# Good-to-Have Features (Optional but Valuable)
- Folder and Subfolder Structure
Allows users to organise documents better than normal drive apps.
- Live Card Linking
Linking Aadhaar, PAN, RC, or insurance cards to their official websites for quick renewal or verification.
- OCR (Auto Date Reading)
Reads expiry dates automatically from photos.
- QR-Based Sharing
Share a document securely using a QR.
- Emergency Vault
Important documents can be accessed even without network.
- Multiple Language Support
Designed so everyone can comfortably use it.
- Dark Mode
Simple viewing in low light.
# Tech Stack
# Frontend (App UI)
- Kotlin
- Android Studio
- Material Design Components
# Backend (App Logic)
- Room Database for storing data
- WorkManager for scheduling reminders
- AndroidX Biometric Library for lock
- CameraX for capturing document photos
- ML Kit OCR (optional)
# Architecture
- MVVM (Model-View-ViewModel)
- Model: Room data
- View: Android UI
- ViewModel: Handles reminders and business logic.
# Timeline
- 7 days
# TeamName-PixelDivas - 06
- Teammate 1:Thota.Ruthwika 24WH1A0540 CSE-A
- Teammate 2:Daggu.Nandini 24WH1A0543 CSE-A
- Teammate 3:Subhi Saxena CSE-D
