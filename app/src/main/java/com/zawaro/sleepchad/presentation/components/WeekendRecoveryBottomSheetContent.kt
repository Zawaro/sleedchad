package com.zawaro.sleepchad.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zawaro.sleepchad.presentation.settings.ErrandsDurationDialogContent
import com.zawaro.sleepchad.presentation.settings.SleepDurationDialogContent
import com.zawaro.sleepchad.presentation.settings.WakeUpTimeDialogContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekendRecoveryBottomSheetContent(
    initialSleepHours: Int,
    initialSleepMinutes: Int,
    initialWakeMs: Long,
    initialErrandsHours: Int,
    initialErrandsMinutes: Int,
    onSave: (sleepHours: Int, sleepMinutes: Int, wakeMs: Long, errandsHours: Int, errandsMinutes: Int) -> Unit
) {
    var sleepDlg by remember { mutableStateOf(false) }
    var wakeDlg by remember { mutableStateOf(false) }
    var errandsDlg by remember { mutableStateOf(false) }
    var sleepHours by remember { mutableIntStateOf(initialSleepHours) }
    var sleepMinutes by remember { mutableIntStateOf(initialSleepMinutes) }
    var wakeMs by remember { mutableStateOf(initialWakeMs) }
    var errandsHours by remember { mutableIntStateOf(initialErrandsHours) }
    var errandsMinutes by remember { mutableIntStateOf(initialErrandsMinutes) }

    Column(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Weekend Preferences", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
        Text("Separate sleep schedule for Saturday and Sunday", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Card(modifier = Modifier.fillMaxWidth().clickable { sleepDlg = true }) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("TARGET SLEEP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${sleepHours}h ${sleepMinutes}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(modifier = Modifier.fillMaxWidth().clickable { wakeDlg = true }) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("WAKE UP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val cal = java.util.Calendar.getInstance().apply { timeInMillis = wakeMs }
                    Text(String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE)), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(modifier = Modifier.fillMaxWidth().clickable { errandsDlg = true }) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("ERRANDS BUFFER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${errandsHours}h ${errandsMinutes}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Button(
            onClick = { onSave(sleepHours, sleepMinutes, wakeMs, errandsHours, errandsMinutes) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }

    if (sleepDlg) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(onDismissRequest = { sleepDlg = false }, sheetState = sheetState) {
            SleepDurationDialogContent(
                initialHours = sleepHours,
                initialMinutes = sleepMinutes,
                onSave = { h, m ->
                    sleepHours = h; sleepMinutes = m
                    scope.launch { sheetState.hide() }.invokeOnCompletion { sleepDlg = false }
                }
            )
        }
    }

    if (wakeDlg) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(onDismissRequest = { wakeDlg = false }, sheetState = sheetState) {
            WakeUpTimeDialogContent(
                initialEpochMs = wakeMs,
                onSave = { epochMs ->
                    wakeMs = epochMs
                    scope.launch { sheetState.hide() }.invokeOnCompletion { wakeDlg = false }
                }
            )
        }
    }

    if (errandsDlg) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(onDismissRequest = { errandsDlg = false }, sheetState = sheetState) {
            ErrandsDurationDialogContent(
                initialHours = errandsHours,
                initialMinutes = errandsMinutes,
                onSave = { h, m ->
                    errandsHours = h; errandsMinutes = m
                    scope.launch { sheetState.hide() }.invokeOnCompletion { errandsDlg = false }
                }
            )
        }
    }
}
