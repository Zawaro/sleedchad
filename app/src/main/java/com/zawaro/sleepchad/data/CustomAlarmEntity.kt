package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Ignore

@Entity(tableName = "custom_alarms")
data class CustomAlarmEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "enabledDaysString") var enabledDaysString: String = "1234567",
    @ColumnInfo(name = "bedtimeMs") var bedtimeMs: Long? = null,
    @ColumnInfo(name = "wakeupMs") var wakeupMs: Long? = null,
    @ColumnInfo(name = "targetSleepDurationMinutes") var targetSleepDurationMinutes: Int? = null,
    @ColumnInfo(name = "errandsDurationMinutes") var errandsDurationMinutes: Int? = null,
) {
    companion object {
        fun toDaysString(set: Set<Int>): String = 
            set.sorted().joinToString(",") { it.toString() }
            
        fun toDaysSet(string: String): Set<Int> =
            string.split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
    }
}

// Extension functions outside companion object
fun Set<Int>.toDaysString(): String = 
    sorted().joinToString(",") { it.toString() }
    
fun String.toDaysSet(): Set<Int> =
    split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
