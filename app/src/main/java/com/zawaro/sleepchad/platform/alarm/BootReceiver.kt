package com.zawaro.sleepchad.platform.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.platform.alarm.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Re‑schedules all alarms after the device boots.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == android.content.Intent.ACTION_BOOT_COMPLETED) {
            val repo = ScheduleRepository.createWithContext(context)
            CoroutineScope(Dispatchers.IO).launch {
                AlarmScheduler.scheduleAlarms(context, repo)
            }
        }
    }
}
