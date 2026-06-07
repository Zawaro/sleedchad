package com.zawaro.sleepchad.presentation.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel

@Composable
fun WelcomeScreen(
    preferencesViewModel: PreferencesViewModel,
    onComplete: () -> Unit,
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var sleepHours by remember { mutableIntStateOf(8) }
    var sleepMinutes by remember { mutableIntStateOf(0) }
    var wakeUpHour by remember { mutableIntStateOf(7) }
    var wakeUpMinute by remember { mutableIntStateOf(0) }
    var errandsMinutes by remember { mutableIntStateOf(30) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                0 -> {
                    Text("Set your target sleep", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TextButton(onClick = { sleepHours = (sleepHours + 1).coerceAtMost(23) }) { Text("+") }
                            Text("${sleepHours}h", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold))
                            TextButton(onClick = { sleepHours = (sleepHours - 1).coerceAtLeast(0) }) { Text("-") }
                        }
                        Spacer(Modifier.width(32.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TextButton(onClick = { sleepMinutes = (sleepMinutes + 5).coerceAtMost(55) }) { Text("+") }
                            Text("${sleepMinutes}m", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold))
                            TextButton(onClick = { sleepMinutes = (sleepMinutes - 5).coerceAtLeast(0) }) { Text("-") }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { currentStep = 1 }) { Text("Next") }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        preferencesViewModel.saveFullPreferences(
                            targetSleepDurationMinutes = 480,
                            wakeUpTimeMs = (7 * 3600000L),
                            errandsDurationMinutes = 30
                        )
                        onComplete()
                    }) { Text("Skip setup") }
                }
                1 -> {
                    Text("When do you wake up?", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TextButton(onClick = { wakeUpHour = (wakeUpHour + 1).coerceAtMost(23) }) { Text("+") }
                            Text(String.format("%02d:%02d", wakeUpHour, wakeUpMinute), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold))
                            TextButton(onClick = { wakeUpHour = (wakeUpHour - 1).coerceAtLeast(0) }) { Text("-") }
                        }
                        Spacer(Modifier.width(32.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TextButton(onClick = { wakeUpMinute = (wakeUpMinute + 5).coerceAtMost(55) }) { Text("+") }
                            Text("min", style = MaterialTheme.typography.bodyLarge)
                            TextButton(onClick = { wakeUpMinute = (wakeUpMinute - 5).coerceAtLeast(0) }) { Text("-") }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { currentStep = 2 }) { Text("Next") }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        preferencesViewModel.saveFullPreferences(
                            targetSleepDurationMinutes = 480,
                            wakeUpTimeMs = (7 * 3600000L),
                            errandsDurationMinutes = 30
                        )
                        onComplete()
                    }) { Text("Skip setup") }
                }
                2 -> {
                    Text("Evening errands buffer", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { errandsMinutes = (errandsMinutes + 5).coerceAtMost(120) }) { Text("+") }
                        Text("${errandsMinutes} min", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold))
                        TextButton(onClick = { errandsMinutes = (errandsMinutes - 5).coerceAtLeast(0) }) { Text("-") }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = {
                        preferencesViewModel.saveFullPreferences(
                            targetSleepDurationMinutes = (sleepHours * 60) + sleepMinutes,
                            wakeUpTimeMs = (wakeUpHour * 3600000L) + (wakeUpMinute * 60000L),
                            errandsDurationMinutes = errandsMinutes
                        )
                        onComplete()
                    }) { Text("Get Started") }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        preferencesViewModel.saveFullPreferences(
                            targetSleepDurationMinutes = 480,
                            wakeUpTimeMs = (7 * 3600000L),
                            errandsDurationMinutes = 30
                        )
                        onComplete()
                    }) { Text("Skip setup") }
                }
            }
        }
    }
}
