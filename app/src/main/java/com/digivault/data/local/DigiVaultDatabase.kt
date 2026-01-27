package com.digivault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.digivault.data.model.*

@Database(
    entities = [
        Document::class,
        Card::class,
        Folder::class,
        Schedule::class,
        ScheduleDocument::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DigiVaultDatabase : RoomDatabase() {
    
    abstract fun documentDao(): DocumentDao
    abstract fun cardDao(): CardDao
    abstract fun folderDao(): FolderDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun scheduleDocumentDao(): ScheduleDocumentDao
    
    companion object {
        @Volatile
        private var INSTANCE: DigiVaultDatabase? = null
        
        fun getDatabase(context: Context): DigiVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DigiVaultDatabase::class.java,
                    "digivault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    // Add any type converters if needed for custom types
}
