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

## Extending Agents
If new functionality requires dedicated logic (e.g., automated tests), create a new agent type, implement its handler, and add it to the dispatch table. The `AGENTS.md` file should be updated accordingly so teammates know which agents are available.
