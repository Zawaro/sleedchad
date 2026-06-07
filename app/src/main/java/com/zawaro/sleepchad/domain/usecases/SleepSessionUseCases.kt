package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.SleepSessionEntity
import com.zawaro.sleepchad.domain.repository.UserPreferencesRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecordBedtimeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(actualBedtimeMs: Long): SleepSessionEntity? {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        var session = repository.getSleepSessionByDate(today)
        
        if (session == null) {
            session = SleepSessionEntity(
                id = 0L,
                date = today,
                scheduledBedtimeMs = null,
                actualBedtimeMs = actualBedtimeMs,
                wakeUpTimeMs = null,
                actualWakeTimeMs = null,
                estimatedSleepDurationMinutes = null
            )
            repository.insertSleepSession(session)
        } else {
            session = session.copy(actualBedtimeMs = actualBedtimeMs)
            repository.updateSleepSession(session)
        }
        
        return session
    }
}

class RecordWakeUpUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(actualWakeTimeMs: Long): SleepSessionEntity? {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        var session = repository.getSleepSessionByDate(today)
        
        if (session == null) {
            return null
        }
        
        val actualBedtime = session.actualBedtimeMs ?: session.scheduledBedtimeMs
        val estimatedDuration = if (actualBedtime != null && actualWakeTimeMs > actualBedtime) {
            (((actualWakeTimeMs.toLong() - actualBedtime.toLong()) / (1000L * 60L))).toInt()
        } else {
            null
        }
        
        session = session.copy(
            wakeUpTimeMs = actualWakeTimeMs,
            actualWakeTimeMs = actualWakeTimeMs,
            estimatedSleepDurationMinutes = estimatedDuration
        )
        
        repository.updateSleepSession(session)
        
        return session
    }
}

class GetLastNightSleepSessionUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(): SleepSessionEntity? {
        val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
        return repository.getSleepSessionByDate(yesterday)
    }
}
