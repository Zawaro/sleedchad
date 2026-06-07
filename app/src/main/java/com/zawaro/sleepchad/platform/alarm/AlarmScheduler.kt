package com.zawaro.sleepchad.platform.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.zawaro.sleepchad.data.ErrandDao
import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.utils.toDaysSet
import kotlinx.coroutines.withContext
import java.util.Calendar

/** Schedules all alarms based on the provided schedules. */
suspend fun scheduleAllAlarms(
    context: Context,
    alarmMgr: AlarmManager,
    repo: ScheduleRepository,
) = withContext(kotlinx.coroutines.Dispatchers.IO) {
    // Get default and exception alarms from repository
    val defaultAlarm = repo.getDefaultAlarm()
    val exceptionAlarms = repo.getExceptionAlarms()
    val userPrefs = repo.getUserPreferences()

    // Calculate which days are covered by exceptions
    val exceptionDays = mutableSetOf<Int>()
    for (exception in exceptionAlarms) {
        exceptionDays.addAll(exception.enabledDaysString.toDaysSet())
    }

    // Schedule exception alarms first (higher priority)
    for (alarm in exceptionAlarms) {
        scheduleAlarmForDays(
            context,
            alarmMgr,
            alarm,
            alarm.enabledDaysString.toDaysSet(),
            repo
        )
    }

    // Schedule default alarm for uncovered days only
    if (defaultAlarm != null && exceptionDays.isNotEmpty()) {
        val uncoveredDays = mutableSetOf<Int>()
        for (day in 1..7) {
            if (!exceptionDays.contains(day)) {
                uncoveredDays.add(day)
            }
        }
        
        if (uncoveredDays.isNotEmpty()) {
            scheduleAlarmForDays(
                context,
                alarmMgr,
                defaultAlarm,
                uncoveredDays,
                repo,
                userPrefs
            )
        }
    } else if (defaultAlarm != null && exceptionDays.isEmpty()) {
        // No exceptions - schedule for all days
        scheduleAlarmForDays(
            context,
            alarmMgr,
            defaultAlarm,
            setOf(1, 2, 3, 4, 5, 6, 7),
            repo,
            userPrefs
        )
    }
}

private suspend fun scheduleAlarmForDays(
    context: Context,
    alarmMgr: AlarmManager,
    alarm: ScheduleEntity,
    days: Set<Int>,
    repo: ScheduleRepository,
    userPrefs: com.zawaro.sleepchad.data.UserPreferencesEntity? = null,
) {
    for (day in days) {
        val calendarDay = dayOfWeekToCalendar(day)

        // Apply weekend recovery override for default alarm on Sat(6)/Sun(7)
        val effectiveBedtimeMs = if (alarm.isDefaultAlarm && userPrefs?.weekendRecoveryEnabled == true && (day == 6 || day == 7)) {
            userPrefs.weekendWakeUpTimeMs?.let { weekendWake ->
                userPrefs.weekendTargetSleepDurationMinutes?.let { weekendSleep ->
                    val weekendWakeCal = java.util.Calendar.getInstance().apply { timeInMillis = weekendWake }
                    val totalWakeMinutes = (weekendWakeCal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + weekendWakeCal.get(java.util.Calendar.MINUTE)
                    var totalBedMinutes = totalWakeMinutes - weekendSleep
                    if (totalBedMinutes < 0) totalBedMinutes += 1440
                    (totalBedMinutes * 60000L)
                } ?: alarm.bedtimeMs
            } ?: alarm.bedtimeMs
        } else {
            alarm.bedtimeMs
        }

        val effectiveWakeupMs = if (alarm.isDefaultAlarm && userPrefs?.weekendRecoveryEnabled == true && (day == 6 || day == 7)) {
            userPrefs.weekendWakeUpTimeMs ?: alarm.wakeupMs
        } else {
            alarm.wakeupMs
        }

        // Bedtime alarm and errands reminders
        if (effectiveBedtimeMs != null) {
            val bedtimeDay = if (day == 1) 7 else day - 1
            val bedtimeCalendarDay = dayOfWeekToCalendar(bedtimeDay)
            val bedtimeCal = nextOccurrence(bedtimeCalendarDay, effectiveBedtimeMs)

            scheduleAlarm(
                context,
                alarmMgr,
                AlarmReceiver.TYPE_BEDTIME,
                alarm.id,
                bedtimeCal.timeInMillis,
                "${alarm.name} - Bedtime",
                dayOfWeek = day,
            )

            val errands = repo.getErrandsForAlarm(alarm.id)
            for (errand in errands) {
                val errandsCal = bedtimeCal.clone() as java.util.Calendar
                errandsCal.add(
                    java.util.Calendar.MINUTE,
                    -errand.minutesBeforeBedtime
                )
                scheduleAlarm(
                    context,
                    alarmMgr,
                    AlarmReceiver.TYPE_ERRANDS,
                    alarm.id,
                    errandsCal.timeInMillis,
                    "${alarm.name} - ${errand.title}",
                    dayOfWeek = day,
                )
            }
        }

        // Wake-up alarm
        if (effectiveWakeupMs != null) {
            val wakeupCal = nextOccurrence(calendarDay, effectiveWakeupMs)
            scheduleAlarm(
                context,
                alarmMgr,
                AlarmReceiver.TYPE_WAKEUP,
                alarm.id,
                wakeupCal.timeInMillis,
                "${alarm.name} - Wake-up",
                dayOfWeek = day,
            )
        }
    }
}

private fun dayOfWeekToCalendar(day: Int): Int =
    when (day) {
        1 -> Calendar.MONDAY
        2 -> Calendar.TUESDAY
        3 -> Calendar.WEDNESDAY
        4 -> Calendar.THURSDAY
        5 -> Calendar.FRIDAY
        6 -> Calendar.SATURDAY
        7 -> Calendar.SUNDAY
        else -> throw IllegalArgumentException("Invalid day: $day")
    }

private fun nextOccurrence(dayOfWeek: Int, timeMs: Long): java.util.Calendar {
    val now = Calendar.getInstance()
    
    // Extract hour/minute from the epoch timestamp
    val timeCal = Calendar.getInstance().apply {
        timeInMillis = timeMs
    }
    val targetHour = timeCal.get(Calendar.HOUR_OF_DAY)
    val targetMinute = timeCal.get(Calendar.MINUTE)
    
    val target = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
        set(Calendar.HOUR_OF_DAY, targetHour)
        set(Calendar.MINUTE, targetMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If target time has already passed today, schedule for next occurrence of this day
    if (target.timeInMillis < now.timeInMillis) {
        target.add(Calendar.DAY_OF_YEAR, 7)
    }

    return target
}

private fun scheduleAlarm(
    context: Context,
    alarmMgr: AlarmManager,
    type: Int,
    alarmId: Long,
    triggerAtMillis: Long,
    label: String,
    dayOfWeek: Int? = null,
) {
    val intent = android.content.Intent(context, AlarmReceiver::class.java).apply {
        putExtra("type", type)
        putExtra("message", label)
        putExtra("alarm_id", alarmId)
        putExtra("label", label)
        dayOfWeek?.let { putExtra("day_of_week", it) }
    }

    // Use exact timing for alarms (API 19+)
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    
    val requestCode = if (dayOfWeek != null) {
        ((type.hashCode() + dayOfWeek) * 1000 + alarmId.toInt()).and(Int.MAX_VALUE)
    } else {
        (type.hashCode() * 1000 + alarmId.toInt()).and(Int.MAX_VALUE)
    }
    
    val pendingIntent = android.app.PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        flags,
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Use setExactAndAllowWhileIdle for exact timing
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    } else {
        alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}
