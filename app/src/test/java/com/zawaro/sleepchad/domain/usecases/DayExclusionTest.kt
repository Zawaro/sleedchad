package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.CustomAlarmEntity.Companion.toDaysSet
import com.zawaro.sleepchad.data.ScheduleEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class DayExclusionTest {

    @Test
    fun `calculate remaining days after exception exclusion - partial overlap`() {
        val defaultDays = setOf(1, 2, 3, 4, 5, 6, 7)
        val exceptionDays = setOf(6, 7)

        val remainingDays = defaultDays - exceptionDays

        assertEquals(setOf(1, 2, 3, 4, 5), remainingDays)
    }

    @Test
    fun `calculate remaining days after exception exclusion - all days covered`() {
        val defaultDays = setOf(1, 2, 3, 4, 5)
        val exceptionDays = setOf(1, 2, 3, 4, 5)

        val remainingDays = defaultDays - exceptionDays

        assertTrue(remainingDays.isEmpty())
    }

    @Test
    fun `exception day anchor logic - Saturday alarm fires errands Friday night`() {
        val exceptionAlarm = ScheduleEntity(
            id = 1L,
            name = "Weekend",
            isDefaultAlarm = false,
            enabledDaysString = "6",
            bedtimeMs = null,
            wakeupMs = null
        )

        val parsedDays = toDaysSet(exceptionAlarm.enabledDaysString)

        assertEquals(setOf(6), parsedDays)
    }

    @Test
    fun `default alarm auto-excludes all covered days - edge case`() {
        val defaultDays = setOf(1, 2, 3, 4, 5, 6, 7)
        val exceptionDays = setOf(1, 2, 3, 4, 5, 6, 7)

        val remainingDays = defaultDays - exceptionDays

        assertTrue(remainingDays.isEmpty())
    }
}