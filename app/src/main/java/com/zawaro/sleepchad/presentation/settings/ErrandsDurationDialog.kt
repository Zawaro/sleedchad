package com.zawaro.sleepchad.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrandsDurationDialog(
    initialHours: Int,
    initialMinutes: Int,
    onDismissRequest: () -> Unit,
    onSave: (hours: Int, minutes: Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours.coerceIn(0, 2)) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes.coerceIn(0, 59)) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Evening Errands Duration") },
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
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    VerticalDivider(modifier = Modifier.width(1.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Minutes")
                        Text(
                            text = selectedMinutes.toString(),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        selectedHours = (selectedHours - 1).takeIf { it >= 0 } ?: 0
                    }) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        selectedHours = (selectedHours + 1).coerceIn(0, 2)
                    }) {
                        Text("+")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        selectedMinutes = (selectedMinutes - 5).takeIf { it >= 0 } ?: 59
                    }) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        selectedMinutes = (selectedMinutes + 5).takeIf { it < 60 } ?: 0
                    }) {
                        Text("+")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(selectedHours, selectedMinutes)
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
