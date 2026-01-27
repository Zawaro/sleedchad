package com.zawaro.sleepchad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
// import removed: viewModel not used
import com.zawaro.sleepchad.presentation.schedule.ScheduleScreen
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.ui.theme.SleepChadAppTheme

@Composable
fun MainActivityContent(viewModel: SettingsViewModel) {
    val themeIndex = viewModel.themeIndex.intValue
    SleepChadAppTheme(themeIndex = themeIndex) {
        ScheduleScreen(viewModel)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        val settingsViewModel = SettingsViewModel(application)
            MainActivityContent(settingsViewModel)

}

    }
}
