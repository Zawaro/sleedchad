package com.zawaro.sleepchad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.zawaro.sleepchad.data.AppDatabase
import com.zawaro.sleepchad.domain.usecases.GetScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.SaveScheduleUseCase
import com.zawaro.sleepchad.domain.usecases.ScheduleAlarmsUseCase
import com.zawaro.sleepchad.domain.usecases.RecordBedtimeUseCase
import com.zawaro.sleepchad.domain.usecases.RecordWakeUpUseCase
import com.zawaro.sleepchad.domain.usecases.GetLastNightSleepSessionUseCase
import com.zawaro.sleepchad.domain.usecases.GetExceptionAlarmsUseCase
import com.zawaro.sleepchad.domain.usecases.CreateExceptionAlarmUseCase
import com.zawaro.sleepchad.domain.usecases.DeleteExceptionAlarmUseCase
import com.zawaro.sleepchad.data.SleepSessionRepository
import com.zawaro.sleepchad.data.ScheduleRepository
import com.zawaro.sleepchad.domain.repository.UserPreferencesRepository
import com.zawaro.sleepchad.data.ErrandRepository
import com.zawaro.sleepchad.domain.usecases.GetUserPreferencesUseCase
import com.zawaro.sleepchad.domain.usecases.SaveUserPreferencesUseCase
import com.zawaro.sleepchad.presentation.statistics.StatisticsViewModel
import com.zawaro.sleepchad.presentation.navigation.SleepChadNavGraph
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.ui.theme.SleepChadAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getInstance(this)
        val scheduleDao = database.scheduleDao()
        val errandDao = database.errandDao()
        val sleepSessionDao = database.sleepSessionDao()
        
        val repository = ScheduleRepository(scheduleDao, errandDao)
        val sleepSessionRepository = SleepSessionRepository(sleepSessionDao)
        val errandRepository = com.zawaro.sleepchad.data.ErrandRepository(errandDao)
        
        val getScheduleUseCase = GetScheduleUseCase(repository)
        val saveScheduleUseCase = SaveScheduleUseCase(repository, errandRepository)
        val scheduleAlarmsUseCase = ScheduleAlarmsUseCase(this, repository)
        
        val userPrefsRepo = UserPreferencesRepository(repository, sleepSessionRepository)
        val getUserPrefs = GetUserPreferencesUseCase(userPrefsRepo)
        val saveUserPrefs = SaveUserPreferencesUseCase(userPrefsRepo)
        val recordBedtime = RecordBedtimeUseCase(userPrefsRepo)
        val recordWakeUp = RecordWakeUpUseCase(userPrefsRepo)
        val getLastNightSession = GetLastNightSleepSessionUseCase(userPrefsRepo)
        
        val customAlarmRepo = com.zawaro.sleepchad.domain.repository.CustomAlarmRepository(repository)
        val getExceptionAlarmsUseCase = GetExceptionAlarmsUseCase(customAlarmRepo)
        val createExceptionAlarmUseCase = CreateExceptionAlarmUseCase(customAlarmRepo)
        val deleteExceptionAlarmUseCase = DeleteExceptionAlarmUseCase(customAlarmRepo)
        
        val scheduleFactory = com.zawaro.sleepchad.presentation.schedule.ScheduleViewModelFactory(
            getSchedule = getScheduleUseCase,
            saveSchedule = saveScheduleUseCase,
            scheduleAlarms = scheduleAlarmsUseCase,
            errandRepository = errandRepository,
        )
        
        val preferencesFactory = PreferencesViewModelFactory(getUserPrefs, saveUserPrefs, recordBedtime, recordWakeUp, getLastNightSession)
        val statisticsViewModelFactory = StatisticsViewModelFactory(database)

        setContent {
            SleepChadAppTheme {
                val scheduleViewModel: ScheduleViewModel = viewModel(factory = scheduleFactory)
                val preferencesViewModel: PreferencesViewModel = viewModel(factory = preferencesFactory)
                val settingsViewModel = viewModel<SettingsViewModel>()
                val statisticsViewModel: StatisticsViewModel = viewModel(factory = statisticsViewModelFactory)
                
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    preferencesViewModel.loadPreferencesWithSystemDetection(this@MainActivity, saveUserPrefs)
                }
                
                SleepChadNavGraph(
                    scheduleViewModel = scheduleViewModel,
                    preferencesViewModel = preferencesViewModel,
                    settingsViewModel = settingsViewModel,
                    statisticsViewModel = statisticsViewModel,
                    navController = navController
                )
            }
        }
    }
}

class PreferencesViewModelFactory(
    private val getUserPreferences: GetUserPreferencesUseCase,
    private val saveUserPreferences: SaveUserPreferencesUseCase,
    private val recordBedtime: RecordBedtimeUseCase?,
    private val recordWakeUp: RecordWakeUpUseCase?,
    private val getLastNightSession: GetLastNightSleepSessionUseCase?,
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            return PreferencesViewModel(getUserPreferences, saveUserPreferences, recordBedtime, recordWakeUp, getLastNightSession) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StatisticsViewModelFactory(private val database: AppDatabase) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
