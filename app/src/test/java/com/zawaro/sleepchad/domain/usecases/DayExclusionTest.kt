package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.CustomAlarmEntity.Companion.toDaysSet
import com.zawaro.sleepchad.data.ScheduleEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

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

    @Test
    fun `parse comma-separated days string with spaces`() {
        val result = toDaysSet("1, 2, 3, 4")
        assertEquals(setOf(1, 2, 3, 4), result)
    }

    @Test
    fun `parse single day from string`() {
        val result = toDaysSet("5")
        assertEquals(setOf(5), result)
    }

    @Test
    fun `parse empty string returns empty set`() {
        val result = toDaysSet("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse invalid days string returns empty set`() {
        val result = toDaysSet("invalid")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `exception alarm with Sunday only`() {
        val exceptionAlarm = ScheduleEntity(
            id = 2L,
            name = "Sunday Only",
            isDefaultAlarm = false,
            enabledDaysString = "7",
            bedtimeMs = null,
            wakeupMs = null
        )

        val parsedDays = toDaysSet(exceptionAlarm.enabledDaysString)
        assertEquals(setOf(7), parsedDays)
    }

    @Test
    fun `weekday alarm with Monday Wednesday Friday`() {
        val exceptionAlarm = ScheduleEntity(
            id = 3L,
            name = "MWF",
            isDefaultAlarm = false,
            enabledDaysString = "1,3,5",
            bedtimeMs = null,
            wakeupMs = null
        )

        val parsedDays = toDaysSet(exceptionAlarm.enabledDaysString)
        assertEquals(setOf(1, 3, 5), parsedDays)
    }

    @Test
    fun `all seven days enabled`() {
        val exceptionAlarm = ScheduleEntity(
            id = 4L,
            name = "Every Day",
            isDefaultAlarm = false,
            enabledDaysString = "1,2,3,4,5,6,7",
            bedtimeMs = null,
            wakeupMs = null
        )

        val parsedDays = toDaysSet(exceptionAlarm.enabledDaysString)
        assertEquals(setOf(1, 2, 3, 4, 5, 6, 7), parsedDays)
    }
}