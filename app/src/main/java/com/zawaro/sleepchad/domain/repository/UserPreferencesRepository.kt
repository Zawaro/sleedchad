package com.zawaro.sleepchad.domain.repository

import com.zawaro.sleepchad.data.SleepSessionRepository
import com.zawaro.sleepchad.data.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val sleepSessionRepository: SleepSessionRepository,
) {
    suspend fun getUserPreferences(): com.zawaro.sleepchad.data.UserPreferencesEntity? = withContext(Dispatchers.IO) {
        scheduleRepository.getUserPreferences()
    }
    
    suspend fun saveUserPreferences(preferences: com.zawaro.sleepchad.data.UserPreferencesEntity): Long = withContext(Dispatchers.IO) {
        return@withContext scheduleRepository.saveUserPreferences(preferences)
    }
    
    suspend fun updateUserPreferences(preferences: com.zawaro.sleepchad.data.UserPreferencesEntity) = withContext(Dispatchers.IO) {
        scheduleRepository.updateUserPreferences(preferences)
    }

    suspend fun insertSleepSession(session: com.zawaro.sleepchad.data.SleepSessionEntity): Long = withContext(Dispatchers.IO) {
        sleepSessionRepository.insert(session)
    }

    suspend fun getSleepSessionByDate(date: String): com.zawaro.sleepchad.data.SleepSessionEntity? = withContext(Dispatchers.IO) {
        sleepSessionRepository.getByDate(date)
    }

    suspend fun updateSleepSession(session: com.zawaro.sleepchad.data.SleepSessionEntity) = withContext(Dispatchers.IO) {
        sleepSessionRepository.update(session)
    }
}
