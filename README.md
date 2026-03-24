# SleepChad (aka "Sleep in Time")

A simple Android application that lets users schedule bedtime, wake‑up, and evening‑errand reminders on a per‑weekday basis.

---

## Features
### Core Alarm System (Phase 1 - Completed)
- **Room** database with default + exception alarm support:
  * Default alarm applies to all days except those covered by exceptions
  * Exception alarms override default on specific weekdays
  * Automatic day exclusion prevents conflicts
- **Default & Exception Alarms**: Toggle between daily schedule and per-day overrides
- **Day Picker**: Multi-select checkboxes with conflict detection for overlapping days
- **Errands Support**: Multiple errands per alarm with emoji icons (e.g., "💊 Meds", "🍽️ Dinner")
- **Disable Alarms**: Quick toggle to silence all notifications for 1–24 hours
- **Jetpack Compose UI** with Material3 design
- **Notifications** use three channels (`errands`, `bedtime`, `wake`)
- **Auto-restart**: Alarms re-schedule after device reboot via BootReceiver

### Upcoming Features (Phase 2+)
See [`plans/MVP_Checklist.md`](./plans/MVP_Checklist.md) for full roadmap.
- Dashboard with countdown to next alarm
- Wake-up snooze feature (3-tap limit, +5 min increments)
- Screen-on sleep tracking estimation
- Foreground service for improved alarm reliability

---

## Architecture Overview
### Clean Architecture (MVVM)
```
+---------------------+          +--------------------+
|  Presentation Layer | <------> |  Domain / Use‑Case |
|  (Compose UI)       |          |  (Get/Save/Schedule) |
+----------+----------+          +---------+----------+
             |                               |
             v                               v
        ScheduleRepository <---> AppDatabase (Room)
```

### Project Structure
```
app/src/main/java/com/zawaro/sleepchad/
├── core/                      # Utilities & helpers
│   └── NotificationHelper.kt  # Notification channels & helpers
├── data/                      # Room database layer
│   ├── entity/               # Entities (ScheduleEntity, ErrandEntity)
│   ├── dao/                  # DAOs (ScheduleDao, ErrandDao)
│   ├── AppDatabase.kt        # Room database definition
│   └── ScheduleRepository.kt # Repository with CRUD operations
├── domain/usecases/           # Business logic layer
│   ├── GetScheduleUseCase.kt  # Retrieve schedules from DB
│   ├── SaveScheduleUseCase.kt # Persist schedules to DB
│   └── ScheduleAlarmsUseCase.kt # Trigger scheduling logic
├── platform/alarm/            # Android alarm services
│   ├── AlarmReceiver.kt       # Handles alarm broadcast intent
│   ├── BootReceiver.kt        # Reschedules alarms after reboot
│   └── AlarmScheduler.kt      # Schedules alarms via AlarmManager
├── presentation/              # UI layer (Jetpack Compose)
│   ├── schedule/             # Main schedule screen & ViewModel
│   │   ├── ScheduleScreen.kt  # Composable with alarm rows & dialogs
│   │   └── ScheduleViewModel.kt
│   ├── settings/             # Settings screen
│   │   └── SettingsViewModel.kt
│   └── ui/components/         # Reusable UI components
├── util/                      # Utility classes
│   └── Converters.kt          # Room type converters
└── ui/theme/                  # Material3 theming
```

### Key Components
- **Data Layer**: `ScheduleEntity` (with `isDefaultAlarm`, `enabledDays`), `ErrandEntity`, DAOs, Room database.
- **Domain Layer**: Use-cases for schedule management, alarm scheduling logic, and exception handling.
- **Presentation Layer**: Jetpack Compose UI with ViewModel + state hoisting pattern.
- **Platform Services**: AlarmManager for precise timing, BootReceiver for persistence across reboots.

---

## Getting Started
1. **Clone** the repo:
     ```bash
     git clone <repository-url>
     cd sleepchad
     ```
2. **Open** in Android Studio (Gradle Kotlin DSL).
3. **Build & Run**:
     ```bash
     ./gradlew assembleDebug
     adb install -r app/build/outputs/apk/debug/app-debug.apk
     ```
4. On device/emulator, open the app and set schedules.
5. Alarms fire at configured times with status bar notifications.

---

## Development Commands

### Build & Run
```bash
./gradlew assembleDebug          # Build debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk  # Install on device/emulator
```

### Testing
```bash
./gradlew testDebugUnitTest           # Unit tests (JVM)
./gradlew connectedAndroidTest        # Instrumentation/UI tests (requires device)
```

### Code Quality
```bash
./gradlew ktlintCheck      # Check for style violations
./gradlew ktlintFormat     # Auto-fix fixable issues
```

---

## Dependencies
- Kotlin 1.9+ (JVM 17)
- AndroidX AppCompat 1.6.1
- AndroidX Core KTX 1.12.0
- Material Design 3 (Material Components 1.9.0)
- Jetpack Compose 1.5.4 (Compose BOM 2024.02.00)
- Room Database 2.5.2
- WorkManager 2.9.0
- Coroutines 1.8.0
- Lifecycle Runtime & ViewModel 2.6.2

---

## Contributing
Feel free to fork the project and submit pull requests. Keep changes aligned with existing architecture and code style (ktlint).

---

## License
MIT – see `LICENSE`.
