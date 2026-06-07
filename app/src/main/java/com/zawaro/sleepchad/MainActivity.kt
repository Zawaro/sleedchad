package com.zawaro.sleepchad

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.zawaro.sleepchad.presentation.navigation.MainLayout
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.ui.theme.SleepChadAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var showSuccessMessage by mutableStateOf<String?>(null)

        val deepLinkAction = intent?.getStringExtra(com.zawaro.sleepchad.platform.alarm.AlarmReceiver.EXTRA_DEEP_LINK_ACTION)

        setContent {
            SleepChadAppTheme {
                val navController = rememberNavController()
                val preferencesViewModel: PreferencesViewModel = viewModel()

                LaunchedEffect(Unit) {
                    preferencesViewModel.loadPreferencesWithSystemDetection()

                    val alarmMgr = getSystemService(android.content.Context.ALARM_SERVICE) as? android.app.AlarmManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmMgr != null && !alarmMgr.canScheduleExactAlarms()) {
                        startActivity(android.content.Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:$packageName")
                        })
                    }

                    if (deepLinkAction == com.zawaro.sleepchad.platform.alarm.AlarmReceiver.ACTION_RECORD_WAKEUP) {
                        preferencesViewModel.recordWakeUp()
                        showSuccessMessage = "Wake-up recorded"
                    }
                }

                MainLayout(
                    preferencesViewModel = preferencesViewModel,
                    navController = navController
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle deep link from notification while app is running
    }
}
