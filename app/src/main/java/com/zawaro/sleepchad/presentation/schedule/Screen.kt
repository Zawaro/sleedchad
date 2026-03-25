package com.zawaro.sleepchad.presentation.schedule

sealed class Screen { 
    object Schedule : Screen()
    object Settings : Screen()
    object About : Screen()
}
