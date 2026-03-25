package com.zawaro.sleepchad.presentation.schedule

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.core.TimeFormatter

@Composable
fun ScheduleContent(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    customAlarms: List<CustomAlarmUiModel>,
    preferencesViewModel: com.zawaro.sleepchad.presentation.settings.PreferencesViewModel,
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
        Text("Global Optimization", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TargetSleepCard(preferences, showSleepDurationDialog, { showSleepDurationDialog = it }, preferencesViewModel)
            WakeUpTimeCard(context, preferences, showWakeUpTimeDialog, { showWakeUpTimeDialog = it })
            ErrandsDurationCard(preferences, showErrandsDurationDialog, { showErrandsDurationDialog = it }, preferencesViewModel)
        }
        
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
        }

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

        if (showSleepDurationDialog && preferences.targetSleepDurationMinutes != null) {
            val currentHours = preferences.targetSleepDurationMinutes / 60
            val currentMins = preferences.targetSleepDurationMinutes % 60
            com.zawaro.sleepchad.presentation.settings.SleepDurationDialog(
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
            com.zawaro.sleepchad.presentation.settings.WakeUpTimeDialog(
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
            com.zawaro.sleepchad.presentation.settings.ErrandsDurationDialog(
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

@Composable
private fun TargetSleepCard(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    showDialog: Boolean,
    onShowDialogChanged: (Boolean) -> Unit,
    preferencesViewModel: com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                IconButton(onClick = { onShowDialogChanged(true) }) {
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
}

@Composable
private fun WakeUpTimeCard(
    context: Context,
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    showDialog: Boolean,
    onShowDialogChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                IconButton(onClick = { onShowDialogChanged(true) }) {
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
}

@Composable
private fun ErrandsDurationCard(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    showDialog: Boolean,
    onShowDialogChanged: (Boolean) -> Unit,
    preferencesViewModel: com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                IconButton(onClick = { onShowDialogChanged(true) }) {
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

@Composable
private fun CustomAlarmAccordion(
    alarm: CustomAlarmUiModel,
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    onDelete: () -> Unit
) {
    var isExpanded: Boolean = remember { mutableStateOf(false).value }
    
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
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
                    
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    
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

private fun getAlarmTime(alarm: CustomAlarmUiModel): String {
    val wakeMs = alarm.wakeupMs ?: return "06:15"
    val hours = (wakeMs / 3600000) % 24
    val minutes = (wakeMs % 3600000) / 60000
    return String.format("%02d:%02d", hours, minutes)
}
