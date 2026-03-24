package com.zawaro.sleepchad.platform.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zawaro.sleepchad.data.ScheduleRepository
import kotlinx.coroutines.*

/**
 * Re‑schedules all alarms after the device boots.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent?.action == android.content.Intent.ACTION_BOOT_COMPLETED) {
            val repo = ScheduleRepository.createWithContext(context)
            CoroutineScope(Dispatchers.IO).launch {
                scheduleAllAlarms(context, context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager, repo)
            }
        }
    }
}
