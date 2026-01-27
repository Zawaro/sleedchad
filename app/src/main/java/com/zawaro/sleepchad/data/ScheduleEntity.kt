package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val dayOfWeek: Int, // 1-7 (Calendar.MONDAY..SUNDAY)
    val bedtimeMs: Long?,          // epoch millis or null if not set
    val wakeupMs: Long?,           // same for wake‑up
    val errandsBeforeBedMinutes: Int = 30
)
