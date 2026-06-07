package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey var id: Int = 1,
    var targetSleepDurationMinutes: Int? = null,
    var wakeUpTimeMs: Long? = null,
    var errandsDurationMinutes: Int? = null,
    var timeFormatPreference: String? = null,
    var themeIndex: Int = 0,
    var weekendRecoveryEnabled: Boolean = false,
    var weekendWakeUpTimeMs: Long? = null,
    var weekendTargetSleepDurationMinutes: Int? = null,
    var weekendErrandsDurationMinutes: Int? = null,
) {
    companion object {
        const val DEFAULT_ID = 1
        const val DEFAULT_TIME_FORMAT = "system"
        const val DEFAULT_THEME_INDEX = 0
    }
}
