package com.zawaro.sleepchad.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrandsDurationDialogContent(
    initialHours: Int,
    initialMinutes: Int,
    onSave: (hours: Int, minutes: Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours.coerceIn(0, 2)) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes.coerceIn(0, 59)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Errands Buffer Duration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Picker
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hours", style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(onClick = { selectedHours = (selectedHours - 1).coerceAtLeast(0) }) {
                        Text("-")
                    }
                    Text(
                        text = String.format("%d", selectedHours),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalButton(onClick = { selectedHours = (selectedHours + 1).coerceAtMost(2) }) {
                        Text("+")
                    }
                }
            }

            // Minute Picker
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Minutes", style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(onClick = { selectedMinutes = (selectedMinutes - 5).coerceAtLeast(0) }) {
                        Text("-")
                    }
                    Text(
                        text = String.format("%02d", selectedMinutes),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalButton(onClick = { selectedMinutes = (selectedMinutes + 5).coerceAtMost(59) }) {
                        Text("+")
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = { /* Handled by ModalBottomSheet dismiss */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    onSave(selectedHours, selectedMinutes)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Set Buffer")
            }
        }
    }
}
