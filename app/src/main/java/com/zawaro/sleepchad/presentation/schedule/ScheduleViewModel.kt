package com.zawaro.sleepchad.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.ScheduleAlarmsUseCase
import com.zawaro.sleepchad.data.ScheduleEntity

/**
 * ViewModel that exposes schedule state and handles user actions.
 */
class ScheduleViewModel(
    private val getSchedule: GetScheduleUseCase,
    private val saveSchedule: SaveScheduleUseCase,
    private val scheduleAlarms: ScheduleAlarmsUseCase
) : ViewModel() {
    data class UiState(
        val day: Int,
        var bedtimeMs: Long? = null,
        var wakeupMs: Long? = null,
        var errandsBeforeBedMinutes: Int = 30
    )

    private val _schedules = MutableStateFlow<List<UiState>>(emptyList())
    val schedules: StateFlow<List<UiState>> = _schedules.asStateFlow()

    init {
        viewModelScope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        val list = (1..7).map { day ->
            val entity = getSchedule(day) ?: ScheduleEntity(
                dayOfWeek = day,
                bedtimeMs = null,
                wakeupMs = null,
                errandsBeforeBedMinutes = 30
            )
            UiState(
                day = day,
                bedtimeMs = entity.bedtimeMs,
                wakeupMs = entity.wakeupMs,
                errandsBeforeBedMinutes = entity.errandsBeforeBedMinutes
            )
        }
        _schedules.value = list
    }

    fun updateDay(updated: UiState) {
        _schedules.update { list ->
            list.map { if (it.day == updated.day) updated else it }
        }
    }

    fun saveAndSchedule() = viewModelScope.launch {
        _schedules.value.forEach { ui ->
            val entity = ScheduleEntity(
                dayOfWeek = ui.day,
                bedtimeMs = ui.bedtimeMs,
                wakeupMs = ui.wakeupMs,
                errandsBeforeBedMinutes = ui.errandsBeforeBedMinutes
            )
            saveSchedule(entity)
        }
        scheduleAlarms()
    }
}
