package com.zawaro.sleepchad.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zawaro.sleepchad.data.SleepSessionRepository
import com.zawaro.sleepchad.data.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

data class StatisticsUiModel(
    val lastNightSleepMinutes: Int? = null,
    val lastNightPercentage: Int? = null,
    val weekAvgSleepHours: Double? = null,
    val weekDisciplinePercent: Int? = null,
    val monthAvgSleepHours: Double? = null,
    val monthTotalHours: Double? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val sleepSessionRepository: SleepSessionRepository,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiModel())
    val uiState: StateFlow<StatisticsUiModel> = _uiState

    init {
        loadLastNightSleep()
        loadWeekStats()
        loadMonthStats()
    }

    fun loadLastNightSleep() {
        viewModelScope.launch {
            val yesterday = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val sessions = withContext(Dispatchers.IO) {
                sleepSessionRepository.getSessionsByDateRange(
                    yesterday.timeInMillis, 
                    today.timeInMillis
                )
            }

            if (sessions.isNotEmpty()) {
                val latestSession = sessions.maxByOrNull { it.wakeUpTimeMs ?: 0L } ?: return@launch

                val userPrefs = withContext(Dispatchers.IO) {
                    scheduleRepository.getUserPreferences()
                }
                val targetSleepDurationMinutes = userPrefs?.targetSleepDurationMinutes ?: 480
                val lastNightSleepMinutes: Int? = if (latestSession.actualBedtimeMs != null || latestSession.scheduledBedtimeMs != null) {
                    latestSession.estimatedSleepDurationMinutes?.toInt() 
                        ?: (((latestSession.actualWakeTimeMs ?: latestSession.wakeUpTimeMs ?: System.currentTimeMillis()) - (latestSession.actualBedtimeMs ?: latestSession.scheduledBedtimeMs ?: 0)) / 60000L).toInt()
                } else {
                    null
                }

                var percentage: Int? = null
                if (lastNightSleepMinutes != null) {
                    val calculatedPercentage = ((lastNightSleepMinutes.toFloat() / targetSleepDurationMinutes) * 100).toInt()
                    if (calculatedPercentage < 0) {
                        percentage = 0
                    } else if (calculatedPercentage > 100) {
                        percentage = 100
                    } else {
                        percentage = calculatedPercentage
                    }
                }
                
                _uiState.value = (_uiState.value.copy(
                    lastNightSleepMinutes = lastNightSleepMinutes,
                    lastNightPercentage = percentage
                ))
            }
        }
    }

    fun loadWeekStats() {
        viewModelScope.launch {
            val sevenDaysAgo = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                set(Calendar.HOUR_OF_DAY, 0)
            }

            val sessions = withContext(Dispatchers.IO) {
                sleepSessionRepository.getSessionsByDateRange(
                    sevenDaysAgo.timeInMillis, 
                    System.currentTimeMillis()
                )
            }

            if (sessions.isNotEmpty()) {
                val totalMinutes = sessions.sumOf { session ->
                    if (session.actualBedtimeMs != null || session.scheduledBedtimeMs != null) {
                        session.estimatedSleepDurationMinutes?.toLong() 
                            ?: ((session.actualWakeTimeMs ?: session.wakeUpTimeMs ?: System.currentTimeMillis()) - (session.actualBedtimeMs ?: session.scheduledBedtimeMs ?: 0)) / 60000 
                    } else 0L
                }
                
                val avgHours = totalMinutes.toDouble() / sessions.size / 60

                _uiState.value = _uiState.value.copy(
                    weekAvgSleepHours = avgHours,
                    weekDisciplinePercent = null
                )
            }
        }
    }

    fun loadMonthStats() {
        viewModelScope.launch {
            val thirtyDaysAgo = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                set(Calendar.HOUR_OF_DAY, 0)
            }

            val sessions = withContext(Dispatchers.IO) {
                sleepSessionRepository.getSessionsByDateRange(
                    thirtyDaysAgo.timeInMillis, 
                    System.currentTimeMillis()
                )
            }

            if (sessions.isNotEmpty()) {
                val totalMinutes = sessions.sumOf { session ->
                    if (session.actualBedtimeMs != null || session.scheduledBedtimeMs != null) {
                        session.estimatedSleepDurationMinutes?.toLong() 
                            ?: ((session.actualWakeTimeMs ?: session.wakeUpTimeMs ?: System.currentTimeMillis()) - (session.actualBedtimeMs ?: session.scheduledBedtimeMs ?: 0)) / 60000 
                    } else 0L
                }
                
                val avgHours = totalMinutes.toDouble() / sessions.size / 60
                val totalHours = totalMinutes.toDouble() / 60

                _uiState.value = _uiState.value.copy(
                    monthAvgSleepHours = avgHours,
                    monthTotalHours = totalHours
                )
            }
        }
    }
}
