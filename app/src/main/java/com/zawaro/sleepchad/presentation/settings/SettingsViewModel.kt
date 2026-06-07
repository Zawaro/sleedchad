package com.zawaro.sleepchad.presentation.settings

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", 0)

    val themeIndex = mutableIntStateOf(prefs.getInt("theme_index", 0))

    fun updateThemeIndex(newIndex: Int) {
        themeIndex.intValue = newIndex
        prefs.edit().putInt("theme_index", newIndex).apply()
    }
}
