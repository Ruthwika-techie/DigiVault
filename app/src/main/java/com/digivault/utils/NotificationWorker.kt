package com.digivault.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.digivault.R
import com.digivault.data.model.Schedule
import com.digivault.data.model.ScheduleStatus
import com.digivault.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            checkAndSendNotifications()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun checkAndSendNotifications() {
        // This would need dependency injection in a real app
        // For now, showing the logic structure
        
        val currentTime = System.currentTimeMillis()
        val sevenDaysLater = currentTime + (7 * 24 * 60 * 60 * 1000)
        
        // Get upcoming schedules from repository
        // val upcomingSchedules = scheduleRepository.getUpcomingSchedules().first()
        
        // Check each schedule and send notifications
        // upcomingSchedules.forEach { schedule ->
        //     val daysUntil = calculateDaysUntil(schedule.eventDate)
        //     if (daysUntil <= schedule.notificationTime && schedule.reminderEnabled) {
        //         sendNotification(schedule, daysUntil)
        //     }
        // }
    }

    private fun sendNotification(schedule: Schedule, daysUntil: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Schedule Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming schedules and events"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open the app
        val intent = Intent(applicationContext, Class.forName("com.digivault.MainActivity"))
        intent.putExtra("scheduleId", schedule.id)
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notificationText = when {
            daysUntil == 0 -> "Today: ${schedule.title}"
            daysUntil == 1 -> "Tomorrow: ${schedule.title}"
            else -> "In $daysUntil days: ${schedule.title}"
        }
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(schedule.category.name.replace("_", " "))
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(schedule.description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(schedule.id.toInt(), notification)
    }
    
    private fun calculateDaysUntil(eventDate: Long): Int {
        val diff = eventDate - System.currentTimeMillis()
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    companion object {
        private const val CHANNEL_ID = "schedule_notifications"
        private const val WORK_NAME = "schedule_notification_work"
        
        fun scheduleNotificationCheck(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ScheduleNotificationWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInitialDelay(0, TimeUnit.SECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun cancelNotificationCheck(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

object NotificationHelper {
    
    fun scheduleNotificationForSchedule(context: Context, schedule: Schedule) {
        val daysBeforeEvent = schedule.notificationTime
        val notificationTime = schedule.eventDate - (daysBeforeEvent * 24 * 60 * 60 * 1000)
        val delay = notificationTime - System.currentTimeMillis()
        
        if (delay > 0 && schedule.reminderEnabled) {
            val workRequest = OneTimeWorkRequestBuilder<ScheduleNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "scheduleId" to schedule.id,
                        "scheduleTitle" to schedule.title
                    )
                )
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "schedule_${schedule.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
    
    fun cancelNotificationForSchedule(context: Context, scheduleId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("schedule_$scheduleId")
    }
}
