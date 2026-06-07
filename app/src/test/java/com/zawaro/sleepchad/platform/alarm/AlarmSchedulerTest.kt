package com.zawaro.sleepchad.platform.alarm

import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.data.UserPreferencesEntity
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AlarmSchedulerTest {

    @MockK
    private lateinit var repo: ScheduleRepository

    private val context: android.content.Context = mockk(relaxed = true)
    private val alarmMgr: android.app.AlarmManager = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `scheduleAllAlarms does nothing when no default alarm exists`() = runTest {
        coEvery { repo.getDefaultAlarm() } returns null
        coEvery { repo.getExceptionAlarms() } returns emptyList()
        coEvery { repo.getUserPreferences() } returns null

        scheduleAllAlarms(context, alarmMgr, repo)

        coVerify { repo.getDefaultAlarm() }
        coVerify { repo.getExceptionAlarms() }
    }
}
