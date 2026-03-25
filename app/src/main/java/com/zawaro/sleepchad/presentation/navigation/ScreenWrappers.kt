package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.zawaro.sleepchad.data.CustomAlarmEntity.Companion.toDaysSet
import com.zawaro.sleepchad.domain.repository.CustomAlarmRepository
import com.zawaro.sleepchad.presentation.schedule.CustomAlarmUiModel
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel
import com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.domain.usecases.SaveUserPreferencesUseCase
import com.zawaro.sleepchad.presentation.components.GlobalAlarmToggleCard
import com.zawaro.sleepchad.presentation.schedule.AboutScreen

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

    var isAllAlarmsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            SleepChadTopAppBar(onSettingsClick = onNavigateToSettings)
        },
        bottomBar = {
            SleepChadBottomBar(
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
            val alarmEnabledStates = remember { mutableStateMapOf<Long, Boolean>() }
            
            ScheduleContent(
                preferencesViewModel = preferencesViewModel,
                currentRoute = currentRoute,
                customAlarms = exceptionAlarms.map { alarm ->
                    val isEnabled = alarmEnabledStates[alarm.id] ?: true
                    if (!alarmEnabledStates.containsKey(alarm.id)) {
                        alarmEnabledStates[alarm.id] = isEnabled
                    }
                    CustomAlarmUiModel(
                        id = alarm.id,
                        name = if (alarm.name.isEmpty()) "Custom Alarm" else alarm.name,
                        enabledDays = com.zawaro.sleepchad.data.CustomAlarmEntity.toDaysSet(alarm.enabledDaysString),
                        targetSleepDurationMinutes = 480,
                        wakeupMs = alarm.wakeupMs,
                        inheritsTargetSleep = false,
                        inheritsWakeTime = alarm.wakeupMs == null,
                        hasIllogicalBedtime = false
                    )
                },
                onDeleteAlarm = { id ->
                    scheduleViewModel.deleteExceptionAlarm(id)
                },
                onAddAlarmClick = { showAddAlarmDialog = true },
                onClearAllAlarms = { 
                    exceptionAlarms.forEach { alarm -> 
                        scheduleViewModel.deleteExceptionAlarm(alarm.id) 
                    }
                },
                isAllAlarmsEnabled = isAllAlarmsEnabled,
                onToggleAllAlarms = { newStatus ->
                    isAllAlarmsEnabled = newStatus
                },
                onNavigateToStatistics = onNavigateToStatistics,
                snackbarHostState = snackbarHostState,
                showSuccessMessage = showSuccessMessage,
                onToggleAlarm = { alarmId, isEnabled ->
                    alarmEnabledStates[alarmId] = isEnabled
                }
            )
            
            if (showAddAlarmDialog) {
                var alarmName by remember { mutableStateOf("") }
                var hourSelection by remember { mutableStateOf(6) }
                var minuteSelection by remember { mutableStateOf(0) }
                var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }
                
                AlertDialog(
                    onDismissRequest = { showAddAlarmDialog = false },
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
                                Text("Time: ", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = String.format("%02d:%02d", hourSelection, minuteSelection),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Hour", style = MaterialTheme.typography.labelSmall)
                                    ExposedDropdownMenuBox(
                                        expanded = remember { mutableStateOf(false) }.value,
                                        onExpandedChange = {}
                                    ) {
                                        OutlinedTextField(
                                            value = "$hourSelection",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("0-23") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Minute", style = MaterialTheme.typography.labelSmall)
                                    ExposedDropdownMenuBox(
                                        expanded = remember { mutableStateOf(false) }.value,
                                        onExpandedChange = {}
                                    ) {
                                        OutlinedTextField(
                                            value = "$minuteSelection",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("0-59") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
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
                            showAddAlarmDialog = false
                            val _unusedTime = System.currentTimeMillis() + ((hourSelection * 3600000) + (minuteSelection * 60000))
                            scheduleViewModel.createExceptionAlarmWithTime(
                                name = "Custom Alarm",
                                enabledDays = selectedDays,
                                targetSleepHours = 8,
                                targetSleepMinutes = 0
                            )
                        }) { Text("Save Alarm") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddAlarmDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleContent(
    preferencesViewModel: PreferencesViewModel,
    currentRoute: String,
    customAlarms: List<CustomAlarmUiModel>,
    onDeleteAlarm: (Long) -> Unit,
    onAddAlarmClick: () -> Unit,
    onClearAllAlarms: () -> Unit,
    isAllAlarmsEnabled: Boolean = true,
    onToggleAllAlarms: ((Boolean) -> Unit)? = null,
    onNavigateToStatistics: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    showSuccessMessage: String? = null,
    onToggleAlarm: (Long, Boolean) -> Unit = { _, _ -> }
) {
    val preferences by preferencesViewModel.preferences.collectAsState()

    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage != null && snackbarHostState != null) {
            snackbarHostState.showSnackbar(showSuccessMessage)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("GLOBAL OPTIMIZATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TargetSleepCard(preferences)
            WakeUpTimeCard(preferences)
            ErrandsDurationCard(preferences)
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
            colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("I'M GOING TO BED", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp), color = Color.White)
        }

        GradientOutlinedButton(
            onClick = { preferencesViewModel.recordWakeUp() },
            modifier = Modifier.fillMaxWidth(),
            colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
        ) {
            Icon(Icons.Default.AlarmOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("I WOKE UP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (onToggleAllAlarms != null) {
            GlobalAlarmToggleCard(
                isAllAlarmsEnabled = isAllAlarmsEnabled,
                onToggle = onToggleAllAlarms,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                TextButtonGreen(onClick = onClearAllAlarms) {
                    Text("CLEAR ALL")
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
                    preferences, 
                    onDelete = { onDeleteAlarm(alarm.id) },
                    onToggleEnabled = { isEnabled -> onToggleAlarm(alarm.id, isEnabled) }
                )
            }
        }
    }
}

@Composable
private fun TargetSleepCard(preferences: UserPreferencesUiModel) {
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
private fun WakeUpTimeCard(preferences: UserPreferencesUiModel) {
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
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("WAKE UP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    preferences.wakeUpTimeMs?.let { ms ->
                        val wakeText = formatTime(ms)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(wakeText, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
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
private fun ErrandsDurationCard(preferences: UserPreferencesUiModel) {
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
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("ERRANDS BUFFER", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    preferences.errandsDurationMinutes?.let { duration ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${duration}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp))
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
private fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(onClick = onClick)
                .minimumInteractiveComponentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun GradientOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp), ambientColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f), spotColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                .clickable(onClick = onClick)
                .minimumInteractiveComponentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun TextButtonGreen(onClick: () -> Unit, content: @Composable () -> Unit) {
    val buttonModifier = Modifier
        .background(Color.Transparent, RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
    
    Row(
        modifier = buttonModifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun CustomAlarmAccordion(
    alarm: CustomAlarmUiModel,
    preferences: UserPreferencesUiModel,
    onDelete: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var enabledDays by remember { mutableStateOf(alarm.enabledDays.toMutableSet()) }
    var isEnabled by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (alarm.hasIllogicalBedtime) 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
        else 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    modifier = Modifier.size(48.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(alarm.name.ifEmpty { "Custom Alarm" }, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        if (alarm.inheritsTargetSleep || alarm.inheritsWakeTime) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = "Inherited values",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    val daysLabel = enabledDays.sorted().joinToString(", ") { getDayLabel(it) }
                    val label = "${formatAlarmTime(alarm)} • ${daysLabel}"
                    Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = if (alarm.hasIllogicalBedtime) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isEnabled, onCheckedChange = { 
                        isEnabled = it
                        onToggleEnabled(it)
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (alarm.hasIllogicalBedtime && isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Warning: Calculated bedtime is illogical (after wake time next day)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        (1..7).forEach { dayNum ->
                            val isSelected = dayNum in alarm.enabledDays
                            val dayLabel = getDayLabel(dayNum)
                            
                            Surface(
                                modifier = Modifier.size(40.dp).clickable { 
                                    enabledDays = if (dayNum in enabledDays) {
                                        (enabledDays - dayNum).toMutableSet()
                                    } else {
                                        (enabledDays + dayNum).toMutableSet()
                                    }
                                },
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(2.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                shadowElevation = if (isSelected) 4.dp else 2.dp
                            ) {
                                Text(
                                    text = dayLabel,
                                    modifier = Modifier.padding(top = 6.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButtonGreen(onClick = {}) {
                            Text("SNOOZE: 5M")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButtonGreen(onClick = {}) {
                            Text("VIBRATE ONLY")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun formatTime(epochMs: Long): String {
    val calendar = java.util.Calendar.getInstance().apply { timeInMillis = epochMs }
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = calendar.get(java.util.Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}

@Composable
private fun formatAlarmTime(alarm: CustomAlarmUiModel): String {
    val wakeMs = alarm.wakeupMs ?: return "06:15"
    val hours = (wakeMs / 3600000) % 24
    val minutes = (wakeMs % 3600000) / 60000
    return String.format("%02d:%02d", hours, minutes)
}

private fun getDayLabel(dayNum: Int): String {
    return when (dayNum) {
        1 -> "M"
        2 -> "T"
        3 -> "W"
        4 -> "T"
        5 -> "F"
        6 -> "S"
        7 -> "S"
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWrapper(
    currentRoute: String,
    preferencesViewModel: PreferencesViewModel,
    onClose: () -> Unit,
    onNavigateToAbout: () -> Unit = {}
) {
    var showAboutScreen by remember { mutableStateOf(false) }
    
    if (showAboutScreen) {
        AboutScreen(
            onClose = { 
                showAboutScreen = false
                onClose()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("System Preferences", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                }
            }

item {
                SettingsSection(title = "Display & Time") {
                        val uiState by preferencesViewModel.preferences.collectAsState()
                        
                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(8.dp))
                                        
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Column {
                                                        Text("Theme", style = MaterialTheme.typography.bodyLarge)
                                                        val themeLabel = when (uiState.themeIndex) {
                                                                0 -> "System Default"
                                                                1 -> "Light"
                                                                2 -> "Dark"
                                                                else -> "System Default"
                                                        }
                                                        Text(themeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                
                                                var themeExpanded by remember { mutableStateOf(false) }
                                                
                                                ExposedDropdownMenuBox(
                                                        expanded = themeExpanded,
                                                        onExpandedChange = { themeExpanded = it }
                                                ) {
                                                        OutlinedTextField(
                                                                value = when (uiState.themeIndex) {
                                                                        0 -> "System Default"
                                                                        1 -> "Light"
                                                                        2 -> "Dark"
                                                                        else -> "System Default"
                                                                },
                                                                onValueChange = {},
                                                                readOnly = true,
                                                                label = { Text("Select Theme") },
                                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                                                                modifier = Modifier.fillMaxWidth()
                                                        )

                                                        ExposedDropdownMenu(
                                                                expanded = themeExpanded,
                                                                onDismissRequest = { themeExpanded = false }
                                                        ) {
                                                                listOf("System Default", "Light", "Dark").forEach { label ->
                                                                        DropdownMenuItem(
                                                                                text = { Text(label) },
                                                                                onClick = { 
                                                                                        preferencesViewModel.updateThemeIndex(
                                                                                                when (label) {
                                                                                                        "System Default" -> 0
                                                                                                        "Light" -> 1
                                                                                                        "Dark" -> 2
                                                                                                        else -> 0
                                                                                                }
                                                                                        )
                                                                                        themeExpanded = false 
                                                                                },
                                                                                enabled = uiState.themeIndex != when (label) {
                                                                                        "System Default" -> 0
                                                                                        "Light" -> 1
                                                                                        "Dark" -> 2
                                                                                        else -> 0
                                                                                }
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                        
                                        Spacer(Modifier.height(16.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(Modifier.height(16.dp))
                                        
                                        Text("Time Format", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(8.dp))
                                        
                                        val timeFormatOptions = listOf("System Default", "12-hour (AM/PM)", "24-hour")
                                        val expandedState = remember { mutableStateOf(false) }
                                        
                                        ExposedDropdownMenuBox(
                                                expanded = expandedState.value,
                                                onExpandedChange = { newState -> expandedState.value = newState }
                                        ) {
                                                OutlinedTextField(
                                                        value = when (uiState.timeFormatPreference?.toBoolean()) {
                                                                null -> "System Default"
                                                                true -> "24-hour"
                                                                false -> "12-hour"
                                                                else -> "System Default"
                                                        },
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select Format") },
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState.value) },
                                                        modifier = Modifier.fillMaxWidth()
                                                )

                                                ExposedDropdownMenu(
                                                        expanded = expandedState.value,
                                                        onDismissRequest = { expandedState.value = false }
                                                ) {
                                                        timeFormatOptions.forEach { label ->
                                                                DropdownMenuItem(
                                                                        text = { Text(label) },
                                                                        onClick = { 
                                                                                preferencesViewModel.updateTimeFormat(
                                                                                        when (label) {
                                                                                                "System Default" -> null
                                                                                                "12-hour (AM/PM)" -> "false"
                                                                                                "24-hour" -> "true"
                                                                                                else -> null
                                                                                        }
                                                                                )
                                                                                expandedState.value = false 
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }

        item {
                SettingsSection(title = "Communications") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val notifState = remember { mutableStateOf(true) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.NotificationsActive,
                                title = "Push Notifications",
                                subtitle = "Wake up alarms and wind-down alerts.",
                                enabledState = notifState
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            val weeklyState = remember { mutableStateOf(false) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Insights,
                                title = "Weekly Sleep Report",
                                subtitle = "Personalized performance analysis.",
                                enabledState = weeklyState
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            val newsletterState = remember { mutableStateOf(false) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Mail,
                                title = "Newsletter",
                                subtitle = "Tips for better sleep hygiene.",
                                enabledState = newsletterState
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Account") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            SettingsListItemWithClick(
                                leadingIcon = Icons.Default.Info,
                                title = "About",
                                onClick = { showAboutScreen = true }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItem(
                                leadingIcon = Icons.Default.PrivacyTip,
                                title = "Privacy Policy",
                                trailingIcon = Icons.Default.ChevronRight
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItem(
                                leadingIcon = Icons.Default.Description,
                                title = "Terms of Service",
                                trailingIcon = Icons.Default.ChevronRight
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            @Suppress("DEPRECATION")
                            SettingsListItem(
                                leadingIcon = Icons.Default.Logout,
                                title = "Logout",
                                trailingText = "",
                                textColor = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Version 2.4.0 (Alpha)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Disciplined Sleep for Chads.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    onNavigateToAbout: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsListItemWithClick(
    leadingIcon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsListItem(
    leadingIcon: ImageVector,
    title: String,
    trailingIcon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingText: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = if (textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText.isNotEmpty()) {
                Text(trailingText, style = MaterialTheme.typography.labelSmall, color = textColor)
                Spacer(Modifier.width(8.dp))
            }
            trailingIcon?.let {
                Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingsListItemWithToggle(
    leadingIcon: ImageVector,
    title: String,
    subtitle: String,
    enabledState: androidx.compose.runtime.MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = enabledState.value,
            onCheckedChange = { enabledState.value = it },
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreenPlaceholder(
    currentRoute: String,
    onBackToSchedule: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) },
                navigationIcon = {
                    IconButton(onClick = onBackToSchedule) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Last Night", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("7h 45m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp))
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Glow effect", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("94% of daily target reached", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("This Week", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatItem(label = "Avg Sleep", value = "7h 23m")
                        StatItem(label = "Discipline", value = "89%")
                        StatItem(label = "Weekends On Track", value = "5/7 days")
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("This Month", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatItem(label = "Avg Sleep", value = "7h 12m")
                        StatItem(label = "Discipline", value = "85%")
                        StatItem(label = "Total Hours", value = "224h")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}
