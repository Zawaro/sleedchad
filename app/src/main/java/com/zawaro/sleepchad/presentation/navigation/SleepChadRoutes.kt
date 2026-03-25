package com.zawaro.sleepchad.presentation.navigation

sealed class SleepChadRoute(val route: String) {
    object Schedule : SleepChadRoute("schedule")
    object Statistics : SleepChadRoute("statistics")
    object Settings : SleepChadRoute("settings")
    object About : SleepChadRoute("about")
}
