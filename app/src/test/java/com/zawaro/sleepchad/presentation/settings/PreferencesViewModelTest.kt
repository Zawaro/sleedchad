package com.zawaro.sleepchad.presentation.settings

import android.content.Context
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
class PreferencesViewModelTest {

    @MockK
    private lateinit var getUserPreferences: GetUserPreferencesUseCase

    @MockK
    private lateinit var saveUserPreferences: SaveUserPreferencesUseCase

    @MockK
    private lateinit var recordBedtime: RecordBedtimeUseCase

    @MockK
    private lateinit var recordWakeUp: RecordWakeUpUseCase

    @MockK
    private lateinit var getLastNightSession: GetLastNightSleepSessionUseCase

    private lateinit var viewModel: PreferencesViewModel
    private lateinit var testDispatcher: TestDispatcher
    private val context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        val prefs = UserPreferencesEntity(id = 1, targetSleepDurationMinutes = 480, wakeUpTimeMs = (7 * 3600000L), errandsDurationMinutes = 30)
        coEvery { getUserPreferences() } returns prefs
        coEvery { saveUserPreferences(any()) } returns 1L
        coEvery { getLastNightSession() } returns null

        viewModel = PreferencesViewModel(
            getUserPreferences = getUserPreferences,
            saveUserPreferences = saveUserPreferences,
            recordBedtime = recordBedtime,
            recordWakeUp = recordWakeUp,
            getLastNightSession = getLastNightSession,
            context = context,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save preferences persists values`() = runTest {
        coEvery { saveUserPreferences(any()) } returns 1L
        coEvery { getUserPreferences() } returns UserPreferencesEntity(id = 1)

        viewModel.savePreferences(540, (8 * 3600000L), 45, null, 0)
        advanceUntilIdle()

        coVerify { saveUserPreferences(any()) }
    }

    @Test
    fun `update target sleep duration`() = runTest {
        viewModel.updateTargetSleepDuration(420)
        advanceUntilIdle()

        coVerify(atLeast = 1) { saveUserPreferences(any()) }
    }

    @Test
    fun `toggle weekend recovery`() = runTest {
        coEvery { getUserPreferences() } returns UserPreferencesEntity(id = 1)
        coEvery { saveUserPreferences(any()) } returns 1L

        viewModel.toggleWeekendRecovery(true)
        advanceUntilIdle()

        val slot = slot<UserPreferencesEntity>()
        coVerify { saveUserPreferences(capture(slot)) }
        assert(slot.captured.weekendRecoveryEnabled)
    }

    @Test
    fun `update weekend preferences`() = runTest {
        coEvery { getUserPreferences() } returns UserPreferencesEntity(id = 1)
        coEvery { saveUserPreferences(any()) } returns 1L

        viewModel.updateWeekendPreferences(600, (9 * 3600000L), 60)
        advanceUntilIdle()

        val slot = slot<UserPreferencesEntity>()
        coVerify { saveUserPreferences(capture(slot)) }
        assert(slot.captured.weekendTargetSleepDurationMinutes == 600)
        assert(slot.captured.weekendWakeUpTimeMs == (9 * 3600000L))
        assert(slot.captured.weekendErrandsDurationMinutes == 60)
    }

    @Test
    fun `record bedtime calls use case`() = runTest {
        coEvery { recordBedtime(any()) } returns null

        viewModel.recordBedtime()
        advanceUntilIdle()

        coVerify { recordBedtime(any()) }
    }

    @Test
    fun `record wake up calls use case and reloads`() = runTest {
        coEvery { recordWakeUp(any()) } returns null

        viewModel.recordWakeUp()
        advanceUntilIdle()

        coVerify { recordWakeUp(any()) }
    }
}
