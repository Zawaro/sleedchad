package com.zawaro.sleepchad.data

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EffectiveAlarmValuesTest {

    @Test
    fun `fromPreferences calculates bedtime from wake time and sleep duration`() {
        val prefs = UserPreferencesEntity(
            id = 1,
            wakeUpTimeMs = (7 * 3600000L), // 7:00 AM
            targetSleepDurationMinutes = 600, // 10 hours
            errandsDurationMinutes = 30,
        )

        val result = EffectiveAlarmValues.fromPreferences(prefs)

        assertEquals(600, result.targetSleepDurationMinutes)
        assertEquals((7 * 3600000L), result.wakeUpTimeMs)
        assertEquals(30, result.errandsDurationMinutes)
    }

    @Test
    fun `overnight bedtime wraps to previous night`() {
        val prefs = UserPreferencesEntity(
            id = 1,
            wakeUpTimeMs = (7 * 3600000L), // 7:00 AM
            targetSleepDurationMinutes = 600, // 10 hours -> bedtime 9 PM previous night
            errandsDurationMinutes = 0,
        )

        val result = EffectiveAlarmValues.fromPreferences(prefs)

        // 7:00 AM = 25200000 ms, 10h = 36000000 ms
        // 25200000 - 36000000 = -10800000, wrap by adding 86400000 = 75600000 = 9:00 PM
        assertEquals(75600000L, result.calculatedBedtimeMs)
    }

    @Test
    fun `resolveFromPreferences uses alarm values when provided`() {
        val alarm = CustomAlarmEntity(
            id = 1L,
            wakeupMs = (6 * 3600000L), // 6:00 AM
            targetSleepDurationMinutes = 480, // 8 hours
            errandsDurationMinutes = 15,
        )
        val prefs = UserPreferencesEntity(
            id = 1,
            wakeUpTimeMs = (7 * 3600000L),
            targetSleepDurationMinutes = 600,
            errandsDurationMinutes = 30,
        )

        val result = EffectiveAlarmValues.resolveFromPreferences(alarm, prefs)

        assertEquals(480, result.targetSleepDurationMinutes)
        assertEquals((6 * 3600000L), result.wakeUpTimeMs)
        assertEquals(15, result.errandsDurationMinutes)
    }

    @Test
    fun `resolveFromPreferences falls back to preferences when alarm has null values`() {
        val alarm = CustomAlarmEntity(
            id = 1L,
            wakeupMs = null,
            targetSleepDurationMinutes = null,
            errandsDurationMinutes = null,
        )
        val prefs = UserPreferencesEntity(
            id = 1,
            wakeUpTimeMs = (8 * 3600000L),
            targetSleepDurationMinutes = 540,
            errandsDurationMinutes = 20,
        )

        val result = EffectiveAlarmValues.resolveFromPreferences(alarm, prefs)

        assertEquals(540, result.targetSleepDurationMinutes)
        assertEquals((8 * 3600000L), result.wakeUpTimeMs)
        assertEquals(20, result.errandsDurationMinutes)
    }

    @Test
    fun `resolveFromPreferences defaults sleep duration to 480 when neither alarm nor preferences have it`() {
        val alarm = CustomAlarmEntity(
            id = 1L,
            wakeupMs = (7 * 3600000L),
            targetSleepDurationMinutes = null,
        )
        val prefs = UserPreferencesEntity(
            id = 1,
            wakeUpTimeMs = (7 * 3600000L),
            targetSleepDurationMinutes = null,
        )

        val result = EffectiveAlarmValues.resolveFromPreferences(alarm, prefs)

        assertEquals(480, result.targetSleepDurationMinutes)
    }
}
