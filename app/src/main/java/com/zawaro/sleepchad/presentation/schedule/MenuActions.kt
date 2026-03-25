package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
fun MenuActions(
    onSettingsClicked: () -> Unit, 
    onAboutClicked: () -> Unit
) {
    var expanded: Boolean = remember { mutableStateOf(false).value }
    
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { expanded = false; onSettingsClicked() }
        )
        DropdownMenuItem(
            text = { Text("About") },
            onClick = { expanded = false; onAboutClicked() }
        )
    }
}
