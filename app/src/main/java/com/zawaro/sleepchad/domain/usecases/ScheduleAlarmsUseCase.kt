package com.zawaro.sleepchad.domain.usecases

import android.app.AlarmManager
import android.content.Context
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.platform.alarm.scheduleAllAlarms
import kotlinx.coroutines.withContext

/** Use case to schedule all alarms. */
class ScheduleAlarmsUseCase(
    private val context: Context,
    private val repo: ScheduleRepository,
) {
    suspend operator fun invoke() = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        scheduleAllAlarms(context, alarmMgr, repo)
    }
}

