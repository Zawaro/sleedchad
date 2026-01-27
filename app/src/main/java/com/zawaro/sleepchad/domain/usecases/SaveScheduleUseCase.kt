package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository

/**
 * Use‑case for persisting a schedule.
 */
class SaveScheduleUseCase(private val repository: ScheduleRepository) {
    suspend operator fun invoke(schedule: ScheduleEntity) =
        repository.saveSchedule(schedule)
}
