# SleepChad MVP Checklist - Current Implementation Status

## 🎯 CURRENT ARCHITECTURE OVERVIEW

### Core Concepts Implemented:
1. **UserPreferencesEntity**: Global defaults stored separately from alarms
   - Target sleep duration (hours + minutes)
   - Wake-up time  
   - Evening errands duration
   - Time format preference (auto-detect from system Locale by default, overrideable)

2. **CustomAlarmEntity**: Exception alarms inheriting from UserPreferences
   - Enable days (Mon-Sun selection)
   - Custom name
   - Nullable fields: null = inherit from UserPreferences, set value = override
   - Auto-calculated bedtime based on resolved values

3. **Header-First UI Design**:
   - Top section shows global preferences (target sleep, wake time, errands duration)
   - All preference items clickable with pencil icon for inline editing
   - No default alarm row visible to user

4. **Custom Alarms List**: Only exception/custom alarms shown below header
   - Accordion layout expand/collapse functionality
   - Clock-style day picker (single letters M, T, W, T, F, S, S)
   - FAB button bottom-right to add custom alarms

---

## ✅ PHASE 1: PREFERENCES & DEFAULT ALARM SYSTEM

### Database Layer

#### New Entities
- [x] `UserPreferencesEntity.kt`:
  - [x] `id: Long` (primary key, single row)
  - [x] `targetSleepHours: Int`
  - [x] `targetSleepMinutes: Int` (0-59)
  - [x] `wakeUpTimeMs: Long` (epoch ms for today's wake-up time)
  - [x] `errandsDurationHours: Int`
  - [x] `errandsDurationMinutes: Int`
  - [x] `timeFormat24Hour: Boolean?` (use system default if null)

- [x] `CustomAlarmEntity.kt`:
  - [x] `id: Long` (primary key, auto-generated)
  - [x] `name: String` ("Custom Alarm" or user-provided)
  - [x] `enabledDaysString: String` (comma-separated day numbers 1-7)
  - [x] `targetSleepDurationMinutes: Int?` (null = inherit from UserPreferences)
  - [x] `wakeUpTimeMs: Long?` (null = inherit from UserPreferences)
  - [x] `errandsDurationHours: Int?` (null = inherit)
  - [x] `errandsDurationMinutes: Int?` (null = inherit)
  - [x] `calculatedBedtimeMs: Long?` (auto-calculated, nullable for new alarms)
  - [x] `createdAt: Long` (epoch ms)

#### DAO Layer
- [x] Update `ScheduleDao.kt`:
  - [x] Add methods for UserPreferencesEntity CRUD (single row)
  - [x] Add query to get current preferences (`@Query("SELECT * FROM user_preferences LIMIT 1")`)

- [x] Create `CustomAlarmDao.kt`:
  - [x] `suspend fun insertCustomAlarm(alarm: CustomAlarmEntity)`
  - [x] `suspend fun updateCustomAlarm(alarm: CustomAlarmEntity)`
  - [x] `suspend fun deleteCustomAlarm(id: Long)`
  - [x] `@Query("SELECT * FROM custom_alarms") suspend fun getAllCustomAlarms(): List<CustomAlarmEntity>`
  - [x] `@Query("SELECT * FROM custom_alarms WHERE id = :id") suspend fun getCustomAlarmById(id: Long): CustomAlarmEntity?`

#### Database Migration
- [x] Update AppDatabase schema version to 4
- [x] Create migration for UserPreferences table (fallbackToDestructiveMigration)
- [x] Create migration for CustomAlarm table

### Repository Layer
- [x] Update `ScheduleRepository.kt`:
  - [x] Add method: `suspend fun getUserPreferences(): UserPreferencesEntity`
  - [x] Add method: `suspend fun saveUserPreferences(prefs: UserPreferencesEntity): Long`
  - [x] Add methods for CustomAlarm CRUD operations

### Domain Layer (Use Cases)
- [x] Create `GetUserPreferencesUseCase.kt`:
  - [x] Fetch current preferences from repository

- [x] Create `SaveUserPreferencesUseCase.kt`:
  - [x] Validate inputs (sleep duration > 0, wake-up time valid)
  - [x] Save to repository

- [x] Update `ScheduleAlarmsUseCase.kt`:
  - [x] Handle CustomAlarmEntity instead of ScheduleEntity for exceptions
  - [x] Implement inheritance logic: use preference values when alarm fields are null
  - [x] Auto-calculate bedtime on save if not provided

### UI/Presentation Layer

#### Main Screen (ScheduleScreen.kt)
- [x] Update `ScheduleScreen.kt`:
  - [x] **Header Area**:
    - Display target sleep duration from UserPreferences [✏️ icon]
    - Display wake-up time [✏️ icon]
    - Display errands duration [✏️ icon]
    - Click on pencil → open corresponding preference dialog (touch handlers wired, dialogs pending)
  - [x] **Alarm List**:
    - Show only CustomAlarms (no default alarm row)
    - Each row shows: name, enabled days, calculated times (using inherited values if needed)
    - Accordion layout with KeyboardArrowUp/Down icons for expand/collapse
  - [x] **FAB (Bottom-Right)**: "Add Custom Alarm" button

#### Clock-style Day Picker
- [x] Clock-style day picker component:
  - [x] Shows single letters (M, T, W, T, F, S, S)
  - [x] Rounded selection backgrounds for selected days
  - [x] Tap to toggle selection

#### Dialogs & Pickers
- [x] Create `SetupDialog.kt` with spinner pickers:
  - [x] Sleep duration picker with inline AlertDialog +/- buttons (4-12h range, 5m increments)
  - [x] Wake-up time using Android TimePickerDialog (native pattern, 7:00 AM default)
  - [x] Errands duration picker with inline AlertDialog +/- buttons (minutes only, max 120m)
  - [x] Time format selector in Settings screen

- [x] Create `AddCustomAlarmDialog.kt`:
  - [x] Name input field
  - [x] Day picker (clock-style with single letters)
  - [x] Target sleep duration input (hours/minutes instead of bedtime times)
  - [x] Wake-up time display (from UserPreferences, editable pending)
  - [x] Save/Delete buttons

#### ViewModel Updates
- [x] Create `PreferencesViewModel.kt`:
  - [x] StateFlow<UserPreferencesEntity?> for current preferences
  - [x] Method: `suspend fun loadUserPreferences()`
  - [x] Method: `suspend fun saveUserPreferences(entity: UserPreferencesEntity): Long`

- [x] Update `ScheduleViewModel.kt`:
  - [x] StateFlow<List<CustomAlarmUiModel>> for custom alarms list
  - [x] Method: `suspend fun addCustomAlarm(name: String, enabledDays: Set<Int>, ...)`
  - [x] Helper: `fun getEffectiveValues(alarm: CustomAlarmEntity): EffectiveAlarmValues` (resolves inherited nulls)

### Platform Layer
- [ ] Update `AlarmScheduler.kt`:
  - [ ] Schedule alarms for all enabled days based on calculated times
  - [ ] Handle both default and custom alarms with proper priority logic
  - [ ] Morning calculation: when wake-up alarm fires, calculate actual sleep duration if bedtime was tracked

---

## ✅ PHASE 2: INHERITANCE & DIALOG IMPLEMENTATION - COMPLETE

### Inheritance Resolution Logic
- [x] Create `EffectiveAlarmValues` data class:
  - [x] All fields resolved (no nulls after resolution)
  - [x] bedtime calculated from wake time minus sleep duration
  - [x] Helper method: `fromPreferences()` for default values

- [x] Create `ResolveAlarmValuesUseCase.kt`:
  - [x] Given a CustomAlarmEntity, resolve all null fields to UserPreferences values
  - [x] Return fully populated EffectiveAlarmValues object
  - [x] Calculate bedtime: `wakeUpTime - (resolvedSleepDuration + resolvedErrandsDuration)`
  - [x] Helper method: `resolveFromPreferences()` for custom alarm inheritance

### Dialog Implementation (Global Preferences)
- [x] Create `PreferenceDialog.kt` composable(s):
  - [x] Sleep duration picker with hour + minute inputs (`SleepDurationDialog`)
  - [x] Wake-up time picker using system locale format based on timeFormat24Hour setting (`WakeUpTimeDialog`)
  - [x] Errands duration picker (`ErrandsDurationDialog`)
  - [x] Time format selector (System/12h/24h) in Settings screen

- [x] Create `AddCustomAlarmDialog.kt`:
  - [x] Name input field
  - [x] Day picker (clock-style with single letters)
  - [x] Target sleep duration input with spinner pickers (hours/minutes)
  - [x] Wake-up time display (from UserPreferences, using TimeFormatter)
  - [x] Save/Delete buttons

### Validation & Bedtime Calculation
- [x] Calculate bedtime from target sleep duration and wake time when saving alarms
- [x] Ensure proper inheritance resolution for custom alarms via EffectiveAlarmValues

### Database Layer
- [x] Create `SleepSessionEntity.kt`:
  - [x] `id: Long` (primary key)
  - [x] `date: String` (ISO YYYY-MM-DD)
  - [x] `scheduledBedtimeMs: Long`
  - [x] `actualBedtimeMs: Long?` (when user went to bed, from manual input or screen-off detection)
  - [x] `wakeUpTimeMs: Long`
  - [x] `actualWakeTimeMs: Long?` (when alarm was dismissed)
  - [x] `estimatedSleepDurationMinutes: Int?`

### DAO Layer
- [x] Update AppDatabase schema version to 5 with SleepSessionEntity
- [x] Create `SleepSessionDao.kt`:
  - [x] `suspend fun insertSleepSession(session: SleepSessionEntity)`
  - [x] `@Query("SELECT * FROM sleep_sessions WHERE date = :date") suspend fun getSessionByDate(date: String): SleepSessionEntity?`
  - [x] `suspend fun updateSleepSession(session: SleepSessionEntity)`

### Repository Layer
- [x] Create `SleepSessionRepository.kt`:
  - [x] `suspend fun recordBedtime(dateTimeMs: Long): Long`
  - [x] `suspend fun getOrCreateTodaySession(date: String): SleepSessionEntity?`
  - [x] `suspend fun updateWakeUp(sessionId: Long, actualWakeTimeMs: Long): Boolean`

### Domain Layer (Use Cases)
- [x] Create `SleepSessionUseCases.kt`:
  - [x] `RecordBedtimeUseCase`: Record when user goes to bed
  - [x] `RecordWakeUpUseCase`: Calculate sleep duration from bedtime and wake time
  - [x] `GetLastNightSleepSessionUseCase`: Fetch previous night's session by date

### UI Layer
- [x] Update PreferencesViewModel.kt:
  - [x] Add `lastNightEstimatedSleepMinutes` field to UI model
  - [x] Method: `suspend fun recordBedtime()`
  - [x] Method: `suspend fun recordWakeUp()`
  - [x] Load last night's sleep data on view creation

- [x] Update Header in ScheduleScreen.kt:
  - [x] Add "Estimated Sleep" display showing last night's actual duration
  - [x] Show "No data yet" if no session recorded
  - [x] Highlight card with green color when sleep data available

### Manual Bedtime Input
- [x] Add "I'm Going to Bed" button/card:
  - [x] When tapped, store current time as `actualBedtimeMs` for today's session
  - [x] Confirmation dialog with immediate action on confirm

### Wake-Up Confirmation
- [x] Add "I Woke Up" confirmation button in ScheduleScreen header
  - [x] Shows when bedtime has been recorded
  - [x] Calculates sleep duration: `actualWakeTimeMs - actualBedtimeMs` (or scheduled if no manual)
  - [x] Stores wake time and updates session with calculated duration

---

## ⏳ PHASE 4: WAKE-UP ALARM & SLEEP CALCULATION

### Platform Layer (Alarm/Notification)
- [ ] Update wake-up notification in AlarmScheduler.kt:
  - [ ] Add "I Woke Up" button action
  - [ ] Create pending intent for wake-up confirmation
  - [ ] Include date and session info in intent extras

- [ ] Create `WakeUpReceiver.kt`:
  - [ ] Handle wake-up button click
  - [ ] Store actual wake time
  - [ ] Calculate sleep duration: `actualWakeTime - actualBedtime` (or scheduled bedtime if no manual input)
  - [ ] Update SleepSessionEntity with calculated duration

### UI Feedback
- [ ] Show confirmation snackbar when user wakes up: "You slept for X hours Y minutes"
- [ ] Display updated estimated sleep in header area

---

## ✅ PHASE 4: TIME FORMAT HANDLING - COMPLETE

### System Auto-Detection
- [x] In MainActivity or SettingsViewModel:
  - [x] Detect system locale using `ResourcesConfiguration.getLocales[0]`
  - [x] Check if locale uses 24-hour format via `DateFormat.is24HourFormat(context)`
  - [x] Set timeFormat24Hour in UserPreferences based on system setting

### Time Display Utilities
- [x] Create `TimeFormatter.kt`:
  - [x] Format time string based on timeFormat24Hour setting
  - [x] Reuse existing TimePickerDialog with appropriate flags
  - [x] Handle both epoch milliseconds and LocalTime conversions
  - [x] Add formatDuration() and formatDurationVerbose() helper methods

### UI Integration
- [x] Update ScheduleScreen.kt to use `TimeFormatter.formatTime()` for all time displays:
  - [x] Wake-up time in header (line 408)
  - [x] Wake-up time in AddCustomAlarmDialog (line 305)  
  - [x] Custom alarm wake-up times (lines 586, 591)

### Settings Enhancement
- [x] Create `SettingsScreen.kt` with time format selection:
  - [x] Three options: System Default, 12-hour, 24-hour
  - [x] Dialog with radio buttons for selection
- [x] Persist choice in UserPreferencesEntity.timeFormatPreference

### Domain Layer
- [x] Update PreferencesViewModel:
  - [x] Add `updateTimeFormat()` method for String-based format selection
  - [x] Add `loadPreferencesWithSystemDetection()` method with system locale detection on app launch

---

## ⏳ PHASE 5: TESTING & QUALITY ASSURATION (TODO)

### Unit Tests
- [ ] Test UserPreferences save/retrieve logic
- [ ] Test inheritance resolution (CustomAlarm → UserPreferences)
- [ ] Test bedtime calculation accuracy
- [ ] Test time format detection and formatting

### Instrumentation Tests
- [ ] Custom alarm creation with inherited values
- [ ] Header updates when preferences change
- [ ] Estimated sleep display after wake-up confirmation

---

## ⏳ PHASE 6: POLISH & DOCUMENTATION (TODO)

### Settings Screen Updates
- [ ] Add "Time Format" option (System/12h/24h) - overrides auto-detection
- [ ] Keep Dark/Light mode toggle
- [ ] Add "About" screen with version info

### README Updates
- [ ] Document new architecture: UserPreferences + CustomAlarms
- [ ] Explain inheritance model for custom alarms
- [ ] Update feature list: preference editing, clock-style picker, accordion layout

---

## Pre-Release Checklist

- [x] App builds without errors (`./gradlew assembleDebug`)
- [x] First launch shows global preferences at top (no onboarding flow)
- [x] Header displays target sleep, wake-up time (all editable with pencil icons)
- [x] Custom alarms list shows only exception alarms
- [x] Clock-style day picker works correctly
- [x] Accordion layout for custom alarms functional
- [ ] Clickable preference items open edit dialogs (handlers wired, dialogs need implementation)
- [x] Estimated sleep displays after wake-up confirmation

---

## Post-MVP Considerations

These are NOT part of MVP but worth noting for later:
- [ ] Background screen-off detection for automatic bedtime tracking
- [ ] Sleep quality estimation based on consistency over weeks
- [ ] Advanced analytics dashboard with trends
- [ ] Multiple custom alarm templates (Weekday, Weekend, etc.)
- [ ] Cloud sync for preferences and schedules
