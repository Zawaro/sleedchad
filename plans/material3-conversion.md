# SleepChad: Material 3 UI Conversion Plan

## Overview
Convert the current HTML-based design system to a fully native Kotlin Jetpack Compose implementation using Material Design 3 components.

---

## Current State Analysis

### Existing Architecture
- **Architecture Pattern**: Clean Architecture (Data → Domain → Presentation)
- **Database**: Room with entities: `CustomAlarmEntity`, `ScheduleEntity`, `SleepSessionEntity`
- **Navigation**: Simple state-based navigation within single Activity
- **Theming**: Basic Material 3 setup with empty color/type schemes

### Current Screens
1. **ScheduleScreen** - Main screen with preferences and custom alarms
2. **SettingsScreen** - Preferences management (time format, about)
3. **Missing**: Statistics screen (referenced in HTML but not implemented)

---

## Implementation Checklist

---

## Implementation Plan

### Phase 1: Material 3 Theme System ✅ Priority: High

**Status:** In Progress

- [x] Create `Color.kt` with adapted color palette (#4edea3 primary, #0b1326 background)
- [ ] Update `Theme.kt` to use proper dark/light color schemes  
- [ ] Create `Type.kt` with Roboto font family (Material 3 default)

#### Files to Create/Modify:

**1. `app/src/main/java/com/zawaro/sleepchad/ui/theme/Color.kt`**
```kotlin
// Define color palette from HTML designs:
val Primary = Color(0xFF4EDEA3)
val OnPrimary = Color(0xFF003824)
val PrimaryContainer = Color(0xFF10B981)
val OnPrimaryContainer = Color(0xFF00422B)

val Background = Color(0xFF0B1326)
val OnBackground = Color(0xFFDAE2FD)
val Surface = Color(0xFF0B1326)
val OnSurface = Color(0xFFDAE2FD)
val SurfaceVariant = Color(0xFF2D3449)
val OnSurfaceVariant = Color(0xFFBBCABF)

// Additional tones:
val Secondary = Color(0xFFADC6FF)
val Tertiary = Color(0xFFffb95f)
```

**2. `app/src/main/java/com/zawaro/sleepchad/ui/theme/Theme.kt`**
- Update to use proper `darkColorScheme()` and `lightColorScheme()` with your palette
- Add theme index support for dark/light/auto modes
- Implement dynamic color support (optional, Android 12+)

**3. `app/src/main/java/com/zawaro/sleepchad/ui/theme/Type.kt`**
```kotlin
// Inter font family integration:
val SleepChadTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter),
    headlineMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontFamily = Inter),
    labelSmall = TextStyle(fontFamily = Inter, letterSpacing = 0.15.em)
)
```

**4. `app/src/main/java/com/zawaro/sleepchad/ui/theme/Components.kt` (New)**
- Reusable Material 3 component wrappers:
  - `SleepChadCard()` - Consistent card styling
  - `SleepChadButton()` - Primary/action button styles
  - `SleepChadSwitch()` - Toggle switches with custom theming

---

### Phase 2: Navigation Architecture ✅ Priority: High

#### Files to Create/Modify:

**1. `app/src/main/java/com/zawaro/sleepchad/presentation/navigation/SleepChadNavGraph.kt` (New)**
```kotlin
sealed class SleepChadRoute(
    val route: String,
    @StringRes val title: Int? = null
) {
    object Schedule : SleepChadRoute("schedule")
    object Statistics : SleepChadRoute("statistics")
    object Settings : SleepChadRoute("settings")
}

@Composable
fun SleepChadNavGraph(
    viewModel: MainViewModel,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = SleepChadRoute.Schedule.route) {
        composable<SleepChadRoute.Schedule> {
            ScheduleScreen(...)
        }
        composable<SleepChadRoute.Statistics> {
            StatisticsScreen() // Placeholder
        }
        composable<SleepChadRoute.Settings> {
            SettingsScreen(onClose = { navController.popBackStack() }, ...)
        }
    }
}
```

**2. `app/src/main/java/com/zawaro/sleepchad/presentation/navigation/BottomNavHost.kt` (New)**
- Implement bottom navigation bar matching HTML design:
  - Schedule icon (alarm) - active state with #4edea3 color
  - Statistics icon (leaderboard)
  - Settings icon (settings)
  - Glassmorphism effect (rgba background + blur)

**3. Update `MainActivity.kt`**
- Integrate Navigation Compose
- Replace state-based navigation with NavController

---

### Phase 3: Schedule Screen Redesign ✅ Priority: Medium

#### Files to Modify/Create:

**1. Refactor `ScheduleScreen.kt`**

**TopAppBar Replacement:**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepChadTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Default.Bedtime, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("SleepChad", style = MaterialTheme.typography.headlineSmall)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.statusBarsPadding()
    )
}
```

**Global Preferences Section:**
```kotlin
@Composable
private fun GlobalPreferencesCard(
    preferences: UserPreferencesUiModel,
    onTargetSleepClick: () -> Unit,
    onWakeUpClick: () -> Unit,
    onErrandsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Global Optimization",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.15.em
            )
            Spacer(Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PreferenceCard(
                    icon = Icons.Default.AutoAwesome,
                    label = "Target Sleep",
                    value = "${preferences.targetSleepDurationMinutes ?: 480 / 60}h ${(preferences.targetSleepDurationMinutes ?: 480) % 60}m",
                    onClick = onTargetSleepClick
                )
                PreferenceCard(
                    icon = Icons.Default.WbSunny,
                    label = "Wake Up",
                    value = TimeFormatter.formatTime(LocalContext.current, preferences.wakeUpTimeMs ?: 25200000L, null),
                    onClick = onWakeUpClick
                )
                PreferenceCard(
                    icon = Icons.Default.Bolt,
                    label = "Errands Buffer",
                    value = "${preferences.errandsDurationMinutes ?: 30}m",
                    onClick = onErrandsClick
                )
            }
        }
    }
}

@Composable
private fun PreferenceCard(
    icon: VectorIcon,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}
```

**Last Night's Sleep Banner:**
```kotlin
@Composable
private fun LastNightBanner(lastNightSleep: Int?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Last night: ${lastNightSleep?.let { "${it / 60}h ${(it % 60)}m" } ?: "Not recorded"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    lastNightSleep?.let { "94% of your daily discipline target reached." } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
```

**Action Buttons Section:**
```kotlin
@Composable
private fun ActionButtons(
    lastNightSleep: Int?,
    onBedtimeClick: () -> Unit,
    onWakeUpClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onBedtimeClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(50.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Icon(Icons.Default.Bed, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("I'm Going to Bed", style = MaterialTheme.typography.labelLarge)
        }
        
        OutlinedButton(
            onClick = onWakeUpClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(50.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Icon(Icons.Default.AlarmOn, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("I Woke Up", style = MaterialTheme.typography.labelLarge)
        }
    }
}
```

**Custom Alarms Accordion:**
```kotlin
@Composable
private fun CustomAlarmsSection(
    alarms: List<CustomAlarmUiModel>,
    onAddClick: () -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Custom Schedules", style = MaterialTheme.typography.headlineSmall)
                Text("Override schedule", style = MaterialTheme.typography.labelSmall)
            }
            TextButton(onClick = { /* Clear all */ }) {
                Text("Clear All")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(alarms) { alarm ->
                CustomAlarmAccordionItem(
                    alarm = alarm,
                    onDelete = { onDelete(alarm.id) }
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add alarm")
        }
    }
}

@Composable
private fun CustomAlarmAccordionItem(
    alarm: CustomAlarmUiModel,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(alarm.name, style = MaterialTheme.typography.headlineSmall)
                        Text("Days: ${alarm.enabledDays.joinToString(", ") { getDayLabel(it) }}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(Modifier.height(16.dp))
                
                // Day picker row
                ClockDayPicker(selectedDays = alarm.enabledDays, onDayToggle = { /* TODO */ })
                
                Spacer(Modifier.height(12.dp))
                
                // Settings (snooze, vibrate)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = Text("Snooze: 5m")
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = Text("Vibrate only")
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockDayPicker(selectedDays: Set<Int>, onDayToggle: (Int) -> Unit) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dayLabels.forEachIndexed { index, label ->
            val dayNum = index + 1
            val isSelected = dayNum in selectedDays
            
            FilterChip(
                selected = isSelected,
                onClick = { onDayToggle(dayNum) },
                label = Text(label),
                leadingIcon = if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null)
                } else null,
                shape = CircleShape
            )
        }
    }
}
```

**Add Custom Alarm Dialog:**
```kotlin
@Composable
private fun AddCustomAlarmDialog(
    preferences: UserPreferencesUiModel,
    existingAlarms: List<CustomAlarmUiModel>,
    onDismiss: () -> Unit,
    onSave: (String, Set<Int>, Int, Int, Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var targetSleepHours by remember { mutableIntStateOf(7) }
    var targetSleepMinutes by remember { mutableIntStateOf(30) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Schedule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ClockDayPicker(selectedDays, onDayToggle)
                
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Hours", style = MaterialTheme.typography.labelSmall) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (targetSleepHours > 0) targetSleepHours-- }) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }
                        Text(targetSleepHours.toString(), style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { if (targetSleepHours < 23) targetSleepHours++ }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
                
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Minutes", style = MaterialTheme.typography.labelSmall) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (targetSleepMinutes >= 0 && targetSleepMinutes < 60) targetSleepMinutes -= 5 }) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }
                        Text(targetSleepMinutes.toString(), style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { if (targetSleepMinutes >= 0 && targetSleepMinutes < 60) targetSleepMinutes += 5 }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedDays, targetSleepHours, targetSleepMinutes, preferences.wakeUpTimeMs) },
                enabled = selectedDays.isNotEmpty()
            ) { Text("Save Routine") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
```

---

### Phase 4: Settings Screen Redesign ✅ Priority: Medium

#### Files to Modify/Create:

**1. Refactor `SettingsScreen.kt`**

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesViewModel: PreferencesViewModel,
    onClose: () -> Unit
) {
    val uiState by preferencesViewModel.preferences.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("System Preferences", style = MaterialTheme.typography.labelSmall)
                    Text("Settings", style = MaterialTheme.typography.headlineMedium)
                }
            }
            
            // Display & Time Section
            item {
                SettingsSection(
                    title = "Display & Time",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TimeFormatPreference(preferencesViewModel = preferencesViewModel, uiState = uiState)
                }
            }
            
            // Communications Section
            item {
                SettingsSection(
                    title = "Communications",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    NotificationToggle(
                        icon = Icons.Default.NotificationsActive,
                        title = "Push Notifications",
                        subtitle = "Wake up alarms and wind-down alerts.",
                        enabled = true // TODO: load from preferences
                    )
                    
                    Spacer(Modifier.height(1.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(1.dp))
                    
                    NotificationToggle(
                        icon = Icons.Default.Insights,
                        title = "Weekly Sleep Report",
                        subtitle = "Personalized performance analysis.",
                        enabled = false // TODO: load from preferences
                    )
                }
            }
            
            // Support & Info (Right column in desktop, below in mobile)
            item {
                SettingsSection(
                    title = "Account",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SettingsListItem(
                        leadingIcon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        trailingIcon = Icons.Default.ChevronRight
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    SettingsListItem(
                        leadingIcon = Icons.Default.Description,
                        title = "Terms of Service",
                        trailingIcon = Icons.Default.ChevronRight
                    )
                }
            }
            
            // Version info
            item {
                Text(
                    text = "Version 2.4.0 (Alpha)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(content)
        }
    }
}

@Composable
private fun TimeFormatPreference(preferencesViewModel: PreferencesViewModel, uiState: UserPreferencesUiModel) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "Current Format", // TODO: derive from uiState
            onValueChange = {},
            label = { Text("Time Format") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = { Text("System Default") }, onClick = { /* TODO */ })
            DropdownMenuItem(text = { Text("12-hour (AM/PM)") }, onClick = { /* TODO */ })
            DropdownMenuItem(text = { Text("24-hour") }, onClick = { /* TODO */ })
        }
    }
}

@Composable
private fun NotificationToggle(
    icon: VectorIcon,
    title: String,
    subtitle: String,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Switch(checked = enabled, onCheckedChange = { /* TODO */ })
    }
}

@Composable
private fun SettingsListItem(
    leadingIcon: VectorIcon,
    title: String,
    trailingIcon: VectorIcon? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        
        trailingIcon?.let {
            Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
```

---

### Phase 5: Bottom Navigation Bar ✅ Priority: High

#### Files to Create:

**`app/src/main/java/com/zawaro/sleepchad/presentation/navigation/SleepChadBottomBar.kt` (New)**

```kotlin
@Composable
fun SleepChadBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0B1326).copy(alpha = 0.6f),
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            NavigationBarItem(
                selected = currentRoute == SleepChadRoute.Schedule.route,
                onClick = { onNavigate(SleepChadRoute.Schedule.route) },
                icon = {
                    Icon(
                        if (currentRoute == SleepChadRoute.Schedule.route) Icons.Default.Alarm else Icons.Default.Outlined.Alarm,
                        contentDescription = "Schedule"
                    )
                },
                label = { Text("Schedule", style = MaterialTheme.typography.labelSmall) }
            )
            
            NavigationBarItem(
                selected = currentRoute == SleepChadRoute.Statistics.route,
                onClick = { onNavigate(SleepChadRoute.Statistics.route) },
                icon = {
                    Icon(
                        if (currentRoute == SleepChadRoute.Statistics.route) Icons.Default.Leaderboard else Icons.Default.Outlined.Leaderboard,
                        contentDescription = "Statistics"
                    )
                },
                label = { Text("Statistics", style = MaterialTheme.typography.labelSmall) }
            )
            
            NavigationBarItem(
                selected = currentRoute == SleepChadRoute.Settings.route,
                onClick = { onNavigate(SleepChadRoute.Settings.route) },
                icon = {
                    Icon(
                        if (currentRoute == SleepChadRoute.Settings.route) Icons.Default.Settings else Icons.Default.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                },
                label = { Text("Settings", style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}
```

---

## Implementation Order

1. **Day 1**: Create Color.kt, Type.kt, update Theme.kt ✅ DONE
2. **Day 2**: Implement Navigation architecture (NavGraph, BottomBar) ✅ DONE  
3. **Day 3-4**: Redesign ScheduleScreen with Material 3 components ⏳ IN PROGRESS
   - [ ] Add "Last night's sleep" analytics banner
   - [ ] Replace linear day picker with circular version
   - [ ] Add gradient action buttons
   - [ ] Add edit icons to preference cards
4. **Day 5**: Redesign Add Custom Alarm Dialog ⏳ IN PROGRESS
   - [ ] Circular day picker with glow effects
   - [ ] Hour/Minute controls in separate cards
   - [ ] Calculated bedtime display card
   - [ ] Gradient Save button + Delete option
5. **Day 6**: Test and refine animations, transitions, accessibility

---

## Success Criteria

✅ All screens use Material 3 component primitives (not custom Composables)  
✅ Consistent color scheme matching HTML designs  
✅ Proper typography hierarchy using Inter font  
✅ Bottom navigation matches HTML design  
✅ Responsive layouts for different screen sizes  
✅ Accessibility (content descriptions, talkback support)  
✅ No hardcoded colors or dimensions  

---

## Design Notes

- Use `MaterialTheme.colorScheme` everywhere instead of direct Color values
- Maintain existing ViewModels and use cases - no refactoring needed there
- Keep database schema as-is
- Test on multiple screen sizes (phone, tablet)

### Key HTML Design Elements to Match:

**Colors:**
- Primary: `#4EDea3` → `MaterialTheme.colorScheme.primary`
- Background/Surface: `#0B1326` → `MaterialTheme.colorScheme.background`
- Surface Container Low: `#131b2e` → `MaterialTheme.colorScheme.surfaceContainerLow`

**Typography:**
- Headline: Inter font (use Roboto as Material 3 default)
- Label: 10px/11px uppercase with tracking

**Components:**
- Circular day picker: Use FilterChip with CircleShape or custom Surface
- Gradient buttons: Use `graphicsLayer { alpha = 1f }` + color filters or ImageBitmap
- Glassmorphism: Use `MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)` with blur

---

## Open Questions

- Should we use Inter font via assets or stick with Roboto (Material 3 default)?
- Gradient implementation: Compose doesn't support gradient backgrounds on buttons natively - should we use ImageBitmap or accept solid colors?