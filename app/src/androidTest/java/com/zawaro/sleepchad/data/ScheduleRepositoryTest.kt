package com.zawaro.sleepchad.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ScheduleRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: ScheduleRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repo = ScheduleRepository(db.scheduleDao(), db.errandDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSaveAndRetrieveDefaultAlarm() =
        runBlocking {
            val schedule = ScheduleEntity(
                id = 0,
                name = "Test Default",
                isDefaultAlarm = true,
                enabledDaysString = setOf(1, 2, 3, 4, 5, 6, 7).joinToString(","),
                bedtimeMs = (22 * 60 * 60 * 1000L),
                wakeupMs = (6 * 60 * 60 * 1000L)
            )
            repo.saveAlarm(schedule)

            val retrieved = repo.getDefaultAlarm()
            assertEquals("Default alarm should match", schedule, retrieved)
        }

    @Test
    fun testSaveAndRetrieveExceptionAlarms() =
        runBlocking {
            val exceptionAlarm = ScheduleEntity(
                id = 0,
                name = "Weekend Alarm",
                isDefaultAlarm = false,
                enabledDaysString = setOf(6, 7).joinToString(","),
                bedtimeMs = (23 * 60 * 60 * 1000L),
                wakeupMs = (8 * 60 * 60 * 1000L)
            )
            repo.saveAlarm(exceptionAlarm)

            val retrieved = repo.getExceptionAlarms()
            assertEquals("Exception alarms should match", listOf(exceptionAlarm), retrieved)
        }
}
