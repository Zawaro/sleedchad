package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zawaro.sleepchad.presentation.statistics.StatisticsViewModel
import com.zawaro.sleepchad.presentation.schedule.AboutScreen
import com.zawaro.sleepchad.presentation.schedule.StatisticsScreen
import com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel

@Composable
fun SleepChadNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    scheduleViewModel: ScheduleViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    statisticsViewModel: StatisticsViewModel = viewModel()
) {
    val startDestination = SleepChadRoute.Schedule.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = SleepChadRoute.Schedule.route) {
            ScheduleScreenWrapper(
                currentRoute = SleepChadRoute.Schedule.route,
                scheduleViewModel = scheduleViewModel,
                preferencesViewModel = preferencesViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    navController.navigate(SleepChadRoute.Settings.route)
                },
                onBottomBarNavigate = { route ->
                    when (route) {
                        "schedule" -> navController.navigate(SleepChadRoute.Schedule.route) {
                            popUpTo(SleepChadRoute.Schedule.route) { inclusive = true }
                        }
                        "statistics" -> navController.navigate(SleepChadRoute.Statistics.route)
                        "settings" -> navController.navigate(SleepChadRoute.Settings.route)
                    }
                },
                onNavigateToStatistics = {
                    navController.navigate(SleepChadRoute.Statistics.route)
                }
            )
        }
        
        composable(route = SleepChadRoute.Statistics.route) {
            StatisticsScreen(
                statisticsUiModel = remember { 
                    statisticsViewModel.uiState.value 
                },
                onClose = {
                    navController.navigate(SleepChadRoute.Schedule.route) {
                        popUpTo(SleepChadRoute.Schedule.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = SleepChadRoute.Settings.route) {
            SettingsScreenWrapper(
                currentRoute = SleepChadRoute.Settings.route,
                preferencesViewModel = preferencesViewModel,
                onClose = { 
                    navController.navigate(SleepChadRoute.Schedule.route) {
                        popUpTo(SleepChadRoute.Schedule.route) { inclusive = true }
                    }
                },
                onNavigateToAbout = {
                    navController.navigate(SleepChadRoute.About.route)
                }
            )
        }
        
        composable(route = SleepChadRoute.About.route) {
            AboutScreen(
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}
