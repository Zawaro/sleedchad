package com.zawaro.sleepchad.test

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zawaro.sleepchad.MainActivity
import com.zawaro.sleepchad.data.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests to verify app startup integrity.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityIntegrityTest {

    private lateinit var application: Application
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext<Application>()
        // Initialize database to verify Room implementation exists
        database = AppDatabase.getInstance(application)
    }

    @Test
    fun appContext_isNotNull() {
        assertNotNull("Application context should not be null", application)
    }

    @Test
    fun databaseInstance_isNotNull() {
        assertNotNull("AppDatabase instance should not be null", database)
    }

    @Test
    fun scheduleDao_exists() {
        val dao = database.scheduleDao()
        assertNotNull("ScheduleDao should be accessible", dao)
    }

    @Test
    fun errandDao_exists() {
        val dao = database.errandDao()
        assertNotNull("ErrandDao should be accessible", dao)
    }

    @Test
    fun customAlarmDao_exists() {
        val dao = database.customAlarmDao()
        assertNotNull("CustomAlarmDao should be accessible", dao)
    }

    @Test
    fun sleepSessionDao_exists() {
        val dao = database.sleepSessionDao()
        assertNotNull("SleepSessionDao should be accessible", dao)
    }

    @Test
    fun databaseCanInsertScheduleEntity() {
        // Test that we can actually use the database
        val scheduleEntity = com.zawaro.sleepchad.data.ScheduleEntity(
            id = 1,
            name = "Test Default",
            isDefaultAlarm = true,
            enabledDaysString = "1,2,3,4,5,6,7",
            bedtimeMs = (22 * 60 * 60 * 1000L),
            wakeupMs = (6 * 60 * 60 * 1000L)
        )
        
        runBlocking {
            database.scheduleDao().insert(scheduleEntity)
            val retrieved = database.scheduleDao().getDefaultSchedule()
            assertNotNull("Should be able to insert and retrieve schedule", retrieved)
            assertEquals("Default alarm should match inserted entity", scheduleEntity.id, retrieved?.id)
        }
    }

    @Test
    fun mainActivity_canInstantiate() {
        val activity = MainActivity()
        assertNotNull("MainActivity should be instantiable", activity)
    }
}
