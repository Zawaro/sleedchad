package com.zawaro.sleepchad.presentation.schedule

data class CustomAlarmUiModel(
    val id: Long = 0L,
    val name: String,
    val enabledDays: Set<Int>,
    val targetSleepDurationMinutes: Int?,
    val wakeupMs: Long? = null,
)
