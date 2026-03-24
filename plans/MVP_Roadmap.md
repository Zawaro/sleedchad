# SleepChad MVP Roadmap - Current Implementation Status

## Executive Summary
This document outlines the current development status and remaining work for transforming the sleep-alarm foundation into a complete, user-ready MVP with **UserPreferences-based configuration** and **CustomAlarm inheritance model**.

---

## 🎯 CURRENT ARCHITECTURE OVERVIEW

### Core Concepts Implemented:
1. **UserPreferencesEntity**: Global defaults stored once separately from alarms
   - Target sleep duration (hours + minutes)
   - Wake-up time  
   - Evening errands duration
   - Time format preference (system auto-detect by default)

2. **CustomAlarmEntity**: Exception alarms that inherit from UserPreferences
   - Enable days (Mon-Sun selection with clock-style picker)
   - Custom name field
   - Nullable fields: null = inherit, set value = override
   - Auto-calculated bedtime based on resolved values

3. **Header-First UI Design**:
   - Top section shows global preferences (target sleep, wake time, errands duration)
   - All header items clickable with pencil icon for inline editing (handlers wired, dialogs pending)
   - No default alarm row visible to user

4. **Custom Alarms List**: Only exception/custom alarms shown below header
   - Accordion layout with expand/collapse functionality using KeyboardArrowUp/Down icons
   - Clock-style day picker with single letters (M, T, W, T, F, S, S) and rounded selection backgrounds
   - FAB button bottom-right to add custom alarms

---

## ✅ PHASE 1: PREFERENCES & DEFAULT ALARM SYSTEM - COMPLETE

### Features Implemented:
1. **Database Layer**:
   - UserPreferencesEntity with all required fields (id, targetSleepHours/Minutes, wakeUpTimeMs, errandsDurationHours/Minutes, timeFormat24Hour)
   - CustomAlarmEntity with inheritance model (nullable fields for inherited values)
   - AppDatabase schema version 4 with migrations for both tables

2. **DAO Layer**:
   - ScheduleDao.kt updated with UserPreferences CRUD methods
   - CustomAlarmDao.kt created with full CRUD operations

3. **Repository Layer**:
   - ScheduleRepository.kt updated with getUserPreferences() and saveUserPreferences() methods
   - CustomAlarm CRUD methods implemented

4. **Domain Layer**:
   - GetUserPreferencesUseCase.kt - fetches current preferences
   - SaveUserPreferencesUseCase.kt - validates and saves preferences
   - ScheduleAlarmsUseCase.kt - handles inheritance resolution for custom alarms

5. **UI/Presentation Layer**:
   - PreferencesViewModel.kt with StateFlow<UserPreferencesEntity?> for current preferences
   - ScheduleScreen.kt updated with:
     - Header area showing target sleep, wake-up time, errands duration (all clickable)
     - Accordion layout for custom alarms list
     - Clock-style day picker component
     - FAB button bottom-right ("Add Custom Alarm")
   - AddCustomAlarmDialog.kt created with:
     - Name input field
     - Clock-style day picker
     - Target sleep duration input (hours/minutes)
     - Save/Delete buttons

### What's Working:
- ✅ App builds without errors
- ✅ Global preferences displayed at top with clickable touch targets (handlers wired)
- ✅ Accordion layout functional with expand/collapse icons
- ✅ Clock-style day picker renders correctly
- ✅ Custom alarms list shows only exception alarms
- ✅ Inheritance model implemented in domain layer

### What Needs Implementation:
- ⏳ Edit dialogs for global preferences (clickable handlers currently empty)
  - Target sleep duration dialog
  - Wake-up time dialog  
  - Errands duration dialog
- ⏳ Hour/minute spinner pickers in AddCustomAlarmDialog (currently just displays values)
- ⏳ Actual alarm creation with calculated bedtime based on wake time and duration

---

## ✅ PHASE 2: INHERITANCE RESOLUTION & DIALOG IMPLEMENTATION (COMPLETE)

### Features Implemented:
1. **Inheritance Resolution Logic**:
    - Created `EffectiveAlarmValues` data class to hold resolved values with all nulls resolved
    - Created `ResolveAlarmValuesUseCase.kt` to resolve CustomAlarmEntity fields to UserPreferences defaults
    - Helper methods: `fromPreferences()` for default values and `resolveFromPreferences()` for custom alarm inheritance
    - Bedtime calculated from wake time minus sleep duration when saving alarms

2. **Dialog Implementation**:
    - Created SetupDialog.kt with inline AlertDialog spinner pickers:
      - Target sleep duration: Hours + minutes with +/- buttons opening spinner dialogs (4-12h range, 5m increments)
      - Wake-up time: Android TimePickerDialog (native pattern, 7:00 AM default)
      - Errands duration: Minutes only with +/- button opening spinner dialog (5-minute increments, max 120m)
    - Wire up hour/minute spinner pickers in AddCustomAlarmDialog (inline AlertDialog-based pickers)
    - Calculate and store calculatedBedtimeMs when creating custom alarms

3. **Validation**:
    - Sleep duration validated to 4-12 hours range
    - Errand duration limited to 0-120 minutes with 5-minute increments
    - Bedtime automatically calculated from wake time minus sleep duration

### Technical Implementation:
- ✅ Created `EffectiveAlarmValues.kt` with companion object helper methods
- ✅ Created `ResolveAlarmValuesUseCase.kt` for inheritance resolution logic
- ✅ All three preference edit dialogs are fully functional and wired up in ScheduleScreen
- ✅ AddCustomAlarmDialog has working hour/minute spinner pickers with visual selection indicators

---

## ✅ PHASE 3: ESTIMATED SLEEP TRACKING (COMPLETE)

### Features Implemented:
1. **Manual Bedtime Input**: "I'm Going to Bed" button in header/card
   - When tapped, stores current time as actualBedtimeMs for today's session
   - Confirmation dialog with immediate action on confirm
   - Updates UI state immediately after recording

2. **Wake-Up Confirmation**: Button in ScheduleScreen header
   - "I Woke Up" confirmation button shows when bedtime recorded
   - Calculates sleep duration: `actualWakeTimeMs - actualBedtimeMs` (or scheduled if no manual)
   - Shows snackbar: "You slept for X hours Y minutes" on success

3. **Estimated Sleep Display in Header**:
   - Shows last night's actual duration (if available)
   - Displays "No data yet" if no session recorded
   - Updates automatically after wake-up confirmation
   - Highlighted green card when sleep data is present

### Technical Implementation:
- ✅ Created SleepSessionEntity.kt with all required fields (id, date, scheduledBedtimeMs, actualBedtimeMs?, wakeUpTimeMs, actualWakeTimeMs?, estimatedSleepDurationMinutes?)
- ✅ Created SleepSessionDao.kt with insert, query by date, and update methods
- ✅ Created SleepSessionRepository.kt following repository pattern with coroutine dispatching
- ✅ Updated AppDatabase schema version to 5 with SleepSessionEntity migration
- ✅ Created SleepSessionUseCases.kt: RecordBedtimeUseCase, RecordWakeUpUseCase, GetLastNightSleepSessionUseCase
- ✅ Updated PreferencesViewModel.kt with recordBedtime(), recordWakeUp() methods and lastNightEstimatedSleepMinutes state
- ✅ Updated ScheduleScreen.kt with estimated sleep display and bedtime/wake-up buttons

---

## ✅ PHASE 4: TIME FORMAT HANDLING (COMPLETE)

### Features Implemented:
1. **System Auto-Detection**:
   - Detect system locale using `ResourcesConfiguration.getLocales[0]` or `LocaleList.getDefault()[0]`
   - Check if locale uses 24-hour format via `DateFormat.is24HourFormat(context)`
   - Set timeFormat24Hour in UserPreferences based on system setting on app launch

2. **Time Display Utilities**:
   - Created `TimeFormatter.kt` to format time strings based on timeFormat24Hour setting
   - Added helper methods: `formatDuration()`, `formatDurationVerbose()` for duration display
   - Handle both epoch milliseconds and LocalTime conversions

3. **Settings Enhancement**:
   - Created `SettingsScreen.kt` with time format selection dialog
   - Three options available: System Default, 12-hour (AM/PM), 24-hour
   - Persist choice in UserPreferencesEntity.timeFormatPreference

### Implementation Details:
- ✅ Updated ScheduleScreen.kt to use `TimeFormatter.formatTime()` for all wake-up time displays
- ✅ PreferencesViewModel updated with `updateTimeFormat()` and `loadPreferencesWithSystemDetection()` methods
- ✅ All time displays throughout the app now respect user's selected format preference

---

## ⏳ PHASE 5: TESTING & QUALITY ASSURANCE (TODO)

### Unit Tests Needed:
- Test UserPreferences save/retrieve logic
- Test inheritance resolution (CustomAlarm → UserPreferences)
- Test bedtime calculation accuracy
- Test time format detection and formatting

### Instrumentation Tests Needed:
- Custom alarm creation with inherited values
- Header updates when preferences change
- Clock-style day picker selection behavior
- Accordion expand/collapse functionality
- Estimated sleep display after wake-up confirmation

---

## ⏳ PHASE 6: POLISH & DOCUMENTATION (TODO)

### UI Polish:
- Add visual indicators for inherited values in CustomAlarmDialog (📥 icon or grayed text)
- Warning message when calculated bedtime is illogical (e.g., bedtime after wake time next day)
- Snackbar/toast feedback on successful save/delete operations
- Empty state message when no custom alarms exist

### Documentation:
- Update README with new architecture: UserPreferences + CustomAlarms inheritance model
- Explain clock-style day picker and accordion layout design decisions
- Add user guide explaining inheritance model (null fields = inherit from defaults)
- Document time format auto-detection behavior

---

## Current Folder Structure

```
app/src/main/java/com/zawaro/sleepchad/
├── core/                      
│   └── NotificationHelper.kt  # Notification channels & helpers
├── data/
│   ├── dao/                  
│   │   ├── ScheduleDao.kt             # Updated with UserPreferences CRUD
│   │   ├── CustomAlarmDao.kt          # NEW: Custom alarm operations
│   │   ├── SleepSessionDao.kt         # NEW: sleep session CRUD (schema v5)
│   │   └── ErrandDao.kt               # Existing: errand operations
│   ├── repository/           
│   │   ├── ScheduleRepository.kt      # Updated with preferences + custom alarms
│   │   ├── SleepSessionRepository.kt  # NEW: sleep session repository (schema v5)
│   │   └── ErrandRepository.kt        # Existing: errand-specific operations
│   ├── entity/               
│   │   ├── UserPreferencesEntity.kt   # NEW: Global defaults (single row)
│   │   ├── CustomAlarmEntity.kt       # NEW: Exception alarms with inheritance
│   │   ├── SleepSessionEntity.kt      # NEW: sleep tracking sessions (schema v5)
│   │   └── ErrandEntity.kt            # Existing: errands per alarm
│   └── AppDatabase.kt               # Schema version 5 migration
├── domain/
│   ├── usecases/             
│   │   ├── GetUserPreferencesUseCase.kt    # NEW: Fetch current preferences
│   │   ├── SaveUserPreferencesUseCase.kt   # NEW: Save + validate preferences
│   │   ├── ScheduleAlarmsUseCase.kt        # Updated with inheritance support
│   │   └── SleepSessionUseCases.kt         # NEW: bedtime/wake-up tracking (schema v5)
├── presentation/             
│   ├── schedule/             
│   │   ├── ScheduleScreen.kt             # Updated: header + accordion layout + sleep display
│   │   ├── ScheduleViewModel.kt          # Updated with preferences state
│   │   └── AddCustomAlarmDialog.kt       # NEW: Custom alarm creation dialog
│   └── settings/             
│       ├── PreferencesViewModel.kt       # NEW: Global preferences management + sleep tracking methods
│       └── SettingsViewModel.kt          # Existing: theme & settings
├── platform/alarm/           
│   ├── AlarmReceiver.kt      # Handles alarm broadcast intent
│   ├── BootReceiver.kt       # Reschedules alarms after reboot
│   └── AlarmScheduler.kt     # Existing: schedules via AlarmManager
└── ui/theme/                   # Material3 theming

plans/
├── MVP_Checklist.md            # Feature implementation checklist (updated)
└── MVP_Roadmap.md              # This file - phased development plan
```

---

## Success Metrics (MVP Definition - Current Status)

**Completed ✅:**
- ✅ App builds without errors (`./gradlew assembleDebug`)
- ✅ UserPreferencesEntity + CustomAlarmEntity created with inheritance model
- ✅ Database schema updated to version 5 with migrations
- ✅ DAO and Repository layers implemented for all entities including SleepSession
- ✅ Domain layer use cases for preferences management, sleep tracking, and alarm resolution
- ✅ Header displays global preferences (target sleep, wake time, errands duration)
- ✅ Clock-style day picker component implemented
- ✅ Accordion layout for custom alarms functional
- ✅ FAB button opens AddCustomAlarmDialog
- ✅ Inheritance resolution logic in domain layer (`EffectiveAlarmValues`, `ResolveAlarmValuesUseCase`)
- ✅ Sleep tracking database layer (Entity + DAO + Repository) created
- ✅ RecordBedtimeUseCase and RecordWakeUpUseCase implemented
- ✅ PreferencesViewModel has sleep tracking methods (recordBedtime, recordWakeUp)
- ✅ ScheduleScreen displays estimated sleep from previous night
- ✅ "I'm Going to Bed" / "I Woke Up" buttons with confirmation dialog
- ✅ Edit dialogs for global preferences fully implemented and wired up
- ✅ Hour/minute spinner pickers in AddCustomAlarmDialog (inline AlertDialog-based)
- ✅ Alarm creation with calculated bedtime based on duration

**In Progress ⏳:**
- 📋 Visual indicators for inherited values in alarm display
- 📋 Empty state handling for custom alarms list

**Not Started 📋:**
- 📋 Comprehensive test coverage (Phase 5)
- 📋 UI polish and visual enhancements (Phase 6)

---

## Implementation Priority Order

1. **Critical (Week 1)**:
   - ✅ UserPreferencesEntity + DAO + Repository (COMPLETE)
   - ✅ CustomAlarmEntity + inheritance model (COMPLETE)
   - ✅ ScheduleScreen header with clickable preferences (handlers wired)
   - ✅ Clock-style day picker (COMPLETE)
   - ✅ Accordion layout for alarms (COMPLETE)

2. **High Priority (Week 2)**:
   - ✅ Sleep tracking database layer (Entity + DAO + Repository) (COMPLETE)
   - ✅ Sleep tracking use cases implemented (COMPLETE)
   - ✅ PreferencesViewModel sleep tracking methods (COMPLETE)
   - ✅ Time format handling and auto-detection (COMPLETE)
   - ✅ EffectiveAlarmValues resolution logic (COMPLETE)
   - ✅ Dialog implementation for global preferences (COMPLETE)
   - ✅ Hour/minute spinner pickers in AddCustomAlarmDialog (COMPLETE)

3. **Medium Priority (Week 3-4)**:
   - 📋 Comprehensive test coverage and QA
   - 📋 Empty state handling for custom alarms list
   - 📋 Visual indicators for inherited values in alarm display

4. **Low Priority (Week 5+)**:
   - 📋 Background screen-off detection (optional)
   - 📋 Sleep trend charts/analytics
   - 📋 Documentation updates

---

## Next Steps

1. **Immediate**: Add visual indicators for inherited values in CustomAlarm display (📥 icon or grayed text)
2. **Short-term**: Implement empty state handling when no custom alarms exist
3. **Medium-term**: Comprehensive test coverage and quality assurance
4. **Long-term**: Background screen-off detection, sleep trend analytics, documentation updates

Ready to continue implementation? Let's start with implementing the preference edit dialogs!
