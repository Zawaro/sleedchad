package com.zawaro.sleepchad.presentation.schedule

import com.zawaro.sleepchad.data.CustomAlarmEntity
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.data.UserPreferencesEntity
import com.zawaro.sleepchad.domain.usecases.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {

    @MockK
    private lateinit var getSchedule: GetScheduleUseCase

    @MockK
    private lateinit var saveSchedule: SaveScheduleUseCase

    @MockK
    private lateinit var scheduleAlarms: ScheduleAlarmsUseCase

    @MockK
    private lateinit var errandRepository: ErrandRepository

    @MockK
    private lateinit var getUserPreferences: GetUserPreferencesUseCase

    @MockK
    private lateinit var getExceptionAlarms: GetExceptionAlarmsUseCase

    @MockK
    private lateinit var createExceptionAlarm: CreateExceptionAlarmUseCase

    @MockK
    private lateinit var deleteExceptionAlarm: DeleteExceptionAlarmUseCase

    private lateinit var viewModel: ScheduleViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        coEvery { getSchedule() } returns null
        coEvery { getUserPreferences() } returns null
        coEvery { scheduleAlarms() } just runs
        coEvery { getExceptionAlarms() } returns emptyList()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create exception alarm loads alarms after save`() = runTest {
        createViewModel()
        coEvery { saveSchedule(any()) } returns 1L

        viewModel.createExceptionAlarm("Test", setOf(1, 3, 5), null, null)

        coVerify { saveSchedule(any()) }
        coVerify(atLeast = 2) { getExceptionAlarms() }
    }

    @Test
    fun `createExceptionAlarmWithTime calculates bedtime from wake time`() = runTest {
        coEvery { getUserPreferences() } returns UserPreferencesEntity(
            id = 1, wakeUpTimeMs = (7 * 3600000L), targetSleepDurationMinutes = 480,
        )
        createViewModel()
        coEvery { saveSchedule(any()) } returns 1L

        viewModel.createExceptionAlarmWithTime("Gym", setOf(1, 3, 5), 8, 0)

        val slot = slot<com.zawaro.sleepchad.data.ScheduleEntity>()
        coVerify { saveSchedule(capture(slot)) }
        assert(slot.captured.name == "Gym")
    }

    @Test
    fun `delete exception alarm with alarms present`() = runTest {
        coEvery { getExceptionAlarms() } returns listOf(CustomAlarmEntity(id = 1L, name = "Test", enabledDaysString = "1,3,5"))
        createViewModel()
        coEvery { deleteExceptionAlarm(1L) } just runs

        viewModel.deleteExceptionAlarm(1L)

        coVerify { deleteExceptionAlarm(1L) }
    }

    @Test
    fun `toggle exception alarm with alarms present`() = runTest {
        coEvery { getExceptionAlarms() } returns listOf(CustomAlarmEntity(id = 2L, name = "Toggle", isEnabled = true, enabledDaysString = "6,7"))
        createViewModel()
        coEvery { createExceptionAlarm(any()) } returns 2L

        viewModel.toggleExceptionAlarm(2L, false)

        val slot = slot<CustomAlarmEntity>()
        coVerify { createExceptionAlarm(capture(slot)) }
        assert(!slot.captured.isEnabled)
    }

    @Test
    fun `delete all exception alarms with alarms present`() = runTest {
        coEvery { getExceptionAlarms() } returns listOf(
            CustomAlarmEntity(id = 1L, name = "A", enabledDaysString = "1"),
            CustomAlarmEntity(id = 2L, name = "B", enabledDaysString = "2"),
        )
        createViewModel()
        coEvery { deleteExceptionAlarm(any()) } just runs

        viewModel.deleteAllExceptionAlarms()

        coVerify { deleteExceptionAlarm(1L) }
        coVerify { deleteExceptionAlarm(2L) }
    }

    private fun createViewModel() {
        viewModel = ScheduleViewModel(
            getSchedule = getSchedule,
            saveSchedule = saveSchedule,
            scheduleAlarms = scheduleAlarms,
            errandRepository = errandRepository,
            getUserPreferences = getUserPreferences,
            getExceptionAlarmsUseCase = getExceptionAlarms,
            createExceptionAlarmUseCase = createExceptionAlarm,
            deleteExceptionAlarmUseCase = deleteExceptionAlarm,
        )
    }
}
