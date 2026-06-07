package com.zawaro.sleepchad.presentation.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpTimeDialogContent(
    initialEpochMs: Long,
    onSave: (epochMs: Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance(Locale.US).apply { timeInMillis = initialEpochMs } }

    var selectedHourState by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinuteState by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Wake-up Time",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularTimeDial(
                label = "Hour",
                value = selectedHourState,
                range = 0..23,
                onValueChange = { selectedHourState = it }
            )

            CircularTimeDial(
                label = "Minute",
                value = selectedMinuteState,
                range = 0..59,
                onValueChange = { selectedMinuteState = it }
            )
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
                    val newDate = Calendar.getInstance(Locale.US).apply {
                        timeInMillis = initialEpochMs
                        set(Calendar.HOUR_OF_DAY, selectedHourState)
                        set(Calendar.MINUTE, selectedMinuteState)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onSave(newDate.timeInMillis)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Set Wake Up")
            }
        }
    }
}

@Composable
fun CircularTimeDial(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { 
                    onValueChange((value + 1) % (range.last + 1)) 
                },
            contentAlignment = Alignment.Center
        ) {
            val colors = MaterialTheme.colorScheme
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 10f
                val anglePerUnit = 360f / (range.last - range.first + 1)
                val currentAngle = value * anglePerUnit - 90f

                drawCircle(
                    color = colors.outlineVariant,
                    radius = radius,
                    style = Stroke(width = 4f)
                )

                drawArc(
                    color = colors.primary,
                    startAngle = -90f,
                    sweepAngle = currentAngle + (anglePerUnit / 2), // Simplified arc filling
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )

                // Draw a marker for the selected value
                val x = center.x + radius * cos(Math.toRadians(currentAngle.toDouble())).toFloat()
                val y = center.y + radius * sin(Math.toRadians(currentAngle.toDouble())).toFloat()
                drawCircle(
                    color = colors.primary,
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

