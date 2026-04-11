# Expense Tracker

A production-grade personal finance Android app with offline-first persistence, ML Kit receipt scanning, Gemini AI insights, and a fully modular multi-module architecture.

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![API](https://img.shields.io/badge/API-26%2B-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-purple)

---

## Features

- **Expense Logging** — Manual entry with category, date, amount, and notes
- **Receipt Scanning** — Google ML Kit OCR auto-fills expense fields from a photo
- **AI Insights** — Spend forecasting, anomaly detection, and natural language entry via Gemini API
- **On-device ML** — Smart category suggestions powered by TensorFlow Lite
- **Budget Tracking** — Per-category monthly budgets with progress indicators and overspend alerts
- **Analytics Dashboard** — Canvas-rendered charts, period-filtered trends, and category breakdowns
- **Offline First** — Full Room persistence; every feature works without a network connection
- **Settings** — DataStore-backed currency, theme, and notification preferences with CSV export

---

## Architecture

MVVM + Unidirectional Data Flow (UDF) is enforced across every feature module.

```
UI (Composable)
  │  observes StateFlow<UiState>
  ▼
ViewModel
  │  calls
  ▼
UseCase  ─────►  Repository (interface)
                      │
                      ▼
                 RepositoryImpl         (core:data)
                      │
                 ┌────┴────┐
                 DAO      API
              (Room)   (Retrofit)
```

**State management contract:**
- One `UiState` data class per screen — collected via `collectAsStateWithLifecycle()`
- One `UiEvent` sealed class for one-shot events (navigation, snackbars)
- All screen composables are stateless; only the `*Screen` composable holds the `ViewModel`

---

## Module Structure

```
ExpenseTracker/
├── app/                        ← Entry point, NavHost, DI root
├── build-logic/convention/     ← Gradle convention plugins
├── core/
│   ├── common/                 ← Result<T>, Kotlin extensions, utilities
│   ├── data/                   ← Room DAOs, Retrofit services, Repository impls
│   ├── domain/                 ← Domain models, Repository interfaces, Use Cases
│   └── ui/                     ← Material 3 theme, Dimens, shared components
└── feature/
    ├── dashboard/              ← Home: monthly summary + recent expenses
    ├── add-expense/            ← Manual expense entry form
    ├── analytics/              ← Charts, trends, period filtering (Canvas API)
    ├── ocr/                    ← CameraX + ML Kit receipt scanning
    ├── budgets/                ← Budget CRUD + progress tracking
    └── settings/               ← DataStore preferences, currency picker, CSV export
```

**Dependency rules (strictly enforced):**

| Direction | Allowed |
|---|---|
| `:feature:*` | → `:core:domain`, `:core:ui`, `:core:common` only |
| `:feature:*` | Never depends on another `:feature:*` |
| `:core:data` | Implements interfaces from `:core:domain` |
| `:app` | Depends on all `:feature:*` for navigation wiring only |

Circular dependencies are a build failure.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + UDF |
| Dependency Injection | Hilt |
| Database | Room + KSP |
| Async | Coroutines + Flow |
| Navigation | Jetpack Navigation Compose |
| OCR | Google ML Kit Text Recognition v2 |
| AI (cloud) | Gemini API |
| AI (on-device) | TensorFlow Lite |
| Networking | Retrofit 2 + OkHttp + Kotlin Serialization |
| Image Loading | Coil 3 |
| Testing | JUnit 5 + MockK + Turbine + Compose UI Test |
| Build | Gradle Kotlin DSL + Version Catalogs (`libs.versions.toml`) |
| CI | GitHub Actions |

---

## Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/sundramsingh/expense_tracker.git

# 2. Open in Android Studio (Ladybug or newer)
#    File → Open → select the cloned directory

# 3. Sync Gradle
#    Android Studio will prompt automatically, or: File → Sync Project with Gradle Files

# 4. Add your Gemini API key to local.properties
echo "GEMINI_API_KEY=your_key_here" >> local.properties

# 5. Run on a device or emulator (API 26+)
#    Press the Run button or use the command below
./gradlew assembleDebug
```

> **Minimum SDK:** API 26 (Android 8.0)
> **Target SDK:** API 35
> **Prerequisites:** Android Studio Ladybug+, JDK 17+

---

## Build Commands

| Command | Description |
|---|---|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew clean` | Clean all build outputs |
| `./gradlew test` | Run all unit tests |
| `./gradlew :feature:dashboard:test` | Run unit tests for a single module |
| `./gradlew connectedAndroidTest` | Run instrumented tests on device/emulator |
| `./gradlew lint` | Run lint across all modules |
| `./gradlew kspDebugKotlin` | Generate Room and Hilt code |

**CI pipeline order:** `clean` → `lint` → `test` → `assembleDebug`

All four steps must pass before a PR can be merged.

---

## Contributing

All contributions must follow the coding standards, architectural rules, and testing requirements defined in [CLAUDE.md](./CLAUDE.md). Key points:

- Kotlin only — no Java
- Every screen follows MVVM + UDF; business logic lives in `UseCase` classes
- All user-visible strings belong in the feature module's `strings.xml`
- All spacing and sizing values belong in `core/ui/theme/Dimens.kt`
- `LazyColumn`/`LazyRow` items must include a `key` argument
- `val` over `var`; use `copy()` for state mutations
- PRs require passing CI: `clean → lint → test → assembleDebug`

See [CLAUDE.md §3–§8](./CLAUDE.md) for the full rules on module dependencies, architecture patterns, naming conventions, and testing standards.
