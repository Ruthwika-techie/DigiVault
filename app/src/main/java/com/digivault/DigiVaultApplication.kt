package com.digivault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DigiVaultApplication : Application()

// DI Modules
package com.digivault.di

import android.content.Context
import com.digivault.data.local.DigiVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DigiVaultDatabase {
        return DigiVaultDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideDocumentDao(database: DigiVaultDatabase) = database.documentDao()
    
    @Provides
    fun provideCardDao(database: DigiVaultDatabase) = database.cardDao()
    
    @Provides
    fun provideFolderDao(database: DigiVaultDatabase) = database.folderDao()
    
    @Provides
    fun provideScheduleDao(database: DigiVaultDatabase) = database.scheduleDao()
    
    @Provides
    fun provideScheduleDocumentDao(database: DigiVaultDatabase) = database.scheduleDocumentDao()
}
