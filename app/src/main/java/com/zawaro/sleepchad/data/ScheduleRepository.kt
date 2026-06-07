package com.zawaro.sleepchad.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that abstracts access to [ScheduleEntity] data.
 * It uses the Room database under the hood but can be mocked easily for tests.
 */
@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val errandDao: ErrandDao,
) {
    companion object {
        fun createWithContext(context: Context): ScheduleRepository = ScheduleRepository(
            AppDatabase.getInstance(context).scheduleDao(),
            AppDatabase.getInstance(context).errandDao()
        )
    }

    suspend fun getUserPreferences(): UserPreferencesEntity? = withContext(Dispatchers.IO) { scheduleDao.getUserPreferences() }
    
    suspend fun saveUserPreferences(preferences: UserPreferencesEntity): Long = withContext(Dispatchers.IO) {
        scheduleDao.insertUserPreferences(preferences)
    }
    
    suspend fun updateUserPreferences(preferences: UserPreferencesEntity) = withContext(Dispatchers.IO) {
        scheduleDao.updateUserPreferences(preferences)
    }

    suspend fun getCustomAlarms(): List<CustomAlarmEntity> = withContext(Dispatchers.IO) { scheduleDao.getCustomAlarms() }
    
    suspend fun getCustomAlarmById(id: Long): CustomAlarmEntity? = withContext(Dispatchers.IO) { scheduleDao.getCustomAlarmById(id) }
    
    suspend fun saveCustomAlarm(alarm: CustomAlarmEntity): Long = withContext(Dispatchers.IO) {
        scheduleDao.insertCustomAlarm(alarm)
    }
    
    suspend fun updateCustomAlarm(alarm: CustomAlarmEntity) = withContext(Dispatchers.IO) {
        scheduleDao.updateCustomAlarm(alarm)
    }
    
    suspend fun deleteCustomAlarm(alarm: CustomAlarmEntity) = withContext(Dispatchers.IO) {
        scheduleDao.deleteCustomAlarm(alarm)
    }
    
    suspend fun deleteCustomAlarmById(id: Long) = withContext(Dispatchers.IO) {
        scheduleDao.deleteCustomAlarmById(id)
    }

    /** Returns the default alarm. */
    suspend fun getDefaultAlarm(): ScheduleEntity? = withContext(Dispatchers.IO) { scheduleDao.getDefaultSchedule() }

    /** Returns all exception alarms (non-default). */
    suspend fun getExceptionAlarms(): List<ScheduleEntity> = withContext(Dispatchers.IO) { scheduleDao.getExceptionSchedules() }

    /** Saves a default or exception alarm. Returns the new ID if inserted. */
    suspend fun saveAlarm(schedule: ScheduleEntity): Long = withContext(Dispatchers.IO) {
        scheduleDao.insert(schedule)
    }

    /** Updates an existing alarm. */
    suspend fun updateAlarm(schedule: ScheduleEntity) {
        withContext(Dispatchers.IO) { scheduleDao.update(schedule) }
    }

    /** Deletes an exception alarm. */
    suspend fun deleteAlarm(schedule: ScheduleEntity) {
        withContext(Dispatchers.IO) { scheduleDao.delete(schedule) }
    }

    /** Deletes an alarm by ID. */
    suspend fun deleteAlarmById(id: Long) {
        withContext(Dispatchers.IO) { scheduleDao.deleteById(id) }
    }

    /** Gets errands for a specific alarm. */
    suspend fun getErrandsForAlarm(alarmId: Long): List<com.zawaro.sleepchad.data.ErrandEntity> =
        withContext(Dispatchers.IO) { errandDao.getErrandsByAlarm(alarmId) }
}

