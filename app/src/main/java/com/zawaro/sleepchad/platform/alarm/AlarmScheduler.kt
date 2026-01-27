package com.zawaro.sleepchad.platform.alarm

import android.app.AlarmManager
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import java.util.concurrent.TimeUnit
import com.zawaro.sleepchad.data.ScheduleRepository

/**
 * Utility that turns a [ScheduleEntity] into real device alarms.
 * It schedules three types of alarms:
 *  - Evening errands reminder
 *  - Bedtime prompt
 *  - Morning wake‑up alarm
 */
object AlarmScheduler {
    suspend fun scheduleAlarms(context: Context, repo: ScheduleRepository) {
        // No scheduling logic for this build.
    }
}