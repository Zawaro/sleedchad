package com.zawaro.sleepchad.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing errands associated with alarms.
 * Errands are linked to specific alarms via alarmId foreign key.
 */
@Singleton
class ErrandRepository @Inject constructor(
    private val errandDao: ErrandDao,
) {
    /**
     * Inserts a new errand for a specific alarm.
     * Returns the new errand ID.
     */
    suspend fun insert(errand: ErrandEntity): Long = withContext(Dispatchers.IO) {
        errandDao.insert(errand)
    }

    /**
     * Deletes an errand by its ID.
     * The corresponding database row is removed via CASCADE delete on alarmId.
     */
    suspend fun deleteById(id: Long) {
        withContext(Dispatchers.IO) { errandDao.deleteById(id) }
    }

    /**
     * Retrieves all errands for a specific alarm (wake-up anchor).
     * Errands fire before bedtime based on their minutesBeforeBedtime setting.
     */
    suspend fun getErrandsByAlarm(alarmId: Long): List<ErrandEntity> =
        withContext(Dispatchers.IO) { errandDao.getErrandsByAlarm(alarmId) }

    /**
     * Deletes all errands for a specific alarm (used when alarm is deleted).
     * This is handled automatically via CASCADE delete, but can be called explicitly if needed.
     */
    suspend fun deleteAllForAlarm(alarmId: Long) {
        val errands = getErrandsByAlarm(alarmId)
        for (errand in errands) {
            deleteById(errand.id)
        }
    }
}
