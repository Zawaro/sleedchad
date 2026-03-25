package com.zawaro.sleepchad.presentation.schedule

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.core.TimeFormatter

@Composable
fun AddCustomAlarmDialog(
    preferences: com.zawaro.sleepchad.presentation.settings.UserPreferencesUiModel,
    existingAlarms: List<CustomAlarmUiModel>,
    onDismiss: () -> Unit,
    onSave: (String, Set<Int>, Int, Int, Long?) -> Unit
) {
    val nameState = remember { mutableStateOf("") }
    val selectedDaysState = remember { mutableStateOf(setOf<Int>()) }
    
    val prefSleepDuration = preferences.targetSleepDurationMinutes ?: 480
    val targetSleepHoursState = remember { mutableIntStateOf(prefSleepDuration / 60) }
    val targetSleepMinutesState = remember { mutableIntStateOf(prefSleepDuration % 60) }
    
    val wakeUpTimeMs = preferences.wakeUpTimeMs
    
    val usedDays = remember(existingAlarms) {
        existingAlarms.flatMap { it.enabledDays }.toSet()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Column { 
            Text("Add Custom Schedule", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary) 
            Spacer(modifier = Modifier.height(8.dp)) 
            Text("Configure your custom alarm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) 
        } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = nameState.value,
                    onValueChange = { newName: String -> nameState.value = newName },
                    label = { Text("Name (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                CircularDayPicker(
                    selectedDays = selectedDaysState.value, 
                    onDayToggle = { day ->
                        if (day in usedDays && day !in selectedDaysState.value) return@CircularDayPicker
                        selectedDaysState.value = if (day in selectedDaysState.value) selectedDaysState.value - day else selectedDaysState.value + day
                    }
                )
                
                var showHourPicker: MutableState<Boolean> = remember { mutableStateOf(false) }
                var showMinutePicker: MutableState<Boolean> = remember { mutableStateOf(false) }
                
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
                                    onClick = { showHourPicker.value = true },
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "%02d".format(targetSleepHoursState.value),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            Text(":", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                            
                            Box(modifier = Modifier.width(60.dp)) {
                                Surface(
                                    onClick = { showMinutePicker.value = true },
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "%02d".format(targetSleepMinutesState.value),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                if (showHourPicker.value) {
                    TimeSelectionDialog(
                        items = 12,
                        selectedItem = targetSleepHoursState.value,
                        onItemSelected = { hour -> 
                            targetSleepHoursState.value = hour
                            showHourPicker.value = false 
                        },
                        formatItem = { "%02d".format(it) }
                    )
                }
                
                if (showMinutePicker.value) {
                    TimeSelectionDialog(
                        items = 12,
                        selectedItem = targetSleepMinutesState.value,
                        onItemSelected = { minute -> 
                            targetSleepMinutesState.value = minute * 5
                            showMinutePicker.value = false 
                        },
                        formatItem = { "%02d".format(it * 5) }
                    )
                }
                
                val calculatedBedtimeMs = remember(targetSleepHoursState.value, targetSleepMinutesState.value, wakeUpTimeMs) {
                    if (wakeUpTimeMs != null && (targetSleepHoursState.value * 60 + targetSleepMinutesState.value) < 1440) {
                        val wakeCal = java.util.Calendar.getInstance().apply { timeInMillis = wakeUpTimeMs }
                        val totalWakeMinutes = (wakeCal.get(java.util.Calendar.HOUR_OF_DAY) * 60) + wakeCal.get(java.util.Calendar.MINUTE)
                        var totalBedMinutes = totalWakeMinutes - (targetSleepHoursState.value * 60 + targetSleepMinutesState.value)
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
                    if (selectedDaysState.value.isNotEmpty()) {
                        onSave(nameState.value, selectedDaysState.value, targetSleepHoursState.value, targetSleepMinutesState.value, wakeUpTimeMs)
                    }
                },
                enabledByDefault = selectedDaysState.value.isNotEmpty(),
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

@Composable
private fun TimeSelectionDialog(
    items: Int,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    formatItem: (Int) -> String
) {
    AlertDialog(
        onDismissRequest = { },
        title = null,
        text = {
            LazyColumn {
                items(items) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            onItemSelected(index) 
                        }.heightIn(max = 36.dp).padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatItem(index),
                            fontSize = 14.sp,
                            fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (selectedItem == index) {
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
