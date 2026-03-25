package com.zawaro.sleepchad.platform.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zawaro.sleepchad.data.SleepSessionEntity
import com.zawaro.sleepchad.data.SleepSessionRepository
import com.zawaro.sleepchad.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WakeUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(context)
            val sleepSessionDao = database.sleepSessionDao()
            val sleepSessionRepository = SleepSessionRepository(sleepSessionDao)
            
            val currentTimeMillis = System.currentTimeMillis()
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(currentTimeMillis))
            
            val sessionEntity = SleepSessionEntity(
                id = 0,
                date = dateStr,
                scheduledBedtimeMs = null,
                actualBedtimeMs = null,
                wakeUpTimeMs = currentTimeMillis,
                actualWakeTimeMs = currentTimeMillis,
                estimatedSleepDurationMinutes = null
            )
            
            sleepSessionRepository.insert(sessionEntity)
        }
    }
}
