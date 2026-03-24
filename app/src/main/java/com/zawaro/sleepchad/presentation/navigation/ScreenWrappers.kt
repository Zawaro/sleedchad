package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrivacyTip
import com.zawaro.sleepchad.data.CustomAlarmEntity.Companion.toDaysSet
import com.zawaro.sleepchad.presentation.schedule.CustomAlarmUiModel
import com.zawaro.sleepchad.presentation.schedule.ScheduleScreen
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenWrapper(
    viewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit,
    onBottomBarNavigate: (String) -> Unit
) {
    val exceptionAlarms = remember { mutableStateOf<List<com.zawaro.sleepchad.data.CustomAlarmEntity>>(emptyList()) }
    LaunchedEffect(viewModel.exceptionAlarms) {
        viewModel.exceptionAlarms.collect { exceptionAlarms.value = it }
    }

    Scaffold(
        topBar = {
            SleepChadTopAppBar(onSettingsClick = onNavigateToSettings)
        },
        bottomBar = {
            SleepChadBottomBar(
                currentRoute = "schedule",
                onNavigate = onBottomBarNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Show add alarm dialog */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Alarm")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ScheduleContent(
                preferencesViewModel = preferencesViewModel,
                customAlarms = exceptionAlarms.value.map { alarm ->
                    CustomAlarmUiModel(
                        id = alarm.id,
                        name = if (alarm.name.isEmpty()) "Custom" else alarm.name,
                        enabledDays = toDaysSet(alarm.enabledDaysString),
                        targetSleepDurationMinutes = null,
                        wakeupMs = alarm.wakeupMs
                    )
                },
                onDeleteAlarm = { alarmId -> viewModel.deleteExceptionAlarm(alarmId) }
            )
        }
    }
}

@Composable
private fun ScheduleContent(
    preferencesViewModel: PreferencesViewModel,
    customAlarms: List<CustomAlarmUiModel>,
    onDeleteAlarm: (Long) -> Unit
) {
    val preferences = remember { mutableStateOf(com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel()) }
    LaunchedEffect(preferencesViewModel.preferences) {
        preferencesViewModel.preferences.collect { preferences.value = it }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Global Preferences", style = MaterialTheme.typography.titleSmall)
                
                Spacer(Modifier.height(8.dp))
                
                preferences.value.targetSleepDurationMinutes?.let { duration ->
                    val hours = duration / 60
                    val mins = duration % 60
                    Text("Target Sleep: ${hours}h ${mins}m", style = MaterialTheme.typography.bodyLarge)
                }
                
                preferences.value.wakeUpTimeMs?.let { ms ->
                    Text("Wake-up: ${formatTime(ms)}", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }

        if (customAlarms.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Custom Alarms", style = MaterialTheme.typography.titleSmall)
                    
                    Spacer(Modifier.height(8.dp))
                    
                    customAlarms.forEach { alarm ->
                        CustomAlarmItem(alarm, onDeleteAlarm)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomAlarmItem(
    alarm: CustomAlarmUiModel,
    onDelete: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(alarm.name.ifEmpty { "Custom Alarm" }, style = MaterialTheme.typography.bodyLarge)
                Text("Days: ${alarm.enabledDays.sorted().joinToString(", ")}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            
            IconButton(onClick = { onDelete(alarm.id)}) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWrapper(
    preferencesViewModel: PreferencesViewModel,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)) {
                    Text("System Preferences", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                }
            }

            item {
                SettingsSection(title = "Display & Time") {
                    val uiState = preferencesViewModel.preferences.value
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Time Format", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            
                            val timeFormatOptions = listOf("System Default", "12-hour (AM/PM)", "24-hour")
                            val expandedState = mutableStateOf(false)
                            
                            ExposedDropdownMenuBox(
                                expanded = expandedState.value,
                                onExpandedChange = { newState -> expandedState.value = newState }
                            ) {
                                OutlinedTextField(
                                    value = when (uiState.timeFormatPreference?.toBoolean()) {
                                        null -> "System Default"
                                        true -> "24-hour"
                                        false -> "12-hour"
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
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.NotificationsActive,
                                title = "Push Notifications",
                                subtitle = "Wake up alarms and wind-down alerts.",
                                enabledState = mutableStateOf(true)
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Insights,
                                title = "Weekly Sleep Report",
                                subtitle = "Personalized performance analysis.",
                                enabledState = mutableStateOf(false)
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Mail,
                                title = "Newsletter",
                                subtitle = "Tips for better sleep hygiene.",
                                enabledState = mutableStateOf(false)
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
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsListItem(
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
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
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    enabledState: MutableState<Boolean>
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
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreenPlaceholder(onBackToSchedule: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
            verticalArrangement = Arrangement.Center
        ) {
            Text("Statistics", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
            Spacer(Modifier.height(24.dp))
            Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Statistics coming soon", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
