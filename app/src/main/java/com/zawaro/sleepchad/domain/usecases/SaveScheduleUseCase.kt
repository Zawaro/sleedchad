package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.utils.toDaysSet

/**
 * Use‑case for persisting an alarm (default or exception).
 */
class SaveScheduleUseCase(
    private val repository: ScheduleRepository,
    private val errandRepository: ErrandRepository,
) {
    /**
     * Saves a default alarm. If one exists, it's deleted first to maintain single default.
     * Returns the new alarm ID.
     */
    suspend operator fun invoke(schedule: ScheduleEntity): Long =
        if (schedule.isDefaultAlarm) {
            // Delete existing default if present
            val existingDefault = repository.getDefaultAlarm()
            if (existingDefault != null && existingDefault.id != schedule.id) {
                repository.deleteAlarm(existingDefault)
            }
            repository.saveAlarm(schedule)
        } else {
            repository.saveAlarm(schedule)
        }

    /** Updates an existing alarm. */
    suspend fun update(schedule: ScheduleEntity): Long = repository.saveAlarm(schedule)

    /** Deletes an exception alarm. */
    suspend fun delete(schedule: ScheduleEntity) {
        repository.deleteAlarm(schedule)
    }

    /** Inserts an errand for a specific alarm. */
    suspend fun insertErrand(errand: com.zawaro.sleepchad.data.ErrandEntity): Long =
        errandRepository.insert(errand)

    /** Deletes an errand by ID. */
    suspend fun deleteErrand(id: Long) {
        errandRepository.deleteById(id)
    }

    /**
     * Saves an exception alarm and checks if it would cover ALL wake-up anchor days.
     * If yes, auto-disables the default alarm (Option A).
     * Returns true if default was disabled, false otherwise.
     */
    suspend fun saveExceptionWithConflictCheck(schedule: ScheduleEntity): Boolean {
        require(!schedule.isDefaultAlarm) { "This method is for exception alarms only" }

        val enabledDays = schedule.enabledDaysString.toDaysSet()
        val existingDefault = repository.getDefaultAlarm()
        
        var defaultDisabled = false
        
        if (existingDefault != null) {
            val defaultDays = existingDefault.enabledDaysString.toDaysSet()
            val remainingDays = defaultDays - enabledDays
            
            // If exception covers all days, auto-disable default
            if (remainingDays.isEmpty()) {
                repository.deleteAlarm(existingDefault)
                defaultDisabled = true
            }
        }

        repository.saveAlarm(schedule)
        return defaultDisabled
    }
}
