package com.zawaro.sleepchad.presentation.settings

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun SetupDialog(
    onDismissRequest: () -> Unit,
    onSavePreferences: (targetSleepHours: Int, targetSleepMinutes: Int, wakeUpTimeMs: Long, errandsDurationHours: Int, errandsDurationMinutes: Int) -> Unit
) {
    val context = LocalContext.current
    
    var selectedSleepHours by remember { mutableIntStateOf(8) }
    var selectedSleepMinutes by remember { mutableIntStateOf(0) }
    
    var wakeUpHour by remember { mutableIntStateOf(7) }
    var wakeUpMinute by remember { mutableIntStateOf(0) }
    
    var showSleepHoursDialog by remember { mutableStateOf(false) }
    var showSleepMinutesDialog by remember { mutableStateOf(false) }
    var showErrandsDialog by remember { mutableStateOf(false) }
    
    var selectedErrandsTotalMinutes by remember { mutableIntStateOf(30) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Setup SleepChad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Target sleep duration with spinner dialog
                Text("Target Sleep Duration", style = MaterialTheme.typography.titleMedium)
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                    val hoursStr = selectedSleepHours.toString()
                    val minsStr = if (selectedSleepMinutes < 10) "0${selectedSleepMinutes}" else selectedSleepMinutes.toString()
                    Text("$hoursStr h $minsStr m", style = MaterialTheme.typography.headlineLarge)
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Button(onClick = { showSleepHoursDialog = true }, 
                           modifier = Modifier.padding(horizontal = 8.dp)) { 
                        Text("hrs") 
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { showSleepMinutesDialog = true }, 
                           modifier = Modifier.padding(horizontal = 8.dp)) { 
                        Text("mins") 
                    }
                }

                // Wake-up time with regular TimePickerDialog (spinner mode)
                Text("Wake-up Time", style = MaterialTheme.typography.titleMedium)
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                    val wakeText = String.format(Locale.getDefault(), "%02d:%02d", wakeUpHour, wakeUpMinute)
                    Text(wakeText, style = MaterialTheme.typography.headlineLarge, 
                         modifier = Modifier.clickable {
                             TimePickerDialog(context, { _, hourOfDay, minute ->
                                 wakeUpHour = hourOfDay
                                 wakeUpMinute = minute
                             }, wakeUpHour, wakeUpMinute, true).show() // true = spinner mode
                         })
                }

                // Errand duration with spinner dialog (minutes only)
                Text("Errand Duration", style = MaterialTheme.typography.titleMedium)
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                    val minutesText = if (selectedErrandsTotalMinutes < 10) "0${selectedErrandsTotalMinutes}" else selectedErrandsTotalMinutes.toString()
                    Text("$minutesText m", style = MaterialTheme.typography.headlineLarge, 
                         modifier = Modifier.clickable {
                             showErrandsDialog = true
                         })
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Button(onClick = { selectedErrandsTotalMinutes = (selectedErrandsTotalMinutes - 5).coerceAtLeast(0) }, 
                           modifier = Modifier.padding(horizontal = 8.dp)) { 
                        Text("-") 
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { selectedErrandsTotalMinutes = (selectedErrandsTotalMinutes + 5).coerceAtMost(120) }, 
                           modifier = Modifier.padding(horizontal = 8.dp)) { 
                        Text("+") 
                    }
                }
            }
        },
        confirmButton = {
            val errandsDurationHours = selectedErrandsTotalMinutes / 60
            val errandsDurationMins = selectedErrandsTotalMinutes % 60
            
            val wakeUpCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, wakeUpHour)
                set(Calendar.MINUTE, wakeUpMinute)
            }
            
            TextButton(onClick = {
                onSavePreferences(
                    selectedSleepHours, 
                    selectedSleepMinutes, 
                    wakeUpCalendar.timeInMillis,
                    errandsDurationHours,
                    errandsDurationMins
                )
            }) {
                Text("Finish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )

    // Sleep hours spinner dialog (4-12 hours) - custom spinner dialog
    if (showSleepHoursDialog) {
        AlertDialog(
            onDismissRequest = { showSleepHoursDialog = false },
            title = { Text("Select Hours") },
            text = {
                var index by remember { mutableIntStateOf(selectedSleepHours - 4) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Button(onClick = { 
                        if (index > 0) index--
                        selectedSleepHours = index + 4
                    }) { Text("-") }
                    
                    Text("${selectedSleepHours}h", style = MaterialTheme.typography.headlineMedium)
                    
                    Button(onClick = { 
                        if (index < 8) index++
                        selectedSleepHours = index + 4
                    }) { Text("+") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepHoursDialog = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showSleepHoursDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Sleep minutes spinner dialog (5-minute increments) - custom spinner
    if (showSleepMinutesDialog && selectedErrandsTotalMinutes == 30) {
        AlertDialog(
            onDismissRequest = { 
                showSleepHoursDialog = false
                showSleepMinutesDialog = false 
            },
            title = { Text("Select Minutes") },
            text = {
                var index by remember { mutableIntStateOf(selectedSleepMinutes / 5) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Button(onClick = { 
                        if (index > 0) index--
                        selectedSleepMinutes = index * 5
                    }) { Text("-") }
                    
                    Text("${selectedSleepMinutes}m", style = MaterialTheme.typography.headlineMedium)
                    
                    Button(onClick = { 
                        if (index < 12) index++
                        selectedSleepMinutes = index * 5
                    }) { Text("+") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepHoursDialog = false; showSleepMinutesDialog = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showSleepHoursDialog = false; showSleepMinutesDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Errands minutes spinner dialog (5-minute increments, max 120m total) - custom spinner
    if (showErrandsDialog) {
        AlertDialog(
            onDismissRequest = { showErrandsDialog = false },
            title = { Text("Select Minutes") },
            text = {
                var index by remember { mutableIntStateOf(selectedErrandsTotalMinutes / 5) }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Button(onClick = { 
                        val totalMins = index * 5
                        if (totalMins > 0 && index > 0) {
                            index--
                            selectedErrandsTotalMinutes = index * 5
                        }
                    }) { Text("-") }
                    
                    val minutesStr = if ((index * 5) < 10) "0${index * 5}" else "${index * 5}"
                    Text("$minutesStr m", style = MaterialTheme.typography.headlineMedium)
                    
                    Button(onClick = { 
                        val totalMins = (index + 1) * 5
                        if (totalMins <= 120) {
                            index++
                            selectedErrandsTotalMinutes = index * 5
                        }
                    }) { Text("+") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showErrandsDialog = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showErrandsDialog = false }) { Text("Cancel") }
            }
        )
    }
}
