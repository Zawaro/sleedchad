package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val isDefaultAlarm: Boolean = false,
    val enabledDaysString: String = "1,2,3,4,5,6,7", // comma-separated days (1-7)
    val bedtimeMs: Long?,
    val wakeupMs: Long?,
) {
    companion object {
        const val DEFAULT_ALARM_NAME = "Default"
    }
}
