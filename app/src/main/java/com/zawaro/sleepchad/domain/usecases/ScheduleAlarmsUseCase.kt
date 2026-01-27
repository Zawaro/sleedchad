package com.zawaro.sleepchad.domain.usecases

import android.content.Context
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.platform.alarm.AlarmScheduler

/**
 * Use‑case that triggers the AlarmScheduler to (re)schedule alarms.
 */
class ScheduleAlarmsUseCase(
    private val context: Context,
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke() = AlarmScheduler.scheduleAlarms(context, repository)
}
