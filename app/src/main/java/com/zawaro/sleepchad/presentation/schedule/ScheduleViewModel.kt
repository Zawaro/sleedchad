package com.zawaro.sleepchad.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.CustomAlarmEntity
import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.UserPreferencesEntity
import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.ScheduleAlarmsUseCase
import com.zawaro.sleepchad.domain.usecases.GetUserPreferencesUseCase
import com.zawaro.sleepchad.domain.usecases.GetExceptionAlarmsUseCase
import com.zawaro.sleepchad.domain.usecases.CreateExceptionAlarmUseCase
import com.zawaro.sleepchad.domain.usecases.DeleteExceptionAlarmUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel that exposes alarm state and handles user actions.
 */
class ScheduleViewModel(
    private val getSchedule: GetScheduleUseCase,
    private val saveSchedule: SaveScheduleUseCase,
    private val scheduleAlarms: ScheduleAlarmsUseCase,
    private val errandRepository: ErrandRepository,
    private val getUserPreferences: GetUserPreferencesUseCase? = null,
    private val getExceptionAlarmsUseCase: GetExceptionAlarmsUseCase? = null,
    private val createExceptionAlarmUseCase: CreateExceptionAlarmUseCase? = null,
    private val deleteExceptionAlarmUseCase: DeleteExceptionAlarmUseCase? = null,
) : ViewModel() {

    data class AlarmUiModel(
        val id: Long = 0L,
        val name: String,
        val isDefaultAlarm: Boolean,
        val enabledDays: Set<Int>,
        val bedtimeMs: Long?,
        val wakeupMs: Long?,
        val errands: List<com.zawaro.sleepchad.data.ErrandEntity> = emptyList(),
    )

    data class ErrandUiModel(
        val id: Long = 0L,
        val alarmId: Long,
        val title: String = "",
        val iconEmoji: String = "🔔",
        val minutesBeforeBedtime: Int = 30,
    )

    private val _defaultAlarm = MutableStateFlow<AlarmUiModel?>(null)
    val defaultAlarm: StateFlow<AlarmUiModel?> = _defaultAlarm.asStateFlow()

    private val _exceptionAlarms = MutableStateFlow<List<com.zawaro.sleepchad.data.CustomAlarmEntity>>(emptyList())
    val exceptionAlarms: StateFlow<List<com.zawaro.sleepchad.data.CustomAlarmEntity>> = _exceptionAlarms.asStateFlow()

    private val _nextAlarmTime = MutableStateFlow<Long?>(null)
    val nextAlarmTime: StateFlow<Long?> = _nextAlarmTime.asStateFlow()

    init {
        viewModelScope.launch { loadAll() }
    }

    @Suppress("UNUSED_PARAMETER")
    fun updateThemeIndex(index: Int) {
        // Theme handling - placeholder for now
    }

    private suspend fun getWakeUpTimeFromPreferences(): Long? {
        return getUserPreferences?.invoke()?.wakeUpTimeMs
    }

    private suspend fun loadAll() {
        val default = getSchedule()
        _defaultAlarm.value = default?.let { alarm ->
            AlarmUiModel(
                id = alarm.id,
                name = if (alarm.name.isEmpty()) ScheduleEntity.DEFAULT_ALARM_NAME else alarm.name,
                isDefaultAlarm = true,
                enabledDays = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysSet(alarm.enabledDaysString),
                bedtimeMs = alarm.bedtimeMs,
                wakeupMs = alarm.wakeupMs,
                errands = errandRepository.getErrandsByAlarm(alarm.id)
            )
        }

        val exceptions = getExceptionAlarmsUseCase?.invoke() ?: emptyList()
        _exceptionAlarms.value = exceptions.map { alarm ->
            alarm
        }

        calculateNextAlarm()
    }

    private fun calculateNextAlarm() {
        _nextAlarmTime.value = null
    }

    /** Creates a new exception alarm with target sleep duration. */
    fun createExceptionAlarm(
        name: String,
        enabledDays: Set<Int>,
        bedtimeMs: Long?,
        wakeupMs: Long?
    ) = viewModelScope.launch {
        val alarm = ScheduleEntity(
            id = 0L,
            name = name.ifEmpty { "Exception" },
            isDefaultAlarm = false,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDays),
            bedtimeMs = bedtimeMs,
            wakeupMs = wakeupMs,
        )

        saveSchedule(alarm)
        
        loadAll()
    }
    
    /** Creates a new exception alarm with target sleep hours and minutes. */
    fun createExceptionAlarmWithTime(
        name: String,
        enabledDays: Set<Int>,
        targetSleepHours: Int,
        targetSleepMinutes: Int
    ) = viewModelScope.launch {
        val wakeUpTimeMs = getWakeUpTimeFromPreferences()
        
        val totalMinutes = (targetSleepHours * 60) + targetSleepMinutes
        
        // Calculate bedtime by subtracting sleep duration from wake time
        var bedHour: Int
        var bedMinute: Int
        
        if (totalMinutes < 1440 && wakeUpTimeMs != null) {
            val wakeCal = java.util.Calendar.getInstance().apply { timeInMillis = wakeUpTimeMs }
            val totalWakeMinutes = (wakeCal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + wakeCal.get(java.util.Calendar.MINUTE)
            var totalBedMinutes = totalWakeMinutes - totalMinutes
            
            if (totalBedMinutes < 0) {
                totalBedMinutes += 1440
            }
            
            bedHour = totalBedMinutes / 60
            bedMinute = totalBedMinutes % 60
        } else if (wakeUpTimeMs != null) {
            val wakeCal = java.util.Calendar.getInstance().apply { timeInMillis = wakeUpTimeMs }
            bedHour = wakeCal.get(java.util.Calendar.HOUR_OF_DAY)
            bedMinute = wakeCal.get(java.util.Calendar.MINUTE)
        } else {
            bedHour = 23
            bedMinute = 0
        }
        
        val calculatedBedtimeMs = (bedHour * 3600000L) + (bedMinute * 60000L)
        
        // Create alarm with calculated bedtime and fixed wake time at 7AM
        val alarm = ScheduleEntity(
            id = 0L,
            name = name.ifEmpty { "Exception" },
            isDefaultAlarm = false,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDays),
            bedtimeMs = if (wakeUpTimeMs != null) calculatedBedtimeMs else null,
            wakeupMs = wakeUpTimeMs ?: ((7 * 3600000L) + (0 * 60000L)),
        )

        saveSchedule(alarm)
        
        loadAll()
    }

    /** Updates an existing exception alarm. */
    fun updateExceptionAlarm(
        id: Long,
        name: String,
        enabledDays: Set<Int>,
        bedtimeMs: Long?,
        wakeupMs: Long?
    ) = viewModelScope.launch {
        val alarm = ScheduleEntity(
            id = id,
            name = name.ifEmpty { "Exception" },
            isDefaultAlarm = false,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDays),
            bedtimeMs = bedtimeMs,
            wakeupMs = wakeupMs,
        )

        saveSchedule.update(alarm)
        
        loadAll()
    }

    /** Deletes an exception alarm. */
fun deleteExceptionAlarm(id: Long) = viewModelScope.launch {
        val exceptions = _exceptionAlarms.value
        val toDelete = exceptions.find { it.id == id }
        if (toDelete != null) {
            deleteExceptionAlarmUseCase?.invoke(toDelete.id) ?: Unit
            loadAll()
        }
    }

    /** Updates the default alarm. */
    fun updateDefaultAlarm(
        name: String,
        enabledDays: Set<Int>,
        bedtimeMs: Long?,
        wakeupMs: Long?
    ) = viewModelScope.launch {
        val current = _defaultAlarm.value ?: return@launch
        
        val alarm = ScheduleEntity(
            id = current.id,
            name = if (name.isEmpty()) ScheduleEntity.DEFAULT_ALARM_NAME else name,
            isDefaultAlarm = true,
            enabledDaysString = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysString(enabledDays),
            bedtimeMs = bedtimeMs,
            wakeupMs = wakeupMs,
        )

        saveSchedule(alarm)
        
        loadAll()
    }

    /** Inserts an errand for a specific alarm. */
    fun insertErrand(alarmId: Long, title: String, iconEmoji: String, minutesBeforeBedtime: Int) =
        viewModelScope.launch {
            val errand = com.zawaro.sleepchad.data.ErrandEntity(
                id = 0L,
                alarmId = alarmId,
                title = title,
                iconEmoji = iconEmoji,
                minutesBeforeBedtime = minutesBeforeBedtime,
            )

            saveSchedule.insertErrand(errand)
            loadAll()
        }

    /** Deletes an errand by ID. */
    fun deleteErrand(errandId: Long) = viewModelScope.launch {
        saveSchedule.deleteErrand(errandId)
        loadAll()
    }

    var disableAlarmsUntil: Long? = null
        private set
    
    fun disableAlarmsFor(hours: Int) {
        val timestamp = System.currentTimeMillis() + (hours * 60L * 60 * 1000)
        disableAlarmsUntil = timestamp
    }

    fun clearDisableAlarmOverride() {
        disableAlarmsUntil = null
    }

    /**
     * Checks for conflicts between new enabledDays and existing exception alarms.
     * Returns list of conflicts with overlapping wake-up anchor days.
     */
    fun checkDayConflicts(newEnabledDays: Set<Int>): List<ConflictInfo> {
        val currentExceptions = _exceptionAlarms.value
        
        return currentExceptions.filter { alarm ->
            com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysSet(alarm.enabledDaysString).intersect(newEnabledDays).isNotEmpty()
        }.map { alarm ->
            ConflictInfo(
                alarmId = alarm.id,
                name = alarm.name,
                overlappingDays = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysSet(alarm.enabledDaysString) intersect newEnabledDays
            )
        }
    }

    data class ConflictInfo(
        val alarmId: Long,
        val name: String,
        val overlappingDays: Set<Int>
    )
}