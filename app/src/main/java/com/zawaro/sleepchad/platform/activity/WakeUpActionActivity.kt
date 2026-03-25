package com.zawaro.sleepchad.platform.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zawaro.sleepchad.data.SleepSessionEntity
import com.zawaro.sleepchad.data.SleepSessionRepository
import com.zawaro.sleepchad.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WakeUpActionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(applicationContext)
            val sleepSessionDao = database.sleepSessionDao()
            val sleepSessionRepository = SleepSessionRepository(sleepSessionDao)
            
            val currentTimeMillis = System.currentTimeMillis()
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(currentTimeMillis))
            
            val existingSession = sleepSessionRepository.getByDate(dateStr)
            
            val sessionEntity = if (existingSession != null) {
                existingSession.copy(
                    wakeUpTimeMs = currentTimeMillis,
                    actualWakeTimeMs = currentTimeMillis
                )
            } else {
                SleepSessionEntity(
                    id = 0L,
                    date = dateStr,
                    scheduledBedtimeMs = null,
                    actualBedtimeMs = null,
                    wakeUpTimeMs = currentTimeMillis,
                    actualWakeTimeMs = currentTimeMillis,
                    estimatedSleepDurationMinutes = null
                )
            }
            
            if (existingSession == null) {
                sleepSessionRepository.insert(sessionEntity)
            } else {
                sleepSessionRepository.update(sessionEntity)
            }
        }
        
        finish()
    }
}
