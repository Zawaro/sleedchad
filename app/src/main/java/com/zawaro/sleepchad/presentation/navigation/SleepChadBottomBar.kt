package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SleepChadBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepChadNavItem(
                selected = currentRoute == SleepChadRoute.Schedule.route,
                onClick = { onNavigate(SleepChadRoute.Schedule.route) },
                icon = Icons.Default.Alarm,
                label = "SCHEDULE"
            )

            SleepChadNavItem(
                selected = currentRoute == SleepChadRoute.Statistics.route,
                onClick = { onNavigate(SleepChadRoute.Statistics.route) },
                icon = Icons.Default.Assessment,
                label = "STATISTICS"
            )

            SleepChadNavItem(
                selected = currentRoute == SleepChadRoute.Settings.route,
                onClick = { onNavigate(SleepChadRoute.Settings.route) },
                icon = Icons.Default.Settings,
                label = "SETTINGS"
            )
        }
    }
}

@Composable
private fun SleepChadNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    val primaryGreen = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (selected) primaryGreen.copy(alpha = 0.12f) else Color.Transparent)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) primaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            color = if (selected) primaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )

        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(primaryGreen.copy(alpha = 0.6f), shape = MaterialTheme.shapes.small)
            )
        }
    }
}
