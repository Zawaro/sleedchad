package com.zawaro.sleepchad.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zawaro.sleepchad.presentation.schedule.AboutScreen
import com.zawaro.sleepchad.presentation.schedule.ScheduleScreenWrapper
import com.zawaro.sleepchad.presentation.schedule.StatisticsScreen
import com.zawaro.sleepchad.presentation.settings.PreferencesViewModel
import com.zawaro.sleepchad.presentation.settings.SettingsViewModel
import com.zawaro.sleepchad.presentation.statistics.StatisticsViewModel
import com.zawaro.sleepchad.presentation.welcome.WelcomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenWrapper(
    currentRoute: String,
    onNavigateToSettings: () -> Unit,
    onBottomBarNavigate: (String) -> Unit,
    onNavigateToStatistics: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    showSuccessMessage: String? = null
) {
    val scheduleViewModel: com.zawaro.sleepchad.presentation.schedule.ScheduleViewModel = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    com.zawaro.sleepchad.presentation.schedule.ScheduleScreenWrapper(
        currentRoute = currentRoute,
        scheduleViewModel = scheduleViewModel,
        preferencesViewModel = preferencesViewModel,
        settingsViewModel = settingsViewModel,
        onNavigateToSettings = onNavigateToSettings,
        onBottomBarNavigate = onBottomBarNavigate,
        onNavigateToStatistics = onNavigateToStatistics,
        snackbarHostState = snackbarHostState,
        showSuccessMessage = showSuccessMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWrapper(
    onClose: () -> Unit
) {
    val preferencesViewModel: PreferencesViewModel = viewModel()
    var showAboutScreen by remember { mutableStateOf(false) }

    if (showAboutScreen) {
        AboutScreen(
            onClose = {
                showAboutScreen = false
                onClose()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("System Preferences", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                }
            }

            item {
                SettingsSection(title = "Display & Time") {
                    val uiState by preferencesViewModel.preferences.collectAsState()

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Theme", style = MaterialTheme.typography.bodyLarge)
                                    val themeLabel = when (uiState.themeIndex) {
                                        0 -> "System Default"
                                        1 -> "Light"
                                        2 -> "Dark"
                                        else -> "System Default"
                                    }
                                    Text(themeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                var themeExpanded by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = themeExpanded,
                                    onExpandedChange = { themeExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = when (uiState.themeIndex) {
                                            0 -> "System Default"
                                            1 -> "Light"
                                            2 -> "Dark"
                                            else -> "System Default"
                                        },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Select Theme") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = themeExpanded,
                                        onDismissRequest = { themeExpanded = false }
                                    ) {
                                        listOf("System Default", "Light", "Dark").forEach { label ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    preferencesViewModel.updateThemeIndex(
                                                        when (label) {
                                                            "System Default" -> 0
                                                            "Light" -> 1
                                                            "Dark" -> 2
                                                            else -> 0
                                                        }
                                                    )
                                                    themeExpanded = false
                                                },
                                                enabled = uiState.themeIndex != when (label) {
                                                    "System Default" -> 0
                                                    "Light" -> 1
                                                    "Dark" -> 2
                                                    else -> 0
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(Modifier.height(16.dp))

                            Text("Time Format", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            val timeFormatOptions = listOf("System Default", "12-hour (AM/PM)", "24-hour")
                            val expandedState = remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedState.value,
                                onExpandedChange = { newState -> expandedState.value = newState }
                            ) {
                                OutlinedTextField(
                                    value = when (uiState.timeFormatPreference?.toBoolean()) {
                                        null -> "System Default"
                                        true -> "24-hour"
                                        false -> "12-hour"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Select Format") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState.value) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedState.value,
                                    onDismissRequest = { expandedState.value = false }
                                ) {
                                    timeFormatOptions.forEach { label ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                preferencesViewModel.updateTimeFormat(
                                                    when (label) {
                                                        "System Default" -> null
                                                        "12-hour (AM/PM)" -> "false"
                                                        "24-hour" -> "true"
                                                        else -> null
                                                    }
                                                )
                                                expandedState.value = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Communications") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val notifState = remember { mutableStateOf(true) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.NotificationsActive,
                                title = "Push Notifications",
                                subtitle = "Wake up alarms and wind-down alerts.",
                                enabledState = notifState
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            val weeklyState = remember { mutableStateOf(false) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Insights,
                                title = "Weekly Sleep Report",
                                subtitle = "Personalized performance analysis.",
                                enabledState = weeklyState
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            val newsletterState = remember { mutableStateOf(false) }
                            SettingsListItemWithToggle(
                                leadingIcon = Icons.Default.Mail,
                                title = "Newsletter",
                                subtitle = "Tips for better sleep hygiene.",
                                enabledState = newsletterState
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Account") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            SettingsListItemWithClick(
                                leadingIcon = Icons.Default.Info,
                                title = "About",
                                onClick = { showAboutScreen = true }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItem(
                                leadingIcon = Icons.Default.PrivacyTip,
                                title = "Privacy Policy",
                                trailingIcon = Icons.Default.ChevronRight
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            SettingsListItem(
                                leadingIcon = Icons.Default.Description,
                                title = "Terms of Service",
                                trailingIcon = Icons.Default.ChevronRight
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Version 2.4.0 (Alpha)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Disciplined Sleep for ChadS.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingsListItemWithClick(
    leadingIcon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsListItem(
    leadingIcon: ImageVector,
    title: String,
    trailingIcon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingText: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = if (textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText.isNotEmpty()) {
                Text(trailingText, style = MaterialTheme.typography.labelSmall, color = textColor)
                Spacer(Modifier.width(8.dp))
            }
            trailingIcon?.let {
                Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingsListItemWithToggle(
    leadingIcon: ImageVector,
    title: String,
    subtitle: String,
    enabledState: MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = enabledState.value,
            onCheckedChange = { enabledState.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun MainLayout(
    preferencesViewModel: PreferencesViewModel,
    navController: NavHostController
) {
    val currentRoute = remember(navController) {
        mutableStateOf(SleepChadRoute.Schedule.route)
    }

    LaunchedEffect(navController.currentBackStackEntryFlow) {
        navController.currentBackStackEntryFlow.collect { entry ->
            entry.destination.route?.let { route ->
                currentRoute.value = route
            }
        }
    }

    val onboardingComplete by preferencesViewModel.onboardingComplete.collectAsState()
    val preferences by preferencesViewModel.preferences.collectAsState()
    val showWelcome = !onboardingComplete || (preferences.targetSleepDurationMinutes == null && preferences.wakeUpTimeMs == null)

    if (showWelcome) {
        com.zawaro.sleepchad.presentation.welcome.WelcomeScreen(
            preferencesViewModel = preferencesViewModel,
            onComplete = {
                preferencesViewModel.completeSetup()
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                SleepChadBottomBar(
                    currentRoute = currentRoute.value,
                    onNavigate = { route ->
                        when (route) {
                            "schedule" -> navController.navigate(SleepChadRoute.Schedule.route) {
                                popUpTo(SleepChadRoute.Schedule.route) { inclusive = true }
                            }
                            "statistics" -> navController.navigate(SleepChadRoute.Statistics.route)
                            "settings" -> navController.navigate(SleepChadRoute.Settings.route)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SleepChadNavGraph(
                    navController = navController,
                    currentRouteProvider = { currentRoute.value }
                )
            }
        }
    }
}

@Composable
fun SleepChadNavGraph(
    navController: NavHostController,
    currentRouteProvider: () -> String
) {
    val startDestination = SleepChadRoute.Schedule.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = SleepChadRoute.Schedule.route) {
            ScheduleScreenWrapper(
                currentRoute = currentRouteProvider(),
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
            val statisticsViewModel: StatisticsViewModel = viewModel()
            val statisticsUiState by statisticsViewModel.uiState.collectAsState()
            StatisticsScreen(
                statisticsUiModel = statisticsUiState,
                onClose = {}
            )
        }

        composable(route = SleepChadRoute.Settings.route) {
            SettingsScreenWrapper(
                onClose = {}
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
