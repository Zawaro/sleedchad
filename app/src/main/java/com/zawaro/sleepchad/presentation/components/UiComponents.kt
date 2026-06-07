package com.zawaro.sleepchad.presentation.components

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.presentation.schedule.CustomAlarmUiModel

@Composable
fun PreferenceCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.clickable(onClick = onClick).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                icon()
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun GlobalPreferencesHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp).padding(top = 24.dp)) {
        Text("Global Optimization", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LastNightBanner(
    lastNightSleep: Int?,
    onRecordWakeUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(horizontal = 16.dp).padding(top = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            
            Column(modifier = Modifier.fillMaxSize()) {
                Text(lastNightSleep?.let { "${it / 60}h ${(it % 60)}m" } ?: "Not recorded", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                lastNightSleep?.let { duration ->
                    val percentage = (duration.toFloat() / 480f * 100).toInt()
                    Text("$percentage% of your daily discipline target reached.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(Modifier.height(4.dp))
                    
                    OutlinedButton(onClick = onRecordWakeUp, modifier = Modifier.fillMaxWidth()) {
                        Text("I Woke Up")
                    }
                } ?: run {
                    Button(onClick = onRecordWakeUp, modifier = Modifier.fillMaxWidth()) {
                        Text("I'm Going to Bed")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomAlarmHeader(
    onClearAll: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Custom Schedules", style = MaterialTheme.typography.headlineSmall)
            Text("Override schedule", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        TextButton(onClick = onClearAll) {
            Text("Clear All")
        }
    }
}

@Composable
fun CustomAlarmAccordionItem(
    alarm: CustomAlarmUiModel,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), shape = RoundedCornerShape(16.dp), modifier = modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SurfaceWithIcon(icon = Icons.Default.Schedule, tint = MaterialTheme.colorScheme.primary, content = {})
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(alarm.name.ifEmpty { "Custom Alarm" }, style = MaterialTheme.typography.titleLarge)
                        val dayLabels = alarm.enabledDays.sorted().map { getDayLabel(it) }
                        Text("Days: ${dayLabels.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(onClick = onToggle) {
                        Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = if (isExpanded) "Collapse" else "Expand")
                    }
                }
            }
        }
    }
}

@Composable
fun SurfaceWithIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    content: @Composable () -> Unit
) {
    Surface(modifier = modifier.size(48.dp), shape = shape, color = backgroundColor, tonalElevation = 2.dp) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint)
            
            if (icon == Icons.Default.Schedule && tint != Color.Transparent) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerRadiusPx = 12f * this.density
                    drawRoundRect(color = tint.copy(alpha = 0.3f), size = androidx.compose.ui.geometry.Size(size.width, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx))
                }
            }
            
            content()
        }
    }
}

@Composable
fun IconButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(onClick = onClick, shape = RoundedCornerShape(8.dp), color = Color.Transparent) {
        Box(modifier = modifier.padding(4.dp)) {
            content()
        }
    }
}

@Composable
fun TextButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(onClick = onClick, shape = RoundedCornerShape(8.dp), color = Color.Transparent) {
        Box(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun AddAlarmFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp)) {
        Icon(Icons.Default.Add, contentDescription = "Add Custom Alarm")
    }
}

private fun getDayLabel(dayNum: Int): String = when (dayNum) {
    1 -> "Mon"
    2 -> "Tue"
    3 -> "Wed"
    4 -> "Thu"
    5 -> "Fri"
    6 -> "Sat"
    7 -> "Sun"
    else -> ""
}
