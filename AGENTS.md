# Sleep‑Alarm Project – Agent Architecture

This repository implements a sleep‑alarm Android application written in Kotlin. The code is organized around the **OpenAI Agent** framework, which allows us to delegate complex tasks (like research or bulk code generation) to specialized agents.

## Project Overview
- **Purpose:** Help users schedule bedtime, wake‑up, and errands notifications on a per‑weekday basis.
- **Key Components:**
  - *Data Layer* – Room database for schedules.
  - *Domain/Use‑case* – Business logic for alarm scheduling.
  - *Presentation* – Jetpack Compose UI.
  - *Platform Services* – AlarmManager / WorkManager notifications.

## Agents in Use
| Agent | Role in the project | Typical Tasks |
|-------|---------------------|---------------|
| `general` | Handles research, dependency updates, and complex reasoning. | Fetching latest docs for Room/AlarmManager, generating boilerplate code. |
| `explore` | Quickly scans the source tree for patterns or missing files. | Finding all schedule‑related classes, locating potential bugs. |
| `testing` | Automates test generation and execution. | Creates unit/instrumentation tests, runs them, reports results. |
| `code-review` | Performs static analysis & code review. | Checks coding standards, suggests improvements, flags issues. |
| `documentation` | Generates project docs. | Builds Javadoc/KDoc, updates README/CHANGELOG.|

> **Future extensions:** As the project grows we plan to add agents such as `testing`, `code-review`, and `documentation`. Each would be registered in this file once implemented.

## Invoking an Agent
Agents are launched via the Task tool:
```json
{
  "description": "Describe what you want",
  "prompt": "/some-command ...",
  "subagent_type": "general"
}
```
The system will return a single message containing the result. No further user interaction is required.

## Materials

### UI Components & Design System

#### Material Colors (Material You / Dynamic Color)
- **Primary**: Mint green (#4DD0E1 / #69F0AE variant) - used for primary actions, active states
- **On Primary**: Dark text for contrast on mint backgrounds
- **Surface**: Deep navy blue (#0D1B2A / #1B263B variants) - card backgrounds, main surface
- **Background**: Very dark navy (#010F1C) - app background
- **Outline**: Light gray/white with low opacity for borders and dividers

#### Material Components Used
- **Cards**: Elevated cards with rounded corners (16dp radius), subtle elevation
  - Global Preferences: Surface container low, edit icon on right
  - Last night summary: Green accent border (#4DD0E1), chevron arrow for navigation
- **Buttons**: 
  - Primary gradient buttons (mint green gradient) - "I'M GOING TO BED"
  - Secondary filled buttons (dark surface variant #1B263B) - "I WOKE UP"
  - TextButton: Clear All button with primary color text
  - FAB: Floating action button, mint green with + icon
- **Typography**: 
  - Headings: ExtraBold weight for section titles ("Custom Schedules")
  - Subtitles: LabelSmall (10sp) for secondary labels ("OVERRIDE SCHEDULE", "GLOBAL OPTIMIZATION")
  - Body text: Regular weight, good contrast on dark background
  - Numbers/Time: ExtraBold weight, large sizes (24sp headlineSmall)
- **Icons**: Outlined icons (Material Icons), white/light gray on dark backgrounds
  - Settings/Help icon in TopAppBar trailing position
  - Edit pencil icons for preference cards
  - Contextual icons: Bedtime/Moon, WbSunny/Sun, Bolt/Lightning, Analytics/Chart
- **Toggle Switches**: Material3 switch with mint primary color (per-alarm enable/disable)
- **Bottom Navigation**: Fixed bottom nav with 3 items (Schedule, Statistics, Settings)

#### Layout Patterns
- **Section Headers**: 
  - "GLOBAL OPTIMIZATION": Uppercase LabelSmall, onSurfaceVariant color
  - "Custom Schedules": TitleLarge ExtraBold weight, lowercase title case
  - Subtitles: LabelSmall below titles ("OVERRIDE SCHEDULE")
- **Preference Cards**: Full-width cards with icons on left, edit icon on right
  - Target Sleep card: Bedtime icon (mint green)
  - Wake Up card: WbSunny icon (yellow/secondary)
  - Errands Buffer card: Bolt icon (tertiary)
- **Last Night Summary**: Card with green accent border, duration + discipline %, chevron arrow
- **Day Pickers**: Circular day-of-week selectors (M T W T F S S) below custom alarms
  - Active days: Mint green background with white text
  - Inactive days: Surface variant background with onSurfaceVariant text
- **Action Buttons**: Gradient primary + secondary filled buttons stacked vertically

### Screen Layout Specifications

#### ScheduleScreen Structure
```
┌─────────────────────────────┐
│ 🌙 SleepChad         [⚙️] │  <- TopAppBar: moon logo + title, settings icon
├─────────────────────────────┤
│ GLOBAL OPTIMIZATION          │  <- Section header (LabelSmall)
├─────────────────────────────┤
│ ┌───────────────────────────┐│
│ │ 🌙 Target Sleep           ││
│ │   8h 00m            [✏️] ││
│ ├───────────────────────────┤│
│ │ ☀️ Wake Up                ││
│ │   07:00             [✏️] ││
│ ├───────────────────────────┤│
│ │ ⚡ Errands Buffer         ││
│ │   30m              [✏️] ││
│ └───────────────────────────┘│
├─────────────────────────────┤
│ ┌───────────────────────────┐│
│ │ 📊 Last night: 7h 45m  >  ││
│ │    94% of daily target     ││
│ └───────────────────────────┘│  <- Green accent border
├─────────────────────────────┤
│ [I'M GOING TO BED]           │  <- Gradient mint green button
│ [I WOKE UP]                  │  <- Secondary filled button (dark)
├─────────────────────────────┤
│ Custom Schedules      CLEAR ALL│
│ OVERRIDE SCHEDULE             │  <- Subtitle below title
├─────────────────────────────┤
│ ┌───────────────────────────┐│
│ │ ⏰ 06:15        [ON/OFF] ▼││
│ │ GYM RITUAL • M W F         ││
│ │ ● ○ ● ○ ● ○ ○             ││
│ │ [SNOOZE: 5M] [VIBRATE]    ││
│ └───────────────────────────┘│
│ ┌───────────────────────────┐│
│ │ ⏰ 09:00        [ON/OFF] ▼││
│ │ WEEKEND RECOVERY • S S     ││
│ │ ○ ● ○ ○ ○ ● ●             ││
│ │ [SNOOZE: 5M] [VIBRATE]    ││
│ └───────────────────────────┘│
├─────────────────────────────┤
│            [FAB +]           │
├─────────────────────────────┤
│ 📅 Schedule | 📊 Stats | ⚙️ Settings │
└─────────────────────────────┘
```

### Implementation Status
**ScheduleScreen Refactoring - Phase 2: UI Enhancement** ✅ COMPLETE

| Component | Status | Notes |
|-----------|--------|-------|
| Global Preferences cards with contextual icons | ✅ DONE | TargetSleepCard, WakeUpTimeCard, ErrandsDurationCard added |
| Last night summary card with green accent border | ✅ DONE | Added navigation chevron and discipline percentage display |
| Quick action buttons (Going to bed / Woke up) | ✅ DONE | Implemented gradient primary + secondary filled buttons |
| Custom Schedules section header layout | ✅ DONE | TitleLarge ExtraBold + LabelSmall subtitle + CLEAR ALL TextButton |
| Per-alarm toggle switches | ✅ DONE | Card-based toggle in accordion cards |
| Day pickers with active/inactive states | ✅ DONE | Circular M T W T F S S buttons with mint green highlight |
| Snooze/vibrate option buttons in cards | ✅ DONE | Added as TextButton components in CustomAlarmAccordion |
| Header logo and settings icon | ✅ DONE | Implemented via SleepChadTopBar |

**Recent Fixes (March 25, 2026):**
- Fixed deprecated icon references (`Icons.Filled.ArrowBack`, `TrendingUp`)
- Replaced missing `StickyNote2` icon with `TrendingUp` for statistics screens  
- Cleaned up unused variables and parameters in ScreenWrappers.kt
- Consolidated duplicate extension functions into `/utils/ExtensionFunctions.kt`
- Fixed StatisticsViewModel type mismatch issues (nullable Int? handling)
- Fixed SleepSessionRepository Flow type mismatch by using `.first()` collector
- Build passes successfully: `./gradlew :app:assembleDebug` ✅

**New Features (March 25, 2026):**
- Added GlobalAlarmToggleCard component for app-wide alarm enable/disable toggle
- Implemented WakeUpReceiver to handle wake-up notifications and session tracking
- Created WakeUpActionActivity for "I Woke Up" button action processing
- Enhanced CustomAlarmUiModel with inheritance flags (inheritsTargetSleep, inheritsWakeTime) and illogical bedtime detection
- Added 📥 visual indicator in CustomAlarmAccordion for inherited values
- Warning message display when calculated bedtime is illogical (bedtime > wake time)
- Snackbar support integrated into ScheduleScreenWrapper with success message handling

**Code Quality Improvements:**
- Removed duplicate `toDaysString()` and `toDaysSet()` extension functions from ScheduleEntity, CustomAlarmEntity, AlarmScheduler.kt, SaveScheduleUseCase.kt
- Created centralized `/com/zawaro/sleepchad/utils/ExtensionFunctions.kt` for shared utility methods
- Fixed nullable UserPreferencesEntity access with safe call operator (?.) in StatisticsViewModel

## Extending Agents
If new functionality requires dedicated logic (e.g., automated tests), create a new agent type, implement its handler, and add it to the dispatch table. The `AGENTS.md` file should be updated accordingly so teammates know which agents are available.
