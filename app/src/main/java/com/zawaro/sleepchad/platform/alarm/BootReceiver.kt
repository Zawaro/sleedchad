package com.zawaro.sleepchad.platform.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.zawaro.sleepchad.data.ScheduleRepository
import kotlinx.coroutines.*

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent?.action == android.content.Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            val repo = ScheduleRepository.createWithContext(context)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    scheduleAllAlarms(context, context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager, repo)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
