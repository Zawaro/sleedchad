# SleepChad (aka "Sleep in Time")

A simple Android application that lets users schedule bedtime, wake‑up, and evening‑errand reminders on a per‑weekday basis.

---

## Features
- **Room** database stores a `ScheduleEntity` for each weekday (1–7). The entity contains:
  * Bedtime epoch milliseconds
  * Wake‑up epoch milliseconds
  * Minutes before bedtime to start errands
- **AlarmManager** + **WorkManager** are used to schedule three alarms per day:
  - Evening errands reminder
  - Bedtime notification
  - Morning wake‑up alarm
- **Jetpack Compose UI** with a single screen that lists all seven days. Each row contains:
  * Time picker for bedtime
  * Time picker for wake‑up
  * Slider to set errand minutes
  * “Save & Schedule” button persists the schedule and reschedules alarms.
- **Notifications** use three notification channels (`errands`, `bedtime`, `wake`).
- The app automatically re‑schedules all alarms after a device reboot.

---

## Architecture Overview
```
+---------------------+          +--------------------+
|  Presentation Layer | <------> |  Domain / Use‑Case |
|  (Compose UI)       |          |  (Get/Save/Schedule) |
+----------+----------+          +---------+----------+
           |                               |
           v                               v
      ScheduleRepository <---> AppDatabase (Room)
```
- **Data Layer** – `ScheduleEntity`, DAO, Repository, Room DB.
- **Domain Layer** – Three use‑case classes that expose a simple API.
- **Presentation Layer** – `ScheduleViewModel` + Compose UI.

---

## Getting Started
1. **Clone** the repo (or copy the project files into your workspace).
2. **Open** in Android Studio (the project uses Gradle Kotlin DSL).
3. **Build & Run**:
    ```bash
    ./gradlew assembleDebug
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    ```
4. On the device or emulator, open the app and set your schedules.
5. The alarms will fire at the configured times – you should see notifications in the status bar.

## Running Tests
- **Unit tests** (JVM only):
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Instrumentation / UI tests** (requires an emulator or connected device):
  ```bash
  ./gradlew connectedAndroidTest
  ```

---

## Dependencies
- Kotlin 1.9+ (JVM 17)
- AndroidX AppCompat 1.6.1
- AndroidX Core KTX 1.12.0
- Material Design 1.9.0
- Jetpack Compose 1.5.2 (Material3)
- Room 2.5.2
- WorkManager 2.9.0
- Coroutines 1.8.0

---

## Project Structure
```
app/src/main/java/com/zawaro/sleepchad/
├── data/                # Room entities, DAO & repository
├── domain/usecases/      # GetScheduleUseCase, SaveScheduleUseCase, ScheduleAlarmsUseCase
├── platform/alarm/       # AlarmReceiver, BootReceiver, AlarmScheduler
├── presentation/schedule/# Compose UI + ViewModel
└── ui/theme/             # Material3 theme
```
---

## Contributing
Feel free to fork the project, add features or fix bugs. Pull requests are welcome! Please keep your changes in line with the existing architecture.

---

## License
MIT – see `LICENSE`.
