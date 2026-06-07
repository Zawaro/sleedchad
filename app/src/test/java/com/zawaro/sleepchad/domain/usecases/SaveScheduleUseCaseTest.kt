package com.zawaro.sleepchad.domain.usecases

import com.zawaro.sleepchad.data.ErrandDao
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.ScheduleEntity
import com.zawaro.sleepchad.data.ScheduleRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SaveScheduleUseCaseTest {

    @MockK
    private lateinit var scheduleRepository: ScheduleRepository

    @MockK
    private lateinit var errandDao: ErrandDao

    private lateinit var saveSchedule: SaveScheduleUseCase
    private lateinit var errandRepository: ErrandRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        errandRepository = ErrandRepository(errandDao)
        saveSchedule = SaveScheduleUseCase(scheduleRepository, errandRepository)
    }

    @Test
    fun `saves default alarm and clears existing default`() = runTest {
        val existingDefault = ScheduleEntity(id = 1L, name = "Old", isDefaultAlarm = true, bedtimeMs = null, wakeupMs = null)
        val newDefault = ScheduleEntity(id = 0L, name = "New", isDefaultAlarm = true, bedtimeMs = null, wakeupMs = null)
        coEvery { scheduleRepository.getDefaultAlarm() } returns existingDefault
        coEvery { scheduleRepository.deleteAlarm(existingDefault) } answers { }
        coEvery { scheduleRepository.saveAlarm(newDefault) } returns 2L

        saveSchedule(newDefault)

        coVerify { scheduleRepository.deleteAlarm(existingDefault) }
        coVerify { scheduleRepository.saveAlarm(newDefault) }
    }

    @Test
    fun `saves exception alarm without touching default`() = runTest {
        val exception = ScheduleEntity(id = 0L, name = "Weekend", isDefaultAlarm = false, bedtimeMs = null, wakeupMs = null)
        coEvery { scheduleRepository.saveAlarm(exception) } returns 1L

        saveSchedule(exception)

        coVerify(inverse = true) { scheduleRepository.getDefaultAlarm() }
    }

    @Test
    fun `saveExceptionWithConflictCheck deletes default when exception covers all days`() = runTest {
        val default = ScheduleEntity(id = 1L, name = "Default", isDefaultAlarm = true, enabledDaysString = "1,2,3,4,5", bedtimeMs = null, wakeupMs = null)
        val exception = ScheduleEntity(id = 0L, name = "Full Coverage", isDefaultAlarm = false, enabledDaysString = "1,2,3,4,5", bedtimeMs = null, wakeupMs = null)
        coEvery { scheduleRepository.getDefaultAlarm() } returns default
        coEvery { scheduleRepository.deleteAlarm(default) } just runs
        coEvery { scheduleRepository.saveAlarm(exception) } returns 1L

        val result = saveSchedule.saveExceptionWithConflictCheck(exception)

        coVerify { scheduleRepository.deleteAlarm(default) }
        coVerify { scheduleRepository.saveAlarm(exception) }
        assert(result)
    }

    @Test
    fun `saveExceptionWithConflictCheck keeps default when exception covers partial days`() = runTest {
        val default = ScheduleEntity(id = 1L, name = "Default", isDefaultAlarm = true, enabledDaysString = "1,2,3,4,5", bedtimeMs = null, wakeupMs = null)
        val exception = ScheduleEntity(id = 0L, name = "Partial", isDefaultAlarm = false, enabledDaysString = "6,7", bedtimeMs = null, wakeupMs = null)
        coEvery { scheduleRepository.getDefaultAlarm() } returns default
        coEvery { scheduleRepository.saveAlarm(exception) } returns 1L

        val result = saveSchedule.saveExceptionWithConflictCheck(exception)

        coVerify(inverse = true) { scheduleRepository.deleteAlarm(any()) }
        coVerify { scheduleRepository.saveAlarm(exception) }
        assert(!result)
    }
}
