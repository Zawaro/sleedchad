package com.zawaro.sleepchad.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.ScheduleAlarmsUseCase

/**
 * Factory for [ScheduleViewModel] that wires the use‑cases.
 */
class ScheduleViewModelFactory(
    private val getSchedule: GetScheduleUseCase,
    private val saveSchedule: SaveScheduleUseCase,
    private val scheduleAlarms: ScheduleAlarmsUseCase,
    private val errandRepository: ErrandRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(getSchedule, saveSchedule, scheduleAlarms, errandRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

