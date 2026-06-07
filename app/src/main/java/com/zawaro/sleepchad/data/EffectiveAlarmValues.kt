package com.zawaro.sleepchad.data

data class EffectiveAlarmValues(
    val targetSleepDurationMinutes: Int,
    val wakeUpTimeMs: Long,
    val errandsDurationMinutes: Int,
    val calculatedBedtimeMs: Long
) {
    companion object {
        fun fromPreferences(preferences: UserPreferencesEntity): EffectiveAlarmValues {
            val wakeUpTime = preferences.wakeUpTimeMs ?: throw IllegalArgumentException("Wake-up time not set")
            val sleepDuration = preferences.targetSleepDurationMinutes ?: 480 // default 8 hours
            val errandsDuration = preferences.errandsDurationMinutes ?: 0
            
            var calculatedBedtime = wakeUpTime - (sleepDuration.toLong() * 60 * 1000) - (errandsDuration.toLong() * 60 * 1000)
            if (calculatedBedtime < 0L) {
                calculatedBedtime += (24 * 3600000L)
            }
            
            return EffectiveAlarmValues(
                targetSleepDurationMinutes = sleepDuration,
                wakeUpTimeMs = wakeUpTime,
                errandsDurationMinutes = errandsDuration,
                calculatedBedtimeMs = calculatedBedtime
            )
        }

        fun resolveFromPreferences(
            alarm: CustomAlarmEntity,
            preferences: UserPreferencesEntity
        ): EffectiveAlarmValues {
            val wakeUpTime = alarm.wakeupMs ?: preferences.wakeUpTimeMs 
                ?: throw IllegalArgumentException("Wake-up time not set")
            
            val sleepDuration = alarm.targetSleepDurationMinutes ?: preferences.targetSleepDurationMinutes 
                ?: 480 // default 8 hours
            
            val errandsDuration = alarm.errandsDurationMinutes ?: (preferences.errandsDurationMinutes ?: 0)
            
            var calculatedBedtime = wakeUpTime - (sleepDuration.toLong() * 60 * 1000) - (errandsDuration.toLong() * 60 * 1000)
            if (calculatedBedtime < 0L) {
                calculatedBedtime += (24 * 3600000L)
            }
            
            return EffectiveAlarmValues(
                targetSleepDurationMinutes = sleepDuration,
                wakeUpTimeMs = wakeUpTime,
                errandsDurationMinutes = errandsDuration,
                calculatedBedtimeMs = calculatedBedtime
            )
        }
    }
}
