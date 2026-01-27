package com.zawaro.sleepchad.presentation.schedule

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel

private sealed class Screen { object Schedule : Screen(); object Settings : Screen(); object About : Screen() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: SettingsViewModel, onThemeChanged: (Int) -> Unit = {}) {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Schedule) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                is Screen.Schedule -> ScheduleContent()
                is Screen.Settings -> SettingsScreen(viewModel, onClose = { currentScreen = Screen.Schedule }, onThemeChanged = onThemeChanged)
                is Screen.About -> AboutScreen(onClose = { currentScreen = Screen.Schedule })
            }
        }
    }
}

@Composable
private fun MenuActions(onSettingsClicked: () -> Unit, onAboutClicked: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
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
private fun SettingsScreen(viewModel: SettingsViewModel, onClose: () -> Unit, onThemeChanged: (Int) -> Unit) {
    var is24Hour by remember { mutableStateOf(false) }
    val themeIndex = viewModel.themeIndex.intValue
    var selectedThemeIndex by remember { mutableStateOf(themeIndex) }
    val labels = listOf("System default", "Light", "Dark")
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("12/24‑hour format")
            Switch(
        checked = is24Hour,
        onCheckedChange = { is24Hour = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White
        )
    )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Theme")
        var showThemeDialog by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { showThemeDialog = true }
        ) {
            Text(labels[selectedThemeIndex])
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
if (showThemeDialog) {
    AlertDialog(
        onDismissRequest = { showThemeDialog = false },
        title = { Text("Select Theme") },
        text = {
            Column {
                labels.forEachIndexed { idx, label ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable {
                             selectedThemeIndex = idx
                            viewModel.updateThemeIndex(idx)
                        }
                    ) {
                        RadioButton(selected = selectedThemeIndex == idx, onClick = { 
                             selectedThemeIndex = idx
                            viewModel.updateThemeIndex(idx)
                        })
                        Text(label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showThemeDialog = false }) {
                Text("OK")
            }
        }
    )
}
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClose) { Text("Done") }
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
        Icon(Icons.Default.Info, contentDescription = "App icon", modifier = Modifier.size(64.dp))
        Text("Version: $versionName")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply { data = android.net.Uri.parse("https://github.com/yourrepo/sleepchad") }
            context.startActivity(intent)
        }) { Text("Open GitHub Repository") }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClose) { Text("Done") }
    }
}

@Composable
private fun ScheduleContent() {
    val days = remember { createSampleDays().toMutableStateList() }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Target: 7h 0m", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        days.forEachIndexed { index, day ->
            DayRow(
                day = day,
                onTimeChange = { newWake ->
                    val list = days.toMutableList()
                    list[index] = list[index].copy(wakeup = newWake)
                    days.clear(); days.addAll(list)
                },
                onToggleChanged = { enabled ->
                    val list = days.toMutableList()
                    list[index] = list[index].copy(enabled = enabled)
                    days.clear(); days.addAll(list)
                }
            )
        }
    }
}

@Composable
private fun DayRow(day: DaySchedule, onTimeChange: (Long) -> Unit, onToggleChanged: (Boolean) -> Unit) {
    val context = LocalContext.current
    val wakeText = android.text.format.DateFormat.getTimeFormat(context).format(java.util.Date(day.wakeup))
    val bedText = android.text.format.DateFormat.getTimeFormat(context).format(java.util.Date(day.bedtime))
    val prepAlarmMillis = day.bedtime + day.prepMinutes * 60L * 1000
    val prepText = android.text.format.DateFormat.getTimeFormat(context).format(java.util.Date(prepAlarmMillis))
    Column(
        modifier = Modifier.fillMaxWidth().clickable { showWakeTimePicker(context, day.wakeup) { onTimeChange(it) } }.padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(day.dayLabel + " - "+wakeText, fontWeight = FontWeight.Bold, color = if (day.enabled) MaterialTheme.colorScheme.primary else Color.Gray)
Switch(
        checked = day.enabled,
        onCheckedChange = { enabled -> onToggleChanged(enabled) },
        colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
    )
        }
        Text("Prep alarm: $prepText", fontSize = 14.sp, color = if (day.enabled) Color.Unspecified else Color.Gray)
        Text("Bedtime alarm: $bedText", fontSize = 14.sp, color = if (day.enabled) Color.Unspecified else Color.Gray)
    }
}

private fun showWakeTimePicker(context: Context, initialMillis: Long, onSet: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialMillis }
    val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) calendar[Calendar.HOUR_OF_DAY] else calendar[Calendar.HOUR]
    val minute = calendar[Calendar.MINUTE]
    TimePickerDialog(
        context,
        { _, h, m ->
            val newCal = Calendar.getInstance().apply {
                timeInMillis = initialMillis
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

@Immutable
data class DaySchedule(
    val dayLabel: String,
    val wakeup: Long,
    val bedtime: Long,
    val prepMinutes: Int,
    val enabled: Boolean
)

private fun createSampleDays(): List<DaySchedule> {
    val now = System.currentTimeMillis()
    return (0 until 7).mapIndexed { i, _ ->
        DaySchedule(
            dayLabel = when (i) {
                0 -> "Mon"
                1 -> "Tue"
                2 -> "Wed"
                3 -> "Thu"
                4 -> "Fri"
                5 -> "Sat"
                else -> "Sun"
            },
            wakeup = now + (7 + i * 30) * 60L * 1000,
            bedtime = now + 22 * 60L * 60 * 1000,
            prepMinutes = 30,
            enabled = true
        )
    }
}
