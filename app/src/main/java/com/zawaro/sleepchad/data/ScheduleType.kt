package com.zawaro.sleepchad.data

sealed class ScheduleType {
    object Weekday : ScheduleType()
    object Weekend : ScheduleType()
}

val ScheduleType.days: List<Int>
    get() = when (this) {
        is ScheduleType.Weekday -> listOf(1, 2, 3, 4, 5) // Mon-Fri
        is ScheduleType.Weekend -> listOf(6, 7) // Sat-Sun
    }

val Int.scheduleType: ScheduleType
    get() = when (this) {
        in 1..5 -> ScheduleType.Weekday
        else -> ScheduleType.Weekend
    }
