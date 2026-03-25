# SleepChad App Navigation & View Plan

## Overview
Convert the current dialog-based navigation system to a proper view-based architecture with seamless screen transitions.

---

## Current State Analysis

### Existing Architecture
- **Navigation Pattern**: State-based with Screen sealed class (Schedule, Settings, About)
- **Current Issue**: 
  - Settings and About screens show as dialogs instead of full views
  - Active state uses light blue background (#6750A4) instead of transparent green
  - Only icon is highlighted, not the entire navigation item

### Files to Update
1. `app/src/main/java/com/zawaro/sleepchad/presentation/schedule/ScheduleScreen.kt`
2. `app/src/main/java/com/zawaro/sleepchad/presentation/settings/SettingsScreen.kt`
3. `app/src/main/java/com/zawaro/sleepchad/presentation/about/AboutScreen.kt`
4. `app/src/main/java/com/zawaro/sleepchad/presentation/schedule/Screen.kt`

---

## Implementation Checklist

### Schedule View Improvements ✅ PRIORITY: HIGH

#### Top Control Cards
- [ ] **Global Alarm Toggle Card**
  - Location: Top of Schedule view, above "GLOBAL OPTIMIZATION"
  - Content: Toggle switch + descriptive text ("Enable/Disable Alarms")
  - Functionality: Toggles app-wide alarm functionality (AlarmManager registration)
  - Color: Mint green when enabled, gray when disabled

- [ ] **Target Sleep Card** (Already exists but needs verification)
  - Icon: Bedtime icon (mint green)
  - Edit button: Pencil icon on right side
  - Functionality: Opens dialog to change target sleep duration
  - Shows hours and minutes below title

- [ ] **Wake Up Time Card** (Already exists but needs verification)
  - Icon: WbSunny icon (yellow/secondary color)
  - Edit button: Pencil icon on right side
  - Functionality: Opens dialog to change wake up time
  - Shows formatted time below title

- [ ] **Errands Duration Card** (Already exists but needs verification)
  - Icon: Bolt/Lightning icon (tertiary color)
  - Edit button: Pencil icon on right side
  - Functionality: Opens dialog to change errands buffer duration
  - Shows hours and minutes below title

#### Action Section
- [ ] **"I'm Going To Bed" Button** (Already exists but needs verification)
  - Style: Gradient mint green primary button
  - Functionality: Records bedtime, starts sleep session tracking
  - Location: First of two stacked buttons

- [ ] **"I Woke Up" Button** (Already exists but needs verification)
  - Style: Solid surface variant (#1B263B) filled button
  - Functionality: Records wake up time, calculates sleep duration
  - Location: Second of two stacked buttons

#### Analytics Section
- [ ] **Last Night's Sleep Card**
  - Location: Below action buttons, above custom schedules
  - Content: Duration + discipline percentage
  - Accent: Green border (#4DD0E1 alpha 0.3f)
  - Functionality: On click → navigate to Statistics view
  - Icon: Analytics chart icon

#### Custom Schedules Section
- [ ] **Header Update**
  - Title: "Custom Schedules" (TitleLarge ExtraBold)
  - Subtitle: Small text indicating empty state if no items exist
  
- [ ] **Weekend Recovery Card** (New feature)
  - Location: Last item in custom schedules list OR separate section
  - Content: Weekend-specific alarm override
  - Functionality: Toggle to enable/disable weekend recovery mode

#### Header Title
- [ ] **"SleepChad" Title Color Change**
  - Current: Default onSurface color
  - Target: Green/mint (#4EDEA3 or MaterialTheme.colorScheme.primary)
  - Location: TopAppBar center, next to moon icon

---

### View Architecture Conversion ✅ PRIORITY: HIGH

#### Schedule View (Already a view ✓)
- [ ] Verify it renders as full-screen view (not dialog)
- [ ] Ensure proper Scaffold with topBar and floatingActionButton

#### Statistics View (Dialog → View conversion)
**Current Issue:** Shows as modal dialog overlay
**Target:** Full-screen view with back navigation

- [ ] Create `StatisticsScreen.kt` as full Composable view
- [ ] Add TopAppBar with:
  - Back arrow icon (navigationIcon)
  - "Statistics" title in green color
  - Optional: Settings/help icon (actions)
- [ ] Remove AlertDialog wrapper
- [ ] Implement statistics content:
  - Sleep duration chart/graph
  - Discipline percentage over time
  - Weekly/monthly summaries

#### Settings View (Dialog → View conversion)
**Current Issue:** Shows as modal dialog overlay
**Target:** Full-screen view with back navigation

- [ ] Update `SettingsScreen.kt` to use Scaffold pattern
- [ ] Add TopAppBar with:
  - Back arrow icon (navigationIcon)
  - "Settings" title in green color
  - Close button (IconButton with X or ArrowBack)
- [ ] Remove AlertDialog wrapper
- [ ] Keep existing settings content structure

#### Navigation State Management
- [ ] Update Screen sealed class to add Statistics screen:
  ```kotlin
  sealed class Screen {
      object Schedule : Screen()
      object Statistics : Screen()  // NEW
      object Settings : Screen()
      object About : Screen()
  }
  ```

- [ ] Fix active state highlighting:
  - Current: Light blue background (#6750A4) on icon only
  - Target: Transparent green background + full item highlight
  
**Implementation:**
```kotlin
// In BottomNavigation or custom navigation bar
NavigationBarItem(
    selected = currentScreen is Screen.Schedule,
    onClick = { currentScreen = Screen.Schedule },
    modifier = Modifier
        .fillMaxWidth()
        .background(
            if (currentScreen is Screen.Schedule) 
                Color(0xFF4EDEA3).copy(alpha = 0.15f)  // Transparent green
            else 
                Color.Transparent
        ),
    icon = { ... },
    label = { Text("Schedule") }
)
```

---

## Implementation Order

### Day 1: Schedule View Enhancements ✅ PRIORITY: HIGH
1. Add global alarm toggle card at top of Schedule view
2. Verify target sleep, wake up, errands cards have correct icons and edit buttons
3. Update "SleepChad" title color to green/mint
4. Test all existing functionality (bedtime/wake recording)

### Day 2: Custom Schedules & Weekend Recovery ✅ PRIORITY: MEDIUM
1. Implement weekend recovery card with toggle functionality
2. Add empty state text for custom schedules list
3. Verify header layout matches design specs

### Day 3: View Architecture Conversion ✅ PRIORITY: HIGH
1. Convert Settings from dialog to full view
2. Create Statistics as new full-screen view
3. Update Screen sealed class to include Statistics
4. Fix navigation highlighting (transparent green background)

### Day 4: Testing & Polish
1. Test all screen transitions
2. Verify back navigation works correctly
3. Ensure consistent styling across views
4. Run full build and test suite

---

## Technical Implementation Details

### View vs Dialog Pattern

**Current Dialog Pattern:**
```kotlin
// SettingsScreen called as dialog
Dialog(onDismissRequest = { onClose() }) {
    SettingsContent(...)
}
```

**New View Pattern:**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClose: () -> Unit,
    preferencesViewModel: PreferencesViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Settings", color = MaterialTheme.colorScheme.primary) 
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Settings content here
        }
    }
}
```

### Screen Navigation Pattern

**Current State:**
```kotlin
var currentScreen: Screen = remember { mutableStateOf(Screen.Schedule).value }

when (currentScreen) {
    is Screen.Schedule -> ScheduleContent(...)
    is Screen.Settings -> SettingsDialog(onClose = { ... })  // Dialog!
    is Screen.About -> AboutDialog(onClose = { ... })        // Dialog!
}
```

**Target State:**
```kotlin
var currentScreen: Screen = remember { mutableStateOf(Screen.Schedule).value }

when (currentScreen) {
    is Screen.Schedule -> ScheduleScreen(
        viewModel = viewModel,
        preferencesViewModel = preferencesViewModel
    )
    is Screen.Statistics -> StatisticsScreen(
        onClose = { currentScreen = Screen.Schedule }
    )
    is Screen.Settings -> SettingsScreen(
        onClose = { currentScreen = Screen.Schedule },
        preferencesViewModel = preferencesViewModel
    )
    is Screen.About -> AboutScreen(onClose = { currentScreen = Screen.Schedule })
}
```

---

## Success Criteria

✅ All screens render as full views (not dialogs)  
✅ Active navigation item has transparent green background covering icon + label  
✅ "SleepChad" header title displays in mint green color  
✅ Global alarm toggle card controls app-wide alarm functionality  
✅ Last night's sleep card navigates to Statistics view on click  
✅ Weekend recovery card exists with functional toggle  
✅ Empty state text shows when no custom schedules exist  
✅ All edit buttons (pencil icons) open correct dialogs  
✅ Back navigation works correctly from all views  
✅ Build passes without errors  

---

## Design Notes

### Color Usage
- **Primary/Green:** #4EDEA3 for active states, titles, accents
- **Surface Variant:** #1B263B for secondary buttons and inactive states
- **Background:** #0B1326 for main background
- **Transparent Green:** #4EDEA3 with alpha 0.15f for navigation highlights

### Typography
- **Header titles:** headlineSmall, ExtraBold, green color
- **Section headers:** labelSmall (10sp), onSurfaceVariant
- **Card titles:** labelMedium or bodyLarge depending on context
- **Time values:** headlineSmall, ExtraBold, 24sp

### Spacing & Layout
- Card radius: 16dp rounded corners
- Section spacing: 20dp vertical between major sections
- Button spacing: 12dp horizontal for action buttons
- Padding: 16dp on all sides of content columns

---

## Files to Create/Modify

### Modify
1. `app/src/main/java/com/zawaro/sleepchad/presentation/schedule/ScheduleScreen.kt`
   - Add global alarm toggle card
   - Update header title color
   - Convert Statistics navigation from dialog to view call

2. `app/src/main/java/com/zawaro/sleepchad/presentation/schedule/Screen.kt`
   - Add Statistics object to sealed class

3. `app/src/main/java/com/zawaro/sleepchad/presentation/settings/SettingsScreen.kt`
   - Convert from AlertDialog to Scaffold-based view

4. `app/src/main/java/com/zawaro/sleepchad/presentation/about/AboutScreen.kt`
   - (Optional) Convert from AlertDialog if needed

### Create
1. `app/src/main/java/com/zawaro/sleepchad/presentation/statistics/StatisticsScreen.kt`
   - New full-screen view for sleep statistics
   - Charts, graphs, and summaries

2. `app/src/main/java/com/zawaro/sleepchad/domain/model/GlobalAlarmState.kt` (optional)
   - Data class for tracking alarm enabled/disabled state

---

## Open Questions

1. Should weekend recovery be a separate card or integrated into custom schedules?
2. What statistics should be displayed in the Statistics view?
3. Should global alarm toggle persist across app restarts (SharedPreferences)?
4. Should navigation highlight use solid green or transparent overlay?