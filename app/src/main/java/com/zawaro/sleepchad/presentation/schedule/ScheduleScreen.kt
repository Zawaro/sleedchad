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
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
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
import com.zawaro.sleepchad.presentation.settings.ErrandsDurationDialog
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsScreen
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.presentation.settings.SetupDialog
import com.zawaro.sleepchad.presentation.settings.SleepDurationDialog
import com.zawaro.sleepchad.presentation.settings.WakeUpTimeDialog
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class Screen { 
    object Schedule : Screen()
    object Settings : Screen()
    object About : Screen()
}

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    settingsViewModel: SettingsViewModel,
    onThemeChanged: (Int) -> Unit = {}
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
    
    val context = LocalContext.current
    
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
                                val wakeTime = alarm.wakeupMs ?: (7 * 60 * 60 * 1000L) // default 7AM
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
                    onDismissRequest = { /* Don't allow dismissing setup */ },
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

data class CustomAlarmUiModel(
    val id: Long = 0L,
    val name: String,
    val enabledDays: Set<Int>,
    val targetSleepDurationMinutes: Int?,
    val wakeupMs: Long? = null,
)

@Composable
private fun MenuActions(onSettingsClicked: () -> Unit, onAboutClicked: () -> Unit) {
    var expanded: Boolean = remember { mutableStateOf(false).value }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { expanded = false; onSettingsClicked() })
        DropdownMenuItem(
            text = { Text("About") },
            onClick = { expanded = false; onAboutClicked() })
    }
}

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit,
    enabledByDefault: Boolean = true
) {
    val gradientBrush = Brush.linearGradient(colors = colors)
    
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer { alpha = 1f }.padding(vertical = 4.dp),
        enabled = enabledByDefault,
        colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent),
        shape = RoundedCornerShape(50.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush, RoundedCornerShape(50.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

@Composable
fun GradientOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    val gradientBrush = Brush.linearGradient(colors = colors)
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer { alpha = 1f }.padding(vertical = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors().copy(containerColor = Color.Transparent),
        shape = RoundedCornerShape(50.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush, RoundedCornerShape(50.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        }
    }
}


@Composable
private fun AboutScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "1.0"
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("SleepChad", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Icon(Icons.Default.Info, contentDescription = "App icon", modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Version: $versionName")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply { 
                data = android.net.Uri.parse("https://github.com/anomalyco/sleepchad") 
            }
            context.startActivity(intent)
        }) { Text("Open GitHub Repository") }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClose) { Text("Done") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomAlarmDialog(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    existingAlarms: List<CustomAlarmUiModel>,
    onDismiss: () -> Unit,
    onSave: (String, Set<Int>, Int, Int, Long?) -> Unit
) {
    var name: String = remember { mutableStateOf("").value }
    var selectedDays: Set<Int> = remember { mutableStateOf(setOf<Int>()).value }
    
    val prefSleepDuration = preferences.targetSleepDurationMinutes ?: 480 // default 8 hours
    var targetSleepHours: Int = remember { mutableIntStateOf(prefSleepDuration / 60).value }
    var targetSleepMinutes: Int = remember { mutableIntStateOf(prefSleepDuration % 60).value }
    
    val wakeUpTimeMs = preferences.wakeUpTimeMs
    
    val usedDays = remember(existingAlarms) {
        existingAlarms.flatMap { it.enabledDays }.toSet()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Column { Text("Add Custom Schedule", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary); Spacer(modifier = Modifier.height(8.dp)); Text("Configure your custom alarm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { newName: String -> name = newName },
                    label = { Text("Name (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
CircularDayPicker(
                    selectedDays = remember { mutableStateOf(selectedDays).value }, 
                    onDayToggle = { day ->
                        if (day in usedDays && day !in selectedDays) return@CircularDayPicker
                        selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                    }
                )
                
                var showHourPicker: Boolean = remember { mutableStateOf(false).value }
                var showMinutePicker: Boolean = remember { mutableStateOf(false).value }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Target Sleep Duration", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Box(modifier = Modifier.width(60.dp)) {
                                Surface(
                                    onClick = { showHourPicker = true },
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = targetSleepHours.toString().padStart(2, '0'),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            Text(":", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                            
                            Box(modifier = Modifier.width(60.dp)) {
                                Surface(
                                    onClick = { showMinutePicker = true },
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = targetSleepMinutes.toString().padStart(2, '0'),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                if (showHourPicker) {
                    AlertDialog(
                        onDismissRequest = { showHourPicker = false },
                        title = null,
                        text = {
                            LazyColumn {
                                items(12) { hour ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { 
                                            targetSleepHours = hour 
                                            showHourPicker = false 
                                        }.heightIn(max = 36.dp).padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = hour.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = if (targetSleepHours == hour) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        if (targetSleepHours == hour) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }
                
                if (showMinutePicker) {
                    AlertDialog(
                        onDismissRequest = { showMinutePicker = false },
                        title = null,
                        text = {
                            LazyColumn {
                                items(12) { minute ->
                                    val mins = minute * 5
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { 
                                            targetSleepMinutes = mins 
                                            showMinutePicker = false 
                                        }.heightIn(max = 36.dp).padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mins.toString().padStart(2, '0'),
                                            fontSize = 14.sp,
                                            fontWeight = if (targetSleepMinutes == mins) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        if (targetSleepMinutes == mins) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }
                
                val calculatedBedtimeMs = remember(targetSleepHours, targetSleepMinutes, wakeUpTimeMs) {
                    if (wakeUpTimeMs != null && (targetSleepHours * 60 + targetSleepMinutes) < 1440) {
                        val wakeCal = java.util.Calendar.getInstance().apply { timeInMillis = wakeUpTimeMs }
                        val totalWakeMinutes = (wakeCal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + wakeCal.get(java.util.Calendar.MINUTE)
                        var totalBedMinutes = totalWakeMinutes - (targetSleepHours * 60 + targetSleepMinutes)
                        if (totalBedMinutes < 0) totalBedMinutes += 1440
                        (totalBedMinutes / 60) * 3600000L + ((totalBedMinutes % 60) * 60000L)
                    } else null
                }
                
                calculatedBedtimeMs?.let { ms ->
                    val bedText = TimeFormatter.formatTime(LocalContext.current, ms, null)
                    Card(
                        modifier = Modifier.fillMaxWidth().graphicsLayer {
                            alpha = 0.9f
                            shape = RoundedCornerShape(12.dp)
                            clip = true
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Text("Calculated Bedtime", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(bedText, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Glow effect", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                
                wakeUpTimeMs?.let { ms ->
                    val wakeText = TimeFormatter.formatTime(LocalContext.current, ms, null)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Wake-up time (from preferences)", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AlarmOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(wakeText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            GradientButton(
                onClick = { 
                    if (selectedDays.isNotEmpty()) {
                        onSave(name, selectedDays, targetSleepHours, targetSleepMinutes, wakeUpTimeMs)
                    }
                },
                enabledByDefault = selectedDays.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                content = { Text("Save Routine") }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getDayLabel(dayNum: Int): String {
    return when (dayNum) {
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        7 -> "Sun"
        else -> ""
    }
}

@Composable
private fun ScheduleContent(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    customAlarms: List<CustomAlarmUiModel>,
    preferencesViewModel: PreferencesViewModel,
    onDeleteAlarm: (Long) -> Unit
) {
    val context = LocalContext.current
    
    var showSleepDurationDialog: Boolean = remember { mutableStateOf(false).value }
    var showWakeUpTimeDialog: Boolean = remember { mutableStateOf(false).value }
    var showErrandsDurationDialog: Boolean = remember { mutableStateOf(false).value }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Global Optimization Section Header
        Text("Global Optimization", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        // 3-Card Grid for Preferences (Responsive: 1 col mobile, wraps on larger screens)
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    // Target Sleep Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                IconButton(onClick = { showSleepDurationDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Target Sleep", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            preferences.targetSleepDurationMinutes?.let { duration ->
                val hours = duration / 60
                val mins = duration % 60
                Spacer(modifier = Modifier.height(6.dp))
                Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp))
            } ?: run {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap to set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    
    // Wake Up Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                IconButton(onClick = { showWakeUpTimeDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Wake Up", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            preferences.wakeUpTimeMs?.let { ms ->
                val wakeText = TimeFormatter.formatTime(context, ms, preferences.timeFormatPreference?.toBoolean())
                Spacer(modifier = Modifier.height(6.dp))
                Text(wakeText, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp))
            } ?: run {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap to set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    
    // Errands Buffer Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                IconButton(onClick = { showErrandsDurationDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Errands Buffer", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            preferences.errandsDurationMinutes?.let { duration ->
                val hours = duration / 60
                val mins = duration % 60
                Spacer(modifier = Modifier.height(6.dp))
                Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp))
            } ?: run {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap to set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
        
        // Last Night's Sleep Banner or Action Buttons
        preferences.lastNightEstimatedSleepMinutes?.let { duration ->
            val hours = duration / 60
            val mins = duration % 60
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Last night: ${hours}h ${mins}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            val targetSleepDurationMinutes = preferencesViewModel.preferences.value.targetSleepDurationMinutes ?: 480
                            val percentage = ((duration.toFloat() / targetSleepDurationMinutes) * 100).toInt().coerceIn(0, 100)
                            Text("$percentage% of your daily discipline target reached.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientButton(
                    onClick = { preferencesViewModel.recordBedtime() },
                    modifier = Modifier.weight(1f),
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer),
                    content = { 
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Bedtime, contentDescription = null)
                            Text("I'M GOING TO BED", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp))
                        }
                    }
                )
                
                GradientOutlinedButton(
                    onClick = { preferencesViewModel.recordWakeUp() },
                    modifier = Modifier.weight(1f),
                    colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    content = { 
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AlarmOn, contentDescription = null)
                            Text("I WOKE UP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp))
                        }
                    }
                )
            }
        } ?: run {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientButton(
                    onClick = { preferencesViewModel.recordBedtime() },
                    modifier = Modifier.weight(1f),
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondaryContainer),
                    content = { 
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Bedtime, contentDescription = null)
                            Text("I'M GOING TO BED", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp))
                        }
                    }
                )
                
                GradientOutlinedButton(
                    onClick = { preferencesViewModel.recordWakeUp() },
                    modifier = Modifier.weight(1f),
                    colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    content = { 
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AlarmOn, contentDescription = null)
                            Text("I WOKE UP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.15.sp))
                        }
                    }
                )
            }

        // Custom Alarms Section Header
        if (customAlarms.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Custom Schedules", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                    Text("Override schedule", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    customAlarms.forEach { alarm ->
                        CustomAlarmAccordion(
                            alarm = alarm,
                            preferences = preferences,
                            onDelete = { onDeleteAlarm(alarm.id) }
                        )
                    }
                }
            }
        }

        // Preference Edit Dialogs
        if (showSleepDurationDialog && preferences.targetSleepDurationMinutes != null) {
            val currentHours = preferences.targetSleepDurationMinutes / 60
            val currentMins = preferences.targetSleepDurationMinutes % 60
            SleepDurationDialog(
                initialHours = currentHours,
                initialMinutes = currentMins,
                onDismissRequest = { showSleepDurationDialog = false },
                onSave = { hours, minutes ->
                    preferencesViewModel.updateTargetSleepDuration((hours * 60) + minutes)
                    showSleepDurationDialog = false
                }
            )
        }

        if (showWakeUpTimeDialog && preferences.wakeUpTimeMs != null) {
            WakeUpTimeDialog(
                initialEpochMs = preferences.wakeUpTimeMs,
                onDismissRequest = { showWakeUpTimeDialog = false },
                onSave = { epochMs ->
                    preferencesViewModel.updateWakeUpTime(epochMs)
                    showWakeUpTimeDialog = false
                }
            )
        }

        if (showErrandsDurationDialog && preferences.errandsDurationMinutes != null) {
            val currentH = preferences.errandsDurationMinutes / 60
            val currentM = preferences.errandsDurationMinutes % 60
            ErrandsDurationDialog(
                initialHours = currentH,
                initialMinutes = currentM,
                onDismissRequest = { showErrandsDurationDialog = false },
                onSave = { hours, minutes ->
                    preferencesViewModel.updateErrandsDuration((hours * 60) + minutes)
                    showErrandsDurationDialog = false
                }
)
            }
        }

        // Custom Alarms Section Header
        if (customAlarms.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Custom Schedules", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                    Text("Override schedule", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    customAlarms.forEach { alarm ->
                        CustomAlarmAccordion(
                            alarm = alarm,
                            preferences = preferences,
                            onDelete = { onDeleteAlarm(alarm.id) }
                        )
                    }
                }
            }
        }

        // Preference Edit Dialogs
        if (showSleepDurationDialog && preferences.targetSleepDurationMinutes != null) {
            val currentHours = preferences.targetSleepDurationMinutes / 60
            val currentMins = preferences.targetSleepDurationMinutes % 60
            SleepDurationDialog(
                initialHours = currentHours,
                initialMinutes = currentMins,
                onDismissRequest = { showSleepDurationDialog = false },
                onSave = { hours, minutes ->
                    preferencesViewModel.updateTargetSleepDuration((hours * 60) + minutes)
                    showSleepDurationDialog = false
                }
            )
        }

        if (showWakeUpTimeDialog && preferences.wakeUpTimeMs != null) {
            WakeUpTimeDialog(
                initialEpochMs = preferences.wakeUpTimeMs,
                onDismissRequest = { showWakeUpTimeDialog = false },
                onSave = { epochMs ->
                    preferencesViewModel.updateWakeUpTime(epochMs)
                    showWakeUpTimeDialog = false
                }
            )
        }

        if (showErrandsDurationDialog && preferences.errandsDurationMinutes != null) {
            val currentH = preferences.errandsDurationMinutes / 60
            val currentM = preferences.errandsDurationMinutes % 60
            ErrandsDurationDialog(
                initialHours = currentH,
                initialMinutes = currentM,
                onDismissRequest = { showErrandsDurationDialog = false },
                onSave = { hours, minutes ->
                    preferencesViewModel.updateErrandsDuration((hours * 60) + minutes)
                    showErrandsDurationDialog = false
                }
            )
        }
    }
}

private fun showTimePickerDialog(context: Context, isBedtime: Boolean, onSet: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, if (isBedtime) 22 else 7)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) calendar[Calendar.HOUR_OF_DAY] else calendar[Calendar.HOUR]
    val minute = calendar[Calendar.MINUTE]
    TimePickerDialog(
        context,
        { _, h, m ->
            val newCal = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onSet(newCal.timeInMillis)
        },
        hour,
        minute,
        android.text.format.DateFormat.is24HourFormat(context)
    ).show()
}

@Composable
private fun CustomAlarmAccordion(
    alarm: CustomAlarmUiModel,
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    onDelete: () -> Unit
) {
    var isExpanded: Boolean = remember { mutableStateOf(false).value }
    
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        // Header row (always visible)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(alarm.name.ifEmpty { "Custom Alarm" }, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        val daysLabel = alarm.enabledDays.sorted().joinToString(", ") { getDayLabel(it) }
                        val label = if (alarm.id == 0L) "${daysLabel}" else "${getAlarmTime(alarm)} • ${daysLabel.take(6)}"
                        Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Toggle switch for enabled/disabled state
                    Card(
                        modifier = Modifier.size(48.dp),
                        colors = if (isExpanded) 
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) 
                        else 
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.Check else Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = if (isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Chevron icon for expand/collapse
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Expanded content (shown when expanded)
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Divider line
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Clock-style Day Picker - circular day pills
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        (1..7).forEach { dayNum ->
                            val isSelected = dayNum in alarm.enabledDays
                            val dayLabel = getDayLabel(dayNum)
                            
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(50.dp),
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                                    else 
                                        Color.Transparent,
                                    border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(
                                        text = dayLabel,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Snooze and Vibrate options - 2-column grid
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Snooze: 5m", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), textAlign = TextAlign.Center)
                        }
                        
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Vibrate only", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

private fun getAlarmTime(alarm: CustomAlarmUiModel): String {
    val wakeMs = alarm.wakeupMs ?: return "06:15"
    val hours = (wakeMs / 3600000) % 24
    val minutes = (wakeMs % 3600000) / 60000
    return String.format("%02d:%02d", hours, minutes)
}

@Composable
private fun ClockDayPicker(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dayLabels.forEachIndexed { index, label ->
            val dayNum = index + 1
            val isSelected = dayNum in selectedDays
            
            Surface(
                modifier = Modifier.clickable { onDayToggle(dayNum) },
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                },
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Text(
                    text = label,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeDualSpinner(
    initialHours: Int,
    initialMinutes: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    var selectedHour: Int = remember { mutableStateOf(initialHours).value }
    var selectedMinute: Int = remember { mutableStateOf(initialMinutes).value }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Hours picker
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Hours", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Box(modifier = Modifier.width(100.dp)) {
                AlertDialog(
                    onDismissRequest = { },
                    title = null,
                    text = {
                        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                            items(24) { hour ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { selectedHour = hour }.heightIn(max = 36.dp).padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
Text(
                                            text = hour.toString().padStart(2, '0'),
                                            fontSize = 16.sp,
                                            fontWeight = if (selectedHour == hour) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    if (selectedHour == hour) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
        
        // Minutes picker
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Minutes", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Box(modifier = Modifier.width(100.dp)) {
                AlertDialog(
                    onDismissRequest = { },
                    title = null,
                    text = {
                        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                            items(60) { minute ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { selectedMinute = minute }.heightIn(max = 36.dp).padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = minute.toString().padStart(2, '0'),
                                        fontSize = 16.sp,
                                        fontWeight = if (selectedMinute == minute) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (selectedMinute == minute) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
}
