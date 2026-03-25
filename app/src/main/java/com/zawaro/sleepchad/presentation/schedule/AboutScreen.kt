package com.zawaro.sleepchad.presentation.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "1.0"
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("SleepChad", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Icon(Icons.Default.Info, contentDescription = "App icon", modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Version: $versionName")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply { 
                data = android.net.Uri.parse("https://github.com/anomalyco/sleepchad") 
            }
            context.startActivity(intent)
        }) { Text("Open GitHub Repository") }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClose) { Text("Done") }
    }
}
