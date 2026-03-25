package com.zawaro.sleepchad.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zawaro.sleepchad.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class StatisticsUiModel(
    val lastNightSleepMinutes: Int? = null,
    val lastNightPercentage: Int? = null,
    val weekAvgSleepHours: Double? = null,
    val weekDisciplinePercent: Int? = null,
    val monthAvgSleepHours: Double? = null,
    val monthTotalHours: Double? = null
)

class StatisticsViewModel(
    private val database: AppDatabase
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
                database.sleepSessionDao().getSessionsByDateRange(
                    yesterday.timeInMillis, 
                    today.timeInMillis
                )
            }

            if (sessions.isNotEmpty()) {
                val latestSession = sessions.maxByOrNull { it.wakeUpTimeMs ?: 0L } ?: return@launch
                
                val userPrefs = withContext(Dispatchers.IO) {
                    database.scheduleDao().getUserPreferences()
                }
                val targetSleepDurationMinutes = userPrefs?.targetSleepDurationMinutes ?: 480
                val lastNightSleepMinutes: Int? = latestSession.estimatedSleepDurationMinutes?.toInt() 
                    ?: (((latestSession.actualWakeTimeMs ?: latestSession.wakeUpTimeMs ?: System.currentTimeMillis()) - (latestSession.actualBedtimeMs ?: 0)) / 60000L).toInt()

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
                database.sleepSessionDao().getSessionsByDateRange(
                    sevenDaysAgo.timeInMillis, 
                    System.currentTimeMillis()
                )
            }

            if (sessions.isNotEmpty()) {
                val totalMinutes = sessions.sumOf { session ->
                    session.estimatedSleepDurationMinutes?.toLong() ?: ((session.actualWakeTimeMs ?: session.wakeUpTimeMs ?: System.currentTimeMillis()) - (session.actualBedtimeMs ?: 0)) / 60000 
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
                timeInMillis = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
                set(Calendar.HOUR_OF_DAY, 0)
            }

            val sessions = withContext(Dispatchers.IO) {
                database.sleepSessionDao().getSessionsByDateRange(
                    thirtyDaysAgo.timeInMillis, 
                    System.currentTimeMillis()
                )
            }

            if (sessions.isNotEmpty()) {
                val totalMinutes = sessions.sumOf { session ->
                    session.estimatedSleepDurationMinutes?.toLong() ?: ((session.actualWakeTimeMs ?: session.wakeUpTimeMs ?: System.currentTimeMillis()) - (session.actualBedtimeMs ?: 0)) / 60000 
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
