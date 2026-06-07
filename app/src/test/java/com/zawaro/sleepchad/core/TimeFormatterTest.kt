package com.zawaro.sleepchad.core

import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TimeFormatterTest {

    private val context = mockk<android.content.Context>(relaxed = true)

    @Test
    fun `formatDuration returns hours and minutes for valid input`() {
        val result = TimeFormatter.formatDuration(150)
        assertEquals("2h 30m", result)
    }

    @Test
    fun `formatDuration returns 0h for null`() {
        val result = TimeFormatter.formatDuration(null)
        assertEquals("0h", result)
    }

    @Test
    fun `formatDuration returns 0h for zero`() {
        val result = TimeFormatter.formatDuration(0)
        assertEquals("0h", result)
    }

    @Test
    fun `formatDuration returns only hours when no minutes`() {
        val result = TimeFormatter.formatDuration(120)
        assertEquals("2h", result)
    }

    @Test
    fun `formatDuration returns only minutes when less than an hour`() {
        val result = TimeFormatter.formatDuration(45)
        assertEquals("45m", result)
    }

    @Test
    fun `formatDurationVerbose returns hours and minutes text`() {
        val result = TimeFormatter.formatDurationVerbose(90)
        assertEquals("1 hours and 30 minutes", result)
    }

    @Test
    fun `formatDurationVerbose returns only hours when no minutes`() {
        val result = TimeFormatter.formatDurationVerbose(60)
        assertEquals("1 hours", result)
    }

    @Test
    fun `formatDurationVerbose returns 0 minutes for null`() {
        val result = TimeFormatter.formatDurationVerbose(null)
        assertEquals("0 minutes", result)
    }

    @Test
    fun `formatTime returns time for valid millis in 24h`() {
        val millis = (14 * 3600000L) + (30 * 60000L)
        val result = TimeFormatter.formatTime(context, millis, true)
        assertEquals("14:30", result)
    }

    @Test
    fun `formatTime returns 12 hour PM format`() {
        val millis = (14 * 3600000L) + (30 * 60000L)
        val result = TimeFormatter.formatTime(context, millis, false)
        assertTrue(result.contains("2:30") || result.contains("02:30"))
        assertTrue(result.contains("PM"))
    }

    @Test
    fun `formatTime returns 12 hour AM format`() {
        val millis = (8 * 3600000L) + (15 * 60000L)
        val result = TimeFormatter.formatTime(context, millis, false)
        assertTrue(result.contains("8:15") || result.contains("08:15"))
        assertTrue(result.contains("AM"))
    }

    @Test
    fun `formatTime handles midnight`() {
        val millis = 0L
        val result = TimeFormatter.formatTime(context, millis, true)
        assertEquals("00:00", result)
    }
}
