package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SleepChadBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == SleepChadRoute.Schedule.route,
            onClick = { onNavigate(SleepChadRoute.Schedule.route) },
            icon = { 
                Icon(
                    Icons.Default.Alarm, 
                    contentDescription = "Schedule",
                    tint = if (currentRoute == SleepChadRoute.Schedule.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            },
            label = { 
                Text(
                    "SCHEDULE", 
                    fontSize = 9.sp,
                    color = if (currentRoute == SleepChadRoute.Schedule.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        )
        
        NavigationBarItem(
            selected = currentRoute == SleepChadRoute.Statistics.route,
            onClick = { onNavigate(SleepChadRoute.Statistics.route) },
            icon = { 
                Icon(
                    Icons.Default.Leaderboard, 
                    contentDescription = "Statistics",
                    tint = if (currentRoute == SleepChadRoute.Statistics.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            },
            label = { 
                Text(
                    "STATISTICS", 
                    fontSize = 9.sp,
                    color = if (currentRoute == SleepChadRoute.Statistics.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        )
        
        NavigationBarItem(
            selected = currentRoute == SleepChadRoute.Settings.route,
            onClick = { onNavigate(SleepChadRoute.Settings.route) },
            icon = { 
                Icon(
                    Icons.Default.Settings, 
                    contentDescription = "Settings",
                    tint = if (currentRoute == SleepChadRoute.Settings.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            },
            label = { 
                Text(
                    "SETTINGS", 
                    fontSize = 9.sp,
                    color = if (currentRoute == SleepChadRoute.Settings.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        )
    }
}
