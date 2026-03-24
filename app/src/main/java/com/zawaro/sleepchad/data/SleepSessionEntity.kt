package com.zawaro.sleepchad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_sessions")
data class SleepSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO YYYY-MM-DD format
    val scheduledBedtimeMs: Long?,
    val actualBedtimeMs: Long? = null,
    val wakeUpTimeMs: Long? = null,
    val actualWakeTimeMs: Long? = null,
    val estimatedSleepDurationMinutes: Int? = null,
)
