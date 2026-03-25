package com.zawaro.sleepchad.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun WakeUpTimeDialog(
    initialEpochMs: Long,
    onDismissRequest: () -> Unit,
    onSave: (epochMs: Long) -> Unit
) {
    val calendar = Calendar.getInstance(Locale.US).apply {
        timeInMillis = initialEpochMs
    }

    var selectedHourState = remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinuteState = remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Wake-up Time") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Hour")
                        Text(
                            text = "${selectedHourState.value}",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Spacer(modifier = Modifier.width(1.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Minute")
                        Text(
                            text = "${selectedMinuteState.value}",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        selectedHourState.value = if (selectedHourState.value <= 0) 23 else selectedHourState.value - 1
                    }) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        selectedHourState.value = if (selectedHourState.value >= 23) 0 else selectedHourState.value + 1
                    }) {
                        Text("+")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        selectedMinuteState.value = if (selectedMinuteState.value <= 5) 59 else selectedMinuteState.value - 5
                    }) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        selectedMinuteState.value = (selectedMinuteState.value + 5) % 60
                    }) {
                        Text("+")
                    }
                }
            }
        },
        confirmButton = {
            val newDate = Calendar.getInstance(Locale.US).apply {
                timeInMillis = initialEpochMs
                set(Calendar.HOUR_OF_DAY, selectedHourState.value)
                set(Calendar.MINUTE, selectedMinuteState.value)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            TextButton(onClick = {
                onSave(newDate.timeInMillis)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
