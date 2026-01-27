package com.digivault.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    
    private const val DATE_FORMAT = "MMM dd, yyyy"
    private const val DATE_TIME_FORMAT = "MMM dd, yyyy hh:mm a"
    private const val TIME_FORMAT = "hh:mm a"
    
    /**
     * Format timestamp to readable date string
     */
    fun formatDate(timestamp: Long, pattern: String = DATE_FORMAT): String {
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Format timestamp to readable date and time string
     */
    fun formatDateTime(timestamp: Long): String {
        return formatDate(timestamp, DATE_TIME_FORMAT)
    }
    
    /**
     * Format timestamp to readable time string
     */
    fun formatTime(timestamp: Long): String {
        return formatDate(timestamp, TIME_FORMAT)
    }
    
    /**
     * Get the number of days between two timestamps
     */
    fun getDaysBetween(startTimestamp: Long, endTimestamp: Long): Int {
        val diff = endTimestamp - startTimestamp
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }
    
    /**
     * Get days until a future date
     */
    fun getDaysUntil(futureTimestamp: Long): Int {
        return getDaysBetween(System.currentTimeMillis(), futureTimestamp)
    }
    
    /**
     * Get days since a past date
     */
    fun getDaysSince(pastTimestamp: Long): Int {
        return getDaysBetween(pastTimestamp, System.currentTimeMillis())
    }
    
    /**
     * Check if date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val date = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return today.timeInMillis == date.timeInMillis
    }
    
    /**
     * Check if date is tomorrow
     */
    fun isTomorrow(timestamp: Long): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val date = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return tomorrow.timeInMillis == date.timeInMillis
    }
    
    /**
     * Check if date is in the past
     */
    fun isPast(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }
    
    /**
     * Check if date is in the future
     */
    fun isFuture(timestamp: Long): Boolean {
        return timestamp > System.currentTimeMillis()
    }
    
    /**
     * Get relative time string (e.g., "2 hours ago", "in 3 days")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = timestamp - now
        
        return when {
            isToday(timestamp) -> "Today"
            isTomorrow(timestamp) -> "Tomorrow"
            diff < 0 -> {
                // Past
                val daysPast = Math.abs(getDaysSince(timestamp))
                when {
                    daysPast == 1 -> "Yesterday"
                    daysPast < 7 -> "$daysPast days ago"
                    daysPast < 30 -> "${daysPast / 7} weeks ago"
                    daysPast < 365 -> "${daysPast / 30} months ago"
                    else -> "${daysPast / 365} years ago"
                }
            }
            else -> {
                // Future
                val daysFuture = getDaysUntil(timestamp)
                when {
                    daysFuture < 7 -> "In $daysFuture days"
                    daysFuture < 30 -> "In ${daysFuture / 7} weeks"
                    daysFuture < 365 -> "In ${daysFuture / 30} months"
                    else -> "In ${daysFuture / 365} years"
                }
            }
        }
    }
    
    /**
     * Get urgency status for schedules
     */
    fun getUrgencyStatus(eventTimestamp: Long): UrgencyStatus {
        val daysUntil = getDaysUntil(eventTimestamp)
        
        return when {
            isPast(eventTimestamp) -> UrgencyStatus.EXPIRED
            daysUntil == 0 -> UrgencyStatus.TODAY
            daysUntil == 1 -> UrgencyStatus.TOMORROW
            daysUntil <= 3 -> UrgencyStatus.URGENT
            daysUntil <= 7 -> UrgencyStatus.SOON
            else -> UrgencyStatus.UPCOMING
        }
    }
    
    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
    
    /**
     * Get timestamp for X days from now
     */
    fun getDaysFromNow(days: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis
    }
    
    /**
     * Get timestamp for X days before a date
     */
    fun getDaysBefore(timestamp: Long, days: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, -days)
        }.timeInMillis
    }
}

enum class UrgencyStatus {
    EXPIRED,
    TODAY,
    TOMORROW,
    URGENT,     // 2-3 days
    SOON,       // 4-7 days
    UPCOMING    // More than 7 days
}
