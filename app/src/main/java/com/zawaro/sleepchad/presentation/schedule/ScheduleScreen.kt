package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.data.CustomAlarmEntity.Companion.toDaysSet
import com.zawaro.sleepchad.presentation.components.GlobalAlarmToggleCard
import com.zawaro.sleepchad.presentation.components.WeekendRecoveryBottomSheetContent
import com.zawaro.sleepchad.presentation.settings.ErrandsDurationDialogContent
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.presentation.settings.SleepDurationDialogContent
import com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel
import com.zawaro.sleepchad.presentation.settings.WakeUpTimeDialogContent
import com.zawaro.sleepchad.presentation.statistics.StatisticsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenWrapper(
    currentRoute: String,
    scheduleViewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    @Suppress("UNUSED_PARAMETER") settingsViewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit,
    onBottomBarNavigate: (String) -> Unit,
    onNavigateToStatistics: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    showSuccessMessage: String? = null
) {
    val exceptionAlarms by remember(scheduleViewModel) {
        scheduleViewModel.exceptionAlarms
    }.collectAsState(initial = emptyList())

    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            com.zawaro.sleepchad.presentation.navigation.SleepChadTopAppBar(onSettingsClick = onNavigateToSettings)
        },
        bottomBar = {
            com.zawaro.sleepchad.presentation.navigation.SleepChadBottomBar(
                currentRoute = currentRoute,
                onNavigate = onBottomBarNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAlarmDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Alarm")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LaunchedEffect(Unit) {
                scheduleViewModel.snackbarEvent.collect { msg ->
                    snackbarHostState?.showSnackbar(msg)
                }
            }

            ScheduleContent(
                preferencesViewModel = preferencesViewModel,
                customAlarms = exceptionAlarms.map { alarm ->
                    CustomAlarmUiModel(
                        id = alarm.id,
                        name = if (alarm.name.isEmpty()) "Custom Alarm" else alarm.name,
                        isEnabled = alarm.isEnabled,
                        enabledDays = toDaysSet(alarm.enabledDaysString),
                        targetSleepDurationMinutes = alarm.targetSleepDurationMinutes,
                        wakeupMs = alarm.wakeupMs,
                        inheritsTargetSleep = alarm.targetSleepDurationMinutes == null,
                        inheritsWakeTime = alarm.wakeupMs == null,
                        hasIllogicalBedtime = false
                    )
                },
                onDeleteAlarm = { id ->
                    scheduleViewModel.deleteExceptionAlarm(id)
                },
                onClearAllAlarms = {
                    scheduleViewModel.deleteAllExceptionAlarms()
                },
                isAllAlarmsEnabled = true,
                onToggleAllAlarms = null,
                onNavigateToStatistics = onNavigateToStatistics,
                snackbarHostState = snackbarHostState,
                showSuccessMessage = showSuccessMessage,
                onToggleAlarm = { alarmId, isEnabled ->
                    scheduleViewModel.toggleExceptionAlarm(alarmId, isEnabled)
                },
                onUpdateAlarmDays = { id, days, name, bedtimeMs, wakeupMs ->
                    scheduleViewModel.updateExceptionAlarmEnabledDays(id, days, name, bedtimeMs, wakeupMs)
                }
            )

            if (showAddAlarmDialog) {
                AddAlarmDialogContent(
                    scheduleViewModel = scheduleViewModel,
                    onDismiss = { showAddAlarmDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAlarmDialogContent(
    scheduleViewModel: ScheduleViewModel,
    onDismiss: () -> Unit
) {
    var alarmName by remember { mutableStateOf("") }
    var hourSelection by remember { mutableStateOf(6) }
    var minuteSelection by remember { mutableStateOf(0) }
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Alarm") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = alarmName,
                    onValueChange = { alarmName = it },
                    label = { Text("Alarm Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Sleep Duration: ", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = String.format("%02d:%02d", hourSelection, minuteSelection),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hour", style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { hourSelection = (hourSelection - 1 + 24) % 24 }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease hour")
                            }
                            Text("$hourSelection", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            IconButton(onClick = { hourSelection = (hourSelection + 1) % 24 }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase hour")
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minute", style = MaterialTheme.typography.labelSmall)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { minuteSelection = (minuteSelection - 1 + 60) % 60 }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease minute")
                            }
                            Text("$minuteSelection", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            IconButton(onClick = { minuteSelection = (minuteSelection + 1) % 60 }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase minute")
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf(Pair(1, "M"), Pair(2, "T"), Pair(3, "W"), Pair(4, "T"), Pair(5, "F"), Pair(6, "S"), Pair(7, "S")).forEach { (dayNum, dayLabel) ->
                        Surface(
                            modifier = Modifier.size(40.dp).clickable {
                                selectedDays = if (dayNum in selectedDays) selectedDays - dayNum else selectedDays + dayNum
                            },
                            shape = CircleShape,
                            color = if (dayNum in selectedDays) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(2.dp, if (dayNum in selectedDays) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            shadowElevation = if (dayNum in selectedDays) 4.dp else 2.dp
                        ) {
                            Text(
                                text = dayLabel,
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (dayNum in selectedDays) FontWeight.Bold else FontWeight.Medium),
                                color = if (dayNum in selectedDays) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                scheduleViewModel.createExceptionAlarmWithTime(
                    name = alarmName,
                    enabledDays = selectedDays,
                    targetSleepHours = hourSelection,
                    targetSleepMinutes = minuteSelection
                )
            }) { Text("Save Alarm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleContent(
    preferencesViewModel: PreferencesViewModel,
    customAlarms: List<CustomAlarmUiModel>,
    onDeleteAlarm: (Long) -> Unit,
    onClearAllAlarms: () -> Unit,
    isAllAlarmsEnabled: Boolean = true,
    onToggleAllAlarms: ((Boolean) -> Unit)? = null,
    onNavigateToStatistics: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    showSuccessMessage: String? = null,
    onToggleAlarm: (Long, Boolean) -> Unit = { _, _ -> },
    onUpdateAlarmDays: (Long, Set<Int>, String, Long?, Long?) -> Unit = { _, _, _, _, _ -> }
) {
    val preferences by preferencesViewModel.preferences.collectAsState()

    var showSleepDurationDialog by remember { mutableStateOf(false) }
    var showWakeUpTimeDialog by remember { mutableStateOf(false) }
    var showErrandsDurationDialog by remember { mutableStateOf(false) }
    var showWeekendRecoveryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage != null && snackbarHostState != null) {
            snackbarHostState.showSnackbar(showSuccessMessage)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            if (onToggleAllAlarms != null) {
                GlobalAlarmToggleCard(
                    isAllAlarmsEnabled = isAllAlarmsEnabled,
                    onToggle = onToggleAllAlarms,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text("GLOBAL OPTIMIZATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TargetSleepCard(preferences, onClick = { showSleepDurationDialog = true })
                WakeUpTimeCard(preferences, onClick = { showWakeUpTimeDialog = true })
                ErrandsDurationCard(preferences, onClick = { showErrandsDurationDialog = true })
            }

            if (showSleepDurationDialog) {
                val initialHours = (preferences.targetSleepDurationMinutes ?: 480) / 60
                val initialMinutes = (preferences.targetSleepDurationMinutes ?: 480) % 60
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()

                ModalBottomSheet(
                    onDismissRequest = { showSleepDurationDialog = false },
                    sheetState = sheetState
                ) {
                    SleepDurationDialogContent(
                        initialHours = initialHours,
                        initialMinutes = initialMinutes,
                        onSave = { hours, minutes ->
                            preferencesViewModel.updateTargetSleepDuration((hours * 60) + minutes)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showSleepDurationDialog = false
                            }
                        }
                    )
                }
            }

            if (showWakeUpTimeDialog) {
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()

                ModalBottomSheet(
                    onDismissRequest = { showWakeUpTimeDialog = false },
                    sheetState = sheetState
                ) {
                    WakeUpTimeDialogContent(
                        initialEpochMs = preferences.wakeUpTimeMs ?: 0,
                        onSave = { epochMs ->
                            preferencesViewModel.updateWakeUpTime(epochMs)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showWakeUpTimeDialog = false
                            }
                        }
                    )
                }
            }

            if (showErrandsDurationDialog) {
                val initialHours = (preferences.errandsDurationMinutes ?: 0) / 60
                val initialMinutes = (preferences.errandsDurationMinutes ?: 0) % 60
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()

                ModalBottomSheet(
                    onDismissRequest = { showErrandsDurationDialog = false },
                    sheetState = sheetState
                ) {
                    ErrandsDurationDialogContent(
                        initialHours = initialHours,
                        initialMinutes = initialMinutes,
                        onSave = { hours, minutes ->
                            preferencesViewModel.updateErrandsDuration((hours * 60) + minutes)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showErrandsDurationDialog = false
                            }
                        }
                    )
                }
            }

            // Weekend Recovery toggle and card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Weekend, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("WEEKEND RECOVERY", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Separate schedule for Sat & Sun", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = preferences.weekendRecoveryEnabled,
                        onCheckedChange = { preferencesViewModel.toggleWeekendRecovery(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            if (preferences.weekendRecoveryEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { showWeekendRecoveryDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Weekend, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                preferences.weekendTargetSleepDurationMinutes?.let { duration ->
                                    val hours = duration / 60
                                    val mins = duration % 60
                                    Text("Sleep ${hours}h ${mins}m", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                } ?: Text("Set weekend sleep", style = MaterialTheme.typography.bodyMedium)
                                preferences.weekendWakeUpTimeMs?.let { epochMs ->
                                    val cal = java.util.Calendar.getInstance()
                                    cal.timeInMillis = epochMs
                                    Text("Wake ${String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } ?: Text("Set weekend wake-up", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Edit weekend", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (showWeekendRecoveryDialog) {
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                val initialSleepHours = (preferences.weekendTargetSleepDurationMinutes ?: (preferences.targetSleepDurationMinutes ?: 480)) / 60
                val initialSleepMinutes = (preferences.weekendTargetSleepDurationMinutes ?: (preferences.targetSleepDurationMinutes ?: 480)) % 60
                val initialWakeMs = preferences.weekendWakeUpTimeMs ?: (preferences.wakeUpTimeMs ?: (7 * 3600000L))
                val initialErrandsHours = (preferences.weekendErrandsDurationMinutes ?: (preferences.errandsDurationMinutes ?: 30)) / 60
                val initialErrandsMinutes = (preferences.weekendErrandsDurationMinutes ?: (preferences.errandsDurationMinutes ?: 30)) % 60

                ModalBottomSheet(
                    onDismissRequest = { showWeekendRecoveryDialog = false },
                    sheetState = sheetState
                ) {
                    WeekendRecoveryBottomSheetContent(
                        initialSleepHours = initialSleepHours,
                        initialSleepMinutes = initialSleepMinutes,
                        initialWakeMs = initialWakeMs,
                        initialErrandsHours = initialErrandsHours,
                        initialErrandsMinutes = initialErrandsMinutes,
                        onSave = { sleepH: Int, sleepM: Int, wakeMs: Long, errandsH: Int, errandsM: Int ->
                            preferencesViewModel.updateWeekendPreferences(
                                targetSleepDurationMinutes = (sleepH * 60) + sleepM,
                                wakeUpTimeMs = wakeMs,
                                errandsDurationMinutes = (errandsH * 60) + errandsM
                            )
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showWeekendRecoveryDialog = false
                            }
                        }
                    )
                }
            }

            preferences.lastNightEstimatedSleepMinutes?.let { duration ->
                val hours = duration / 60
                val mins = duration % 60

                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToStatistics() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Last night: ${hours}h ${mins}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                val targetSleepDurationMinutes = preferences.targetSleepDurationMinutes ?: 480
                                val percentage = ((duration.toFloat() / targetSleepDurationMinutes) * 100).toInt().coerceIn(0, 100)
                                Text("$percentage% of your daily discipline target reached.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            GradientButton(
                onClick = { preferencesViewModel.recordBedtime() },
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.primary),
                content = {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I'M GOING TO BED", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp, fontSize = 14.sp), color = Color(0xFF1B263B))
                }
            )

            GradientOutlinedButton(
                onClick = { preferencesViewModel.recordWakeUp() },
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Icon(Icons.Default.AlarmOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I WOKE UP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp, fontSize = 14.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Custom Schedules", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                    if (customAlarms.isEmpty()) {
                        Text("No custom alarms yet", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text("Override schedule", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (customAlarms.isNotEmpty()) {
                    TextButton(onClick = onClearAllAlarms) {
                        Text("CLEAR ALL", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (customAlarms.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AlarmOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No custom alarms", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap + to add your first custom alarm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                customAlarms.forEach { alarm ->
                    CustomAlarmAccordion(
                        alarm,
                        onDelete = { onDeleteAlarm(alarm.id) },
                        onToggleEnabled = { isEnabled -> onToggleAlarm(alarm.id, isEnabled) },
                        onEnabledDaysChanged = { newDays ->
                            onUpdateAlarmDays(alarm.id, newDays, alarm.name, alarm.targetSleepDurationMinutes?.let { it.toLong() * 60000 }, alarm.wakeupMs)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetSleepCard(preferences: UserPreferencesUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("TARGET SLEEP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    preferences.targetSleepDurationMinutes?.let { duration ->
                        val hours = duration / 60
                        val mins = duration % 60
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
                    } ?: run {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to set", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit target sleep", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WakeUpTimeCard(preferences: UserPreferencesUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("WAKE UP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    preferences.wakeUpTimeMs?.let { epochMs ->
                        val calendar = java.util.Calendar.getInstance()
                        calendar.timeInMillis = epochMs
                        val hours = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                        val minutes = calendar.get(java.util.Calendar.MINUTE)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(String.format("%02d:%02d", hours, minutes), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
                    } ?: run {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to set", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit wake up time", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrandsDurationCard(preferences: UserPreferencesUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("ERRANDS BUFFER", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    preferences.errandsDurationMinutes?.let { duration ->
                        val hours = duration / 60
                        val mins = duration % 60
                        Spacer(modifier = Modifier.height(4.dp))
                        if (hours > 0) {
                            Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
                        } else {
                            Text("${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
                        }
                    } ?: run {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap to set", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit errands buffer", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CustomAlarmAccordion(
    alarm: CustomAlarmUiModel,
    onDelete: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit = {},
    onEnabledDaysChanged: (Set<Int>) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var localEnabledDays by remember { mutableStateOf(alarm.enabledDays) }
    var localIsEnabled by remember { mutableStateOf(alarm.isEnabled) }
    LaunchedEffect(alarm.id) {
        localEnabledDays = alarm.enabledDays
        localIsEnabled = alarm.isEnabled
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val minutes = alarm.wakeupMs?.let { epochMs ->
                    val calendar = java.util.Calendar.getInstance()
                    calendar.timeInMillis = epochMs
                    ((calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60) + calendar.get(java.util.Calendar.MINUTE))
                } ?: 480

                val hours = minutes / 60
                val mins = minutes % 60

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(String.format("%02d:%02d", hours, mins), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))

                    if (alarm.inheritsWakeTime) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Download, contentDescription = "Inherited", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = localIsEnabled && localEnabledDays.isNotEmpty(),
                        onCheckedChange = { enabled ->
                            localIsEnabled = enabled
                            onToggleEnabled(enabled)
                        }
                    )

                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(alarm.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

            val daysStr = localEnabledDays.sorted().joinToString(", ") { day ->
                when (day) {
                    1 -> "M"; 2 -> "T"; 3 -> "W"; 4 -> "T"; 5 -> "F"; 6 -> "S"; 7 -> "S"; else -> ""
                }
            }
            Text(daysStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val dayItems = localEnabledDays.toList().sorted()
                items(dayItems, key = { it }) { dayNum ->
                    val daysMap = mapOf(
                        Pair(1, "M"), Pair(2, "T"), Pair(3, "W"),
                        Pair(4, "T"), Pair(5, "F"), Pair(6, "S"), Pair(7, "S")
                    )
                    val dayLabel = daysMap[dayNum] ?: ""

                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        border = BorderStroke(2.dp, Color.Transparent),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = dayLabel,
                            modifier = Modifier.padding(top = 6.dp),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..7).forEach { dayNum ->
                        val isSelected = dayNum in localEnabledDays
                        Surface(
                            modifier = Modifier.size(40.dp).clickable {
                                localEnabledDays = if (isSelected) localEnabledDays - dayNum else localEnabledDays + dayNum
                                onEnabledDaysChanged(localEnabledDays)
                            },
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(2.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            shadowElevation = if (isSelected) 4.dp else 2.dp
                        ) {
                            Text(
                                text = when (dayNum) { 1 -> "M"; 2 -> "T"; 3 -> "W"; 4 -> "T"; 5 -> "F"; 6 -> "S"; 7 -> "S"; else -> "" },
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {}) {
                        Icon(Icons.Default.Snooze, contentDescription = "Snooze", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("5M")
                    }

                    TextButton(onClick = {}) {
                        Icon(Icons.Default.Vibration, contentDescription = "Vibrate", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VIBRATE")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete alarm", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
