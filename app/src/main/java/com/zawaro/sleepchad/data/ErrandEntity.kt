package com.zawaro.sleepchad.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "errands")
data class ErrandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "alarmId") val alarmId: Long,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "iconEmoji") val iconEmoji: String = "🔔",
    @ColumnInfo(name = "minutesBeforeBedtime") val minutesBeforeBedtime: Int = 30,
) {
    companion object {
        const val DEFAULT_EMOJI = "🔔"
        const val DEFAULT_MINUTES_BEFORE_BEDTIME = 30
    }
}
