package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zawaro.sleepchad.presentation.statistics.StatisticsUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onClose: () -> Unit = {},
    statisticsUiModel: StatisticsUiModel = StatisticsUiModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Statistics", color = MaterialTheme.colorScheme.primary) 
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Last Night", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        statisticsUiModel.lastNightSleepMinutes?.let { minutes ->
                            val hours = minutes / 60
                            val mins = minutes % 60
                            Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp))
                        } ?: run {
                            Text("No data yet", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp))
                        }
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Glow effect", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Track your sleep consistency", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("This Week", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        statisticsUiModel.weekAvgSleepHours?.let { hours ->
                            StatItem(label = "Avg Sleep", value = String.format("%.1fh", hours))
                        } ?: run {
                            StatItem(label = "Avg Sleep", value = "No data")
                        }
                        StatItem(label = "Discipline", value = statisticsUiModel.weekDisciplinePercent?.let { "$it%" } ?: "--%")
                        StatItem(label = "Weekends On Track", value = "5/7 days")
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text("This Month", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        statisticsUiModel.monthAvgSleepHours?.let { hours ->
                            StatItem(label = "Avg Sleep", value = String.format("%.1fh", hours))
                        } ?: run {
                            StatItem(label = "Avg Sleep", value = "No data")
                        }
                        StatItem(label = "Discipline", value = statisticsUiModel.monthTotalHours?.let { "$it hrs tracked" } ?: "--%")
                        statisticsUiModel.monthTotalHours?.let { hours ->
                            StatItem(label = "Total Hours", value = String.format("%.1fh", hours))
                        } ?: run {
                            StatItem(label = "Total Hours", value = "0h")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}
