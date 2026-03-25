package com.zawaro.sleepchad.core

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object TimeFormatter {
    private const val TIME_FORMAT_24H = "HH:mm"
    private const val TIME_FORMAT_12H = "hh:mm a"

    fun formatTime(
        context: Context,
        timeMs: Long?,
        use24HourFormat: Boolean?
    ): String {
        if (timeMs == null) return "--:--"

        // Check if this is epoch milliseconds or time-of-day in ms
        val calendar = Calendar.getInstance(Locale.US).apply {
            // If value is less than a day's worth of ms, treat as time-only
            if (timeMs < 86400000L) {
                // Time-only value: set to today's date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                timeInMillis += timeMs
            } else {
                // Full epoch timestamp
                timeInMillis = timeMs
            }
        }

        val shouldUse24Hour = use24HourFormat ?: isSystemUsing24HourFormat(context)
        val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H

        return SimpleDateFormat(pattern, Locale.US).format(calendar.time)
    }

    fun formatTimeWithTimeZone(
        context: Context,
        timeMs: Long?,
        use24HourFormat: Boolean?
    ): String {
        if (timeMs == null) return "--:--"

        val calendar = Calendar.getInstance(Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")

            // Handle time-only vs epoch timestamp
            if (timeMs < 86400000L) {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                timeInMillis += timeMs
            } else {
                timeInMillis = timeMs
            }
        }

        val shouldUse24Hour = use24HourFormat ?: isSystemUsing24HourFormat(context)
        val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H

        return SimpleDateFormat(pattern, Locale.US).format(calendar.time)
    }

    fun parseTime(
        context: Context,
        timeString: String
    ): Long? {
        try {
            val shouldUse24Hour = isSystemUsing24HourFormat(context)
            val pattern = if (shouldUse24Hour) TIME_FORMAT_24H else TIME_FORMAT_12H

            val calendar = Calendar.getInstance()
            calendar.time = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeString) ?: return null

            // Return epoch milliseconds for consistency
            return calendar.timeInMillis
        } catch (e: Exception) {
            return null
        }
    }

    fun isSystemUsing24HourFormat(context: Context): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }

    fun formatDuration(minutes: Int?) = if (minutes == null || minutes <= 0) {
        "0h"
    } else {
        val hours = minutes / 60
        val mins = minutes % 60

        when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }

    fun formatDurationVerbose(minutes: Int?) = if (minutes == null || minutes <= 0) {
        "0 minutes"
    } else {
        val hours = minutes / 60
        val mins = minutes % 60

        when {
            hours > 0 && mins > 0 -> "$hours hours and $mins minutes"
            hours > 0 -> "$hours hours"
            else -> "$mins minutes"
        }
    }

    fun getMinutesFromTime(timeMs: Long): Int {
        val calendar = Calendar.getInstance(Locale.US).apply {
            // Handle time-only vs epoch timestamp
            if (timeMs < 86400000L) {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                timeInMillis += timeMs
            } else {
                timeInMillis = timeMs
            }
        }
        return (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)
    }
}
