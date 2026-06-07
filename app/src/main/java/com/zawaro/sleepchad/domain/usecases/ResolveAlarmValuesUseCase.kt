package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.CustomAlarmEntity
import com.zawaro.sleepchad.data.EffectiveAlarmValues
import com.zawaro.sleepchad.data.UserPreferencesEntity
import javax.inject.Inject

class ResolveAlarmValuesUseCase @Inject constructor() {
    fun execute(
        alarm: CustomAlarmEntity,
        preferences: UserPreferencesEntity?
    ): EffectiveAlarmValues {
        val prefs = preferences ?: throw IllegalArgumentException("UserPreferences not found")
        
        return EffectiveAlarmValues.resolveFromPreferences(alarm, prefs)
    }
}
