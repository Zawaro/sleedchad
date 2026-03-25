package com.zawaro.sleepchad.presentation.schedule

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.core.TimeFormatter
import com.zawaro.sleepchad.data.CustomAlarmEntity
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsScreen
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.presentation.settings.SetupDialog
import kotlinx.coroutines.launch
import java.util.Calendar

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    @Suppress("UNUSED_PARAMETER") settingsViewModel: SettingsViewModel,
    @Suppress("UNUSED_PARAMETER") onThemeChanged: (Int) -> Unit = {}
) {
    val exceptionAlarms = viewModel.exceptionAlarms.collectAsState().value.map { alarm ->
        ScheduleViewModel.AlarmUiModel(
            id = alarm.id,
            name = if (alarm.name.isEmpty()) "Custom" else alarm.name,
            isDefaultAlarm = false,
            enabledDays = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysSet(alarm.enabledDaysString),
            bedtimeMs = alarm.bedtimeMs,
            wakeupMs = alarm.wakeupMs,
        )
    }
    val preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel = preferencesViewModel.preferences.collectAsState().value
    val onboardingComplete: Boolean = preferencesViewModel.onboardingComplete.collectAsState(initial = false).value
    
    var currentScreen: Screen = remember { mutableStateOf(Screen.Schedule).value }
    var showAddAlarmDialog: Boolean = remember { mutableStateOf(false).value }
    var showSetupDialog: Boolean = remember { mutableStateOf(!onboardingComplete && (preferences.targetSleepDurationMinutes == null || preferences.wakeUpTimeMs == null)).value }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SleepChad") },
                navigationIcon = {
                    if (currentScreen != Screen.Schedule) {
                        IconButton(onClick = { currentScreen = Screen.Schedule }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentScreen == Screen.Schedule) {
                        MenuActions(
                            onSettingsClicked = { currentScreen = Screen.Settings },
                            onAboutClicked = { currentScreen = Screen.About })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAlarmDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Alarm")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                is Screen.Schedule -> ScheduleContent(
                    preferences = preferences,
                    customAlarms = exceptionAlarms.map { alarm ->
                        CustomAlarmUiModel(
                            id = alarm.id,
                            name = if (alarm.name.isEmpty()) "Custom" else alarm.name,
                            enabledDays = alarm.enabledDays,
                            targetSleepDurationMinutes = alarm.bedtimeMs?.let { bedtime ->
                                val wakeTime = alarm.wakeupMs ?: (7 * 60 * 60 * 1000L)
                                val diffMs = wakeTime - bedtime
                                ((diffMs / 60000).toInt())
                            },
                            wakeupMs = alarm.wakeupMs,
                        )
                    },
                    preferencesViewModel = preferencesViewModel,
                    onDeleteAlarm = { alarmId ->
                        viewModel.deleteExceptionAlarm(alarmId)
                    }
                )

                is Screen.Settings -> SettingsScreen(onClose = { currentScreen = Screen.Schedule }, preferencesViewModel = preferencesViewModel)
                is Screen.About -> AboutScreen(onClose = { currentScreen = Screen.Schedule })
            }
            
            if (showAddAlarmDialog) {
                AddCustomAlarmDialog(
                    preferences = preferences,
                    existingAlarms = exceptionAlarms.map { alarm ->
                        CustomAlarmUiModel(
                            id = alarm.id,
                            name = if (alarm.name.isEmpty()) "Custom" else alarm.name,
                            enabledDays = alarm.enabledDays,
                            targetSleepDurationMinutes = null,
                            wakeupMs = alarm.wakeupMs,
                        )
                    },
                    onDismiss = { showAddAlarmDialog = false },
                    onSave = { name, selectedDays, hours, minutes, _ ->
                        viewModel.createExceptionAlarmWithTime(
                            name = name.ifEmpty { "Custom Alarm" },
                            enabledDays = selectedDays,
                            targetSleepHours = hours,
                            targetSleepMinutes = minutes
                        )
                        showAddAlarmDialog = false
                    }
                )
            }

            if (showSetupDialog) {
                SetupDialog(
                    onDismissRequest = { },
                    onSavePreferences = { targetSleepHours: Int, targetSleepMinutes: Int, wakeUpTimeMs: Long, errandsDurationHours: Int, errandsDurationMinutes: Int ->
                        preferencesViewModel.saveFullPreferences(
                            (targetSleepHours * 60) + targetSleepMinutes, 
                            wakeUpTimeMs,
                            (errandsDurationHours * 60) + errandsDurationMinutes
                        )
                        preferencesViewModel.completeSetup()
                        showSetupDialog = false
                    }
                )
            }
        }
    }
}
