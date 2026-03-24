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
        repo = ScheduleRepository(db.scheduleDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSaveAndRetrieveSchedule() =
        runBlocking {
            val schedule = ScheduleEntity(1, 22 * 60 * 60 * 1000L, 6 * 60 * 60 * 1000L)
            repo.saveSchedule(schedule)

            val retrieved = repo.getScheduleForDay(1)
            assertEquals("Schedules should match", schedule, retrieved)
        }
}
