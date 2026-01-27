package com.zawaro.sleepchad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun SleepChadAppTheme(themeIndex: Int = 0, content: @Composable () -> Unit) {
    val colors = when(themeIndex) {
        0 -> if (isSystemInDarkTheme()) DarkColors else LightColors
        1 -> LightColors
        2 -> DarkColors
        else -> if (isSystemInDarkTheme()) DarkColors else LightColors
    }
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
