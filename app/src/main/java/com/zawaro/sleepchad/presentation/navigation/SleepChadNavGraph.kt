package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel

@Composable
fun SleepChadNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    scheduleViewModel: ScheduleViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val startDestination = SleepChadRoute.Schedule.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = SleepChadRoute.Schedule.route) {
            ScheduleScreenWrapper(
                viewModel = scheduleViewModel,
                preferencesViewModel = preferencesViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    navController.navigate(SleepChadRoute.Settings.route)
                },
                onBottomBarNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable(route = SleepChadRoute.Statistics.route) {
            StatisticsScreenPlaceholder(
                onBackToSchedule = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = SleepChadRoute.Settings.route) {
            SettingsScreenWrapper(
                preferencesViewModel = preferencesViewModel,
                onClose = { 
                    navController.popBackStack()
                }
            )
        }
    }
}
