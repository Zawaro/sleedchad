package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("SleepChad", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            
            Icon(Icons.Default.Info, contentDescription = "App icon", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            
            Text("Version 2.4.0 (Alpha)", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply { 
                    data = android.net.Uri.parse("https://github.com/anomalyco/sleepchad") 
                }
                context.startActivity(intent)
            }) { Text("Open GitHub Repository") }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onClose) { Text("Done") }
        }
    }
}
