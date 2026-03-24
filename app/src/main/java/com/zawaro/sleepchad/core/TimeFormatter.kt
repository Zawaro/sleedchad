package com.zawaro.sleepchad.core

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeFormatter {
    private const val TIME_FORMAT_24H = "HH:mm"
    private const val TIME_FORMAT_12H = "hh:mm a"

    fun formatTime(context: Context, timeMs: Long?, use24HourFormat: Boolean?): String {
        if (timeMs == null) return "--:--"

        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
        }

        val shouldUse24Hour = use24HourFormat ?: isSystemUsing24HourFormat(context)
        val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H
        
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMs))
    }

    fun formatTimeWithTimeZone(context: Context, timeMs: Long?, use24HourFormat: Boolean?): String {
        if (timeMs == null) return "--:--"

        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val shouldUse24Hour = use24HourFormat ?: isSystemUsing24HourFormat(context)
        val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H
        
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeMs))
    }

    fun parseTime(context: Context, timeString: String): Long? {
        try {
            val shouldUse24Hour = isSystemUsing24HourFormat(context)
            val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H
            
            val calendar = Calendar.getInstance()
            calendar.time = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeString) ?: return null
            
            return calendar.timeInMillis
        } catch (e: Exception) {
            return null
        }
    }

    fun isSystemUsing24HourFormat(context: Context): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }

    fun formatDuration(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "0h"
        
        val hours = minutes / 60
        val mins = minutes % 60
        
        return when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }

    fun formatDurationVerbose(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "0 minutes"
        
        val hours = minutes / 60
        val mins = minutes % 60
        
        return when {
            hours > 0 && mins > 0 -> "$hours hours and $mins minutes"
            hours > 0 -> "$hours hours"
            else -> "$mins minutes"
        }
    }

    fun getMinutesFromTime(timeMs: Long): Int {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
        }
        return (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)
    }
}
