package com.zawaro.sleepchad.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zawaro.sleepchad.data.UserPreferencesEntity
import com.zawaro.sleepchad.domain.usecases.GetUserPreferencesUseCase
import com.zawaro.sleepchad.domain.usecases.SaveUserPreferencesUseCase
import com.zawaro.sleepchad.domain.usecases.RecordBedtimeUseCase
import com.zawaro.sleepchad.domain.usecases.RecordWakeUpUseCase
import com.zawaro.sleepchad.domain.usecases.GetLastNightSleepSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserPreferencesUiModel(
    val targetSleepDurationMinutes: Int? = null,
    val wakeUpTimeMs: Long? = null,
    val errandsDurationMinutes: Int? = null,
    val timeFormatPreference: String? = null,
    val themeIndex: Int = 0,
    val lastNightEstimatedSleepMinutes: Int? = null,
) {
    companion object {
        fun fromEntity(entity: UserPreferencesEntity?, lastNightSession: com.zawaro.sleepchad.data.SleepSessionEntity?): UserPreferencesUiModel {
            entity ?: return UserPreferencesUiModel(
                themeIndex = 0,
                lastNightEstimatedSleepMinutes = lastNightSession?.estimatedSleepDurationMinutes
            )
            
            return UserPreferencesUiModel(
                targetSleepDurationMinutes = entity.targetSleepDurationMinutes,
                wakeUpTimeMs = entity.wakeUpTimeMs,
                errandsDurationMinutes = entity.errandsDurationMinutes,
                timeFormatPreference = entity.timeFormatPreference,
                themeIndex = entity.themeIndex,
                lastNightEstimatedSleepMinutes = lastNightSession?.estimatedSleepDurationMinutes
            )
        }
    }
}

class PreferencesViewModel(
    private val getUserPreferences: GetUserPreferencesUseCase,
    private val saveUserPreferences: SaveUserPreferencesUseCase,
    private val recordBedtime: RecordBedtimeUseCase? = null,
    private val recordWakeUp: RecordWakeUpUseCase? = null,
    private val getLastNightSession: GetLastNightSleepSessionUseCase? = null,
) : ViewModel() {

    private val _preferences = MutableStateFlow(UserPreferencesUiModel())
    val preferences: StateFlow<UserPreferencesUiModel> = _preferences.asStateFlow()

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    init {
        viewModelScope.launch { loadPreferences() }
    }

    private suspend fun loadPreferences() {
        val entity = getUserPreferences()
        val lastNightSession = getLastNightSession?.let { it.invoke() }

        if (entity != null && entity.targetSleepDurationMinutes != null) {
            _preferences.value = UserPreferencesUiModel.fromEntity(entity, lastNightSession)
            _onboardingComplete.value = true
        } else {
            _preferences.value = UserPreferencesUiModel.fromEntity(entity, lastNightSession)
            _onboardingComplete.value = false
        }
    }

    fun loadPreferencesWithSystemDetection(context: android.content.Context, savePrefsUseCase: SaveUserPreferencesUseCase) {
        viewModelScope.launch {
            val entity = getUserPreferences()

            val resolvedEntity = if (entity != null && !entity.timeFormatPreference.isNullOrEmpty()) {
                entity
            } else {
                val use24Hour = android.text.format.DateFormat.is24HourFormat(context)
                val defaultFormat = if (use24Hour) "true" else "false"
                val updatedEntity = entity?.copy(timeFormatPreference = defaultFormat) ?: UserPreferencesEntity(id = 1, timeFormatPreference = defaultFormat)
                
                savePrefsUseCase(updatedEntity)
                updatedEntity
            }

            _preferences.value = UserPreferencesUiModel.fromEntity(resolvedEntity, getLastNightSession?.invoke())

            if (resolvedEntity.targetSleepDurationMinutes != null) {
                _onboardingComplete.value = true
            } else {
                _onboardingComplete.value = false
            }
        }
    }

    fun setOnboardingComplete(complete: Boolean) {
        _onboardingComplete.value = complete
    }

    suspend fun savePreferences(
        targetSleepDurationMinutes: Int?,
        wakeUpTimeMs: Long?,
        errandsDurationMinutes: Int?,
        timeFormatPreference: String?,
        themeIndex: Int = 0
    ) {
        val current = getUserPreferences() ?: UserPreferencesEntity(id = 1)
        
        val updated = current.copy(
            targetSleepDurationMinutes = targetSleepDurationMinutes ?: current.targetSleepDurationMinutes,
            wakeUpTimeMs = wakeUpTimeMs ?: current.wakeUpTimeMs,
            errandsDurationMinutes = errandsDurationMinutes ?: current.errandsDurationMinutes,
            timeFormatPreference = timeFormatPreference ?: current.timeFormatPreference ?: UserPreferencesEntity.DEFAULT_TIME_FORMAT,
            themeIndex = themeIndex
        )

        if (current.id == 0) {
            saveUserPreferences(updated)
        } else {
            saveUserPreferences(updated.copy(id = current.id))
        }
        
        _preferences.value = UserPreferencesUiModel.fromEntity(updated, null)
    }

    fun updateTargetSleepDuration(minutes: Int?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = minutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = current.timeFormatPreference
            )
        }
    }

    fun updateWakeUpTime(ms: Long?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = ms,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = current.timeFormatPreference
            )
        }
    }

    fun updateErrandsDuration(minutes: Int?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = minutes,
                timeFormatPreference = current.timeFormatPreference
            )
        }
    }

    fun updateTimeFormat(format: String?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = format
            )
        }
    }

    fun updateTimeFormat(value: Boolean?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = value?.toString()
            )
        }
    }

    fun updateTimeFormatBoolean(use24Hour: Boolean?) {
        viewModelScope.launch {
            val current = _preferences.value
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = use24Hour?.toString()
            )
        }
    }

    fun updateThemeIndex(themeIndex: Int) {
        viewModelScope.launch {
            val current = getUserPreferences() ?: UserPreferencesEntity(id = 1)
            savePreferences(
                targetSleepDurationMinutes = current.targetSleepDurationMinutes,
                wakeUpTimeMs = current.wakeUpTimeMs,
                errandsDurationMinutes = current.errandsDurationMinutes,
                timeFormatPreference = current.timeFormatPreference,
                themeIndex = themeIndex
            )
        }
    }

    fun completeSetup() {
        _onboardingComplete.value = true
    }

    fun recordBedtime() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            recordBedtime?.invoke(now)
        }
    }

    fun recordWakeUp() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            recordWakeUp?.invoke(now)
            loadPreferences()
        }
    }

    fun saveFullPreferences(
        targetSleepDurationMinutes: Int,
        wakeUpTimeMs: Long,
        errandsDurationMinutes: Int
    ) {
        viewModelScope.launch {
            val current = getUserPreferences() ?: UserPreferencesEntity(id = 1)
            
            val updated = current.copy(
                targetSleepDurationMinutes = targetSleepDurationMinutes,
                wakeUpTimeMs = wakeUpTimeMs,
                errandsDurationMinutes = errandsDurationMinutes,
                timeFormatPreference = null
            )

            if (current.id == 0) {
                saveUserPreferences(updated)
            } else {
                saveUserPreferences(updated.copy(id = current.id))
            }
            
            _preferences.value = UserPreferencesUiModel.fromEntity(updated, null)
        }
    }
}
