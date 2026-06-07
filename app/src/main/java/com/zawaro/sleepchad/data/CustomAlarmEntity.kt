package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.zawaro.sleepchad.utils.toDaysSet
import com.zawaro.sleepchad.utils.toDaysString

@Entity(tableName = "custom_alarms")
data class CustomAlarmEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "isEnabled") var isEnabled: Boolean = true,
    @ColumnInfo(name = "enabledDaysString") var enabledDaysString: String = "1,2,3,4,5,6,7",
    @ColumnInfo(name = "bedtimeMs") var bedtimeMs: Long? = null,
    @ColumnInfo(name = "wakeupMs") var wakeupMs: Long? = null,
    @ColumnInfo(name = "targetSleepDurationMinutes") var targetSleepDurationMinutes: Int? = null,
    @ColumnInfo(name = "errandsDurationMinutes") var errandsDurationMinutes: Int? = null,
) {
    companion object {
        fun toDaysString(set: Set<Int>): String = set.sorted().joinToString(",") { it.toString() }
            
        fun toDaysSet(string: String): Set<Int> = string.split(",").filter { it.isNotEmpty() }.mapNotNull { it.trim().toIntOrNull() }.toSet()
    }
}
