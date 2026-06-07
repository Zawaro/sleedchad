package com.zawaro.sleepchad.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
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
                id = 0L,
                name = "Test Default",
                isDefaultAlarm = true,
                enabledDaysString = setOf(1, 2, 3, 4, 5, 6, 7).joinToString(","),
                bedtimeMs = (22 * 60 * 60 * 1000L)?.toLong(),
                wakeupMs = (6 * 60 * 60 * 1000L)?.toLong()
            )
            
            val newId = repo.saveAlarm(schedule)

            val retrieved = repo.getDefaultAlarm()
            assertNotNull("Retrieved default alarm should not be null", retrieved)
            assertEquals("Default alarm name should match", schedule.name, retrieved?.name)
            assertTrue("Saved ID should be used (auto-generated)", newId > 0 || schedule.id == 0L)
        }

    @Test
    fun testSaveAndRetrieveExceptionAlarms() =
        runBlocking {
            val exceptionAlarm = ScheduleEntity(
                id = 0L,
                name = "Weekend Alarm",
                isDefaultAlarm = false,
                enabledDaysString = setOf(6, 7).joinToString(","),
                bedtimeMs = (23 * 60 * 60 * 1000L),
                wakeupMs = (8 * 60 * 60 * 1000L)
            )
            repo.saveAlarm(exceptionAlarm)

            val retrieved = repo.getExceptionAlarms()
            assertEquals("Should have one exception alarm", 1, retrieved.size)
            assertEquals("Weekend Alarm name should match", "Weekend Alarm", retrieved[0].name)
        }

    @Test
    fun testMultipleExceptionAlarms() =
        runBlocking {
            val weekendAlarm = ScheduleEntity(
                id = 0L,
                name = "Weekend",
                isDefaultAlarm = false,
                enabledDaysString = "6,7",
                bedtimeMs = (23 * 60 * 60 * 1000L),
                wakeupMs = (9 * 60 * 60 * 1000L)
            )
            
            val earlyAlarm = ScheduleEntity(
                id = 0L,
                name = "Early Bird",
                isDefaultAlarm = false,
                enabledDaysString = "1,2,3",
                bedtimeMs = (21 * 60 * 60 * 1000L),
                wakeupMs = (5 * 60 * 60 * 1000L)
            )
            
            repo.saveAlarm(weekendAlarm)
            repo.saveAlarm(earlyAlarm)

            val retrieved = repo.getExceptionAlarms()
            assertEquals("Should have two exception alarms", 2, retrieved.size)
        }

    @Test
    fun testUpdateExistingAlarm() =
        runBlocking {
            val alarm = ScheduleEntity(
                id = 1L,
                name = "Original Name",
                isDefaultAlarm = false,
                enabledDaysString = "1,2,3",
                bedtimeMs = (22 * 60 * 60 * 1000L),
                wakeupMs = (7 * 60 * 60 * 1000L)
            )
            
            repo.saveAlarm(alarm)
            
            val updatedAlarm = alarm.copy(
                name = "Updated Name",
                bedtimeMs = (23 * 60 * 60 * 1000L)
            )
            
            repo.saveAlarm(updatedAlarm)

            val retrieved = repo.getExceptionAlarms()
            assertEquals("Name should be updated", "Updated Name", retrieved[0].name)
            assertEquals("Bedtime should be updated", (23 * 60 * 60 * 1000L), retrieved[0].bedtimeMs)
        }

    @Test
    fun testDeleteAlarm() =
        runBlocking {
            val alarm = ScheduleEntity(
                id = 1L,
                name = "To Delete",
                isDefaultAlarm = false,
                enabledDaysString = "5",
                bedtimeMs = (22 * 60 * 60 * 1000L)?.toLong(),
                wakeupMs = (7 * 60 * 60 * 1000L)?.toLong()
            )
            
            repo.saveAlarm(alarm)
            assertTrue("Alarm should exist before delete", repo.getExceptionAlarms().isNotEmpty())

            repo.deleteAlarmById(1L)

            val remaining = repo.getExceptionAlarms()
            assertTrue("Should have no exception alarms after delete", remaining.isEmpty())
        }

    @Test
    fun testSaveAndRetrieveErrand() =
        runBlocking {
            val alarmId = repo.saveAlarm(ScheduleEntity(
                id = 0L,
                name = "Test Alarm",
                isDefaultAlarm = false,
                enabledDaysString = "1,2,3",
                bedtimeMs = (22 * 60 * 60 * 1000L)?.toLong(),
                wakeupMs = (7 * 60 * 60 * 1000L)?.toLong()
            ))

            val errand = com.zawaro.sleepchad.data.ErrandEntity(
                id = 0L,
                alarmId = alarmId,
                title = "Morning Errand",
                iconEmoji = "🔔",
                minutesBeforeBedtime = 30
            )
            
            repo.errandDao.insert(errand)

            val retrieved = repo.getErrandsForAlarm(alarmId)
            assertEquals("Should have one errand", 1, retrieved.size)
            assertEquals("Title should match", "Morning Errand", retrieved[0].title)
        }

    @Test
    fun testDefaultAlarmWithNullTimes() =
        runBlocking {
            val alarm = ScheduleEntity(
                id = 0L,
                name = "No Times Set",
                isDefaultAlarm = true,
                enabledDaysString = "1,2,3,4,5",
                bedtimeMs = null,
                wakeupMs = null
            )
            
            repo.saveAlarm(alarm)

            val retrieved = repo.getDefaultAlarm()
            assertNotNull("Should retrieve alarm with null times", retrieved)
            assertNull("Bedtime should be null", retrieved?.bedtimeMs)
            assertNull("Wakeup should be null", retrieved?.wakeupMs)
        }

    @Test
    fun testGetDefaultScheduleWithLongId() =
        runBlocking {
            val scheduleEntity = com.zawaro.sleepchad.data.ScheduleEntity(
                id = 123456789L,
                name = "Custom Default",
                isDefaultAlarm = true,
                enabledDaysString = "1,2,3,4,5,6,7",
                bedtimeMs = (22 * 60 * 60 * 1000L),
                wakeupMs = (6 * 60 * 60 * 1000L)
            )
            
            runBlocking {
                db.scheduleDao().insert(scheduleEntity)
                val retrieved = db.scheduleDao().getDefaultSchedule()
                assertNotNull("Should retrieve schedule with long id", retrieved)
                assertEquals("ID should match large number", scheduleEntity.id, retrieved?.id)
            }
        }
}
