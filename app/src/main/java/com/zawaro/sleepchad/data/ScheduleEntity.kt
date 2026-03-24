package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val isDefaultAlarm: Boolean = false,
    val enabledDaysString: String = "1234567", // comma-separated days (1-7)
    val bedtimeMs: Long?,
    val wakeupMs: Long?,
) {
    companion object {
        const val DEFAULT_ALARM_NAME = "Default"
        
        fun Set<Int>.toDaysString(): String = 
            sorted().joinToString(",") { it.toString() }
            
        fun String.toDaysSet(): Set<Int> =
            split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
    }
}
