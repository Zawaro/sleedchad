package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SleepChadBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        color = Color.White.copy(alpha = 0.05f),
        shadowElevation = 12.dp
    ) {
        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            
            SleepChadNavItem(
                selected = currentRoute == "schedule",
                onClick = { onNavigate("schedule") },
                icon = Icons.Default.Alarm,
                label = "SCHEDULE"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            SleepChadNavItem(
                selected = currentRoute == "statistics",
                onClick = { onNavigate("statistics") },
                icon = Icons.Default.Leaderboard,
                label = "STATISTICS"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            SleepChadNavItem(
                selected = currentRoute == "settings",
                onClick = { onNavigate("settings") },
                icon = Icons.Default.Settings,
                label = "SETTINGS"
            )
            
            Spacer(modifier = Modifier.weight(1f))
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
    Column(
        modifier = Modifier.padding(vertical = 4.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label, 
            fontSize = 9.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
        )
    }
}
