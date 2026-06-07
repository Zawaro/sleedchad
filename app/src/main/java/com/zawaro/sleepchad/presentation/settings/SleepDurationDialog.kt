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
import androidx.compose.foundation.layout.*
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepDurationDialogContent(
    initialHours: Int,
    initialMinutes: Int,
    onSave: (hours: Int, minutes: Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours.coerceIn(4, 12)) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes.coerceIn(0, 59)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Target Sleep Duration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text("Hours", style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text("Minutes", style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                onClick = { onSave(selectedHours, selectedMinutes) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Set Duration")
            }
        }
    }
}
