package com.zawaro.sleepchad.presentation.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.util.Calendar
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesViewModel: PreferencesViewModel,
    onClose: () -> Unit
) {
    val uiState by preferencesViewModel.preferences.collectAsState()

    @Suppress("DEPRECATION")
    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Settings", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("System Preferences", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text("Settings", style = MaterialTheme.typography.headlineMedium)
            }

            // Display & Time Section
            SettingsSection(title = "Display & Time") {
                val timeFormatOptions = listOf(
                    Triple("System Default", null, "Use system locale setting"),
                    Triple("12-hour (AM/PM)", false, "e.g., 3:45 PM"),
                    Triple("24-hour", true, "e.g., 15:45")
                )

                val timeFormatPreference = uiState.timeFormatPreference ?: "system"
                var expanded = remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Time Format", style = MaterialTheme.typography.bodyLarge)

                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it }
                        ) {
                            OutlinedTextField(
                                value = when (timeFormatPreference) {
                                    "system" -> "System Default"
                                    "true" -> "24-hour"
                                    else -> "12-hour"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Format") },
                                trailingIcon = { 
                                    androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded.value
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                timeFormatOptions.forEach { (label, value, _) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            preferencesViewModel.updateTimeFormat(value?.toString() ?: "system")
                                            expanded.value = false
                                        },
                                        enabled = timeFormatPreference != (value?.toString() ?: "system")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Account Section
            SettingsSection(title = "Account") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsListItem(
                            leadingIcon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            trailingIcon = Icons.Default.ChevronRight
                        )

                        Spacer(Modifier.height(4.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(4.dp))

                        SettingsListItem(
                            leadingIcon = Icons.Default.Description,
                            title = "Terms of Service",
                            trailingIcon = Icons.Default.ChevronRight
                        )
                    }
                }
            }

            // Version info
            Text(
                text = "Version 2.4.0 (Alpha)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun SettingsListItem(
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }

        trailingIcon?.let {
            Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}