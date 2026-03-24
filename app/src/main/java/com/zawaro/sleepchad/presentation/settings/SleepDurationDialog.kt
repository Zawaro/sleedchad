package com.zawaro.sleepchad.presentation.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Canvas

@Composable
fun SleepDurationDialog(
    initialHours: Int,
    initialMinutes: Int,
    onDismissRequest: () -> Unit,
    onSave: (hours: Int, minutes: Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours.coerceIn(4, 12)) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes.coerceIn(0, 59)) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Target Sleep Duration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Hours")
                        Text(
                            text = selectedHours.toString(),
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedHours = (selectedHours - 1).coerceIn(4, 12) }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Decrease")
                            }

                            Text(
                                text = selectedHours.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(onClick = { selectedHours = (selectedHours + 1).coerceIn(4, 12) }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Increase")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(1.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Minutes")
                        Text(
                            text = selectedMinutes.toString(),
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedMinutes = (selectedMinutes - 5).takeIf { it >= 0 } ?: 59 }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Decrease")
                            }

                            Text(
                                text = selectedMinutes.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(onClick = { selectedMinutes = (selectedMinutes + 5).takeIf { it < 60 } ?: 0 }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    onSave(selectedHours, selectedMinutes)
                }) {
                    Text("Save")
                }
            }
        },
    )
}
