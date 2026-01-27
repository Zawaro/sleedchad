package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository

/**
 * Use‑case for fetching a schedule for a specific day.
 */
class GetScheduleUseCase(private val repository: ScheduleRepository) {
    suspend operator fun invoke(dayOfWeek: Int): ScheduleEntity? =
        repository.getScheduleForDay(dayOfWeek)
}
