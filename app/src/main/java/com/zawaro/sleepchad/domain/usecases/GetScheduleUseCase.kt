package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import javax.inject.Inject

/**
 * Use‑case for fetching alarm schedules.
 */
class GetScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository,
) {
    /** Returns the default alarm (applies to all days not covered by exceptions). */
    suspend operator fun invoke(): ScheduleEntity? = repository.getDefaultAlarm()

    /** Returns all exception alarms. */
    suspend fun getExceptionAlarms(): List<ScheduleEntity> = repository.getExceptionAlarms()

    /** Gets errands for a specific alarm. */
    suspend fun getErrandsForAlarm(alarmId: Long): List<com.zawaro.sleepchad.data.ErrandEntity> =
        repository.getErrandsForAlarm(alarmId)
}
