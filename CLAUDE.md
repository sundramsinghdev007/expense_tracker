# CLAUDE.md — Expense Tracker (Android)
> **This file is Claude's single source of truth for this project.**
> Read this before writing, reviewing, or refactoring any code.

---

## 0. Session Startup — Read Project Memory First

At the start of every session, read these files for full project context before doing anything else:

- `.claude/memory/project_overview.md` — app identity, 11 modules, core capabilities
- `.claude/memory/project_build_config.md` — all pinned versions, convention plugins, CI
- `.claude/memory/project_architecture.md` — MVVM+UDF rules, module dependency constraints
- `.claude/memory/feedback_build_errors.md` — 5 build errors already fixed (don't regress)
- `.claude/memory/feedback_build_workflow.md` — mandatory build-verify loop rules
- `.claude/memory/reference_key_files.md` — critical file paths for DI, domain, navigation, theme

These files are gitignored (local only). Update them whenever architecture, versions, or patterns change.

---

## 1. Project Overview

**App:** Expense Tracker — a production-grade personal finance Android app.

**Core Capabilities:**
- Manual expense logging with categories, dates, and notes
- Receipt scanning via **ML Kit OCR** (auto-fills expense fields)
- **AI-powered insights** — spend forecasting, anomaly detection, natural language entry (Gemini API)
- Budget creation, tracking, and overspend alerts
- Analytics dashboard — charts, trends, category breakdowns
- Recurring expense automation and smart category suggestions
- Offline-first with full Room persistence

**Non-negotiables:** Offline-first · Accessible · Localised (no hardcoded strings) · Testable

---

## 2. Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 2.0+ |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + UDF (Unidirectional Data Flow) |
| DI | Hilt |
| Database | Room + KSP |
| Async | Coroutines + Flow |
| Navigation | Jetpack Navigation Compose |
| OCR | Google ML Kit Text Recognition v2 |
| AI | Gemini API (cloud) + TensorFlow Lite (on-device) |
| Networking | Retrofit 2 + OkHttp + Kotlin Serialization |
| Images | Coil 3 |
| Testing | JUnit 5 · MockK · Turbine · Compose UI Test |
| Build | Gradle Kotlin DSL + Version Catalogs (`libs.versions.toml`) |
| CI | GitHub Actions |

---

## 3. Module Structure

```
ExpenseTracker/
├── app/                        ← Entry point, NavHost, DI root
├── build-logic/convention/     ← Gradle convention plugins
├── core/
│   ├── common/                 ← Kotlin-only utils, Result<T>, extensions
│   ├── data/                   ← Room, Retrofit, Repository implementations
│   ├── domain/                 ← Models, Repository interfaces, Use Cases
│   └── ui/                     ← Theme, Dimens, shared components
└── feature/
    ├── dashboard/              ← Home: summary + recent expenses
    ├── add-expense/            ← Manual + voice entry
    ├── analytics/              ← Charts, trends, forecasts
    ├── ocr/                    ← Camera + ML Kit receipt scanning
    ├── budgets/                ← Budget CRUD + progress tracking
    └── settings/               ← Preferences, currency, export
```

**Dependency rules (strictly enforced):**
- `:feature:*` → `:core:domain`, `:core:ui`, `:core:common` only
- `:feature:*` modules **never** depend on each other
- `:core:data` implements interfaces from `:core:domain`
- `:app` depends on all `:feature:*` only for navigation wiring
- Circular dependencies = build failure

---

## 4. Build Commands

```bash
./gradlew assembleDebug             # Build debug APK
./gradlew assembleRelease           # Build release APK
./gradlew clean                     # Clean all outputs
./gradlew test                      # All unit tests
./gradlew :feature:dashboard:test   # Single module tests
./gradlew connectedAndroidTest      # Instrumented tests
./gradlew lint                      # Lint all modules
./gradlew kspDebugKotlin            # Generate Room/Hilt code
```

**CI order:** `clean → lint → test → assembleDebug`
PRs must pass all four steps before merge.

---

## 5. Architecture Rules

### MVVM + UDF
Every feature follows this exact flow:

```
UI (Composable)
  │  observes StateFlow
  ▼
ViewModel
  │  calls
  ▼
UseCase  ──►  Repository (interface)
                   │
                   ▼
              RepositoryImpl  (data module)
                   │
              ┌────┴────┐
              DAO      API
```

### State Management
- One `UiState` data class per screen
- One `UiEvent` sealed class for one-shot events (navigation, toasts)
- `StateFlow<UiState>` in ViewModel; collect via `collectAsStateWithLifecycle()`
- Never use `LiveData` in new code

### ViewModel Rules
- Functions return `Unit`
- Business logic lives in `UseCase` classes, not ViewModels
- `suspend` calls inside `viewModelScope.launch { }`
- Inject `@ApplicationContext` via Hilt — never pass raw `Context`
- No `GlobalScope` — ever

---

## 6. Code Style & Guidelines

### 6.1 Kotlin Conventions
- Follow [official Kotlin style](https://kotlinlang.org/docs/coding-conventions.html) strictly
- Max function length: **30 lines** — extract named private functions
- No `!!` null assertions — use `?.let`, `requireNotNull()`, or elvis `?:`
- Prefer `when` over `if-else` chains for sealed classes / enums
- One top-level declaration per file (except tightly related utilities)

### 6.2 Functional Programming (Mandatory)
- **`val` over `var` always.** A `var` requires a comment justifying mutability.
- Use `copy()` for state mutations — never mutate data classes directly
- Use `map`, `filter`, `fold`, `groupBy` — not imperative loops
- Functions must be pure (no side effects, deterministic output)
- All shared state flows through `StateFlow` or `SharedFlow`

```kotlin
// ✅
val filtered = expenses
    .filter { it.category == selected }
    .sortedByDescending { it.date }

// ❌
val filtered = mutableListOf<Expense>()
for (e in expenses) { if (e.category == selected) filtered.add(e) }
```

### 6.3 Jetpack Compose — State Hoisting (Mandatory)
- All non-trivial composables must be **stateless**
- No composable below screen level calls `viewModel()` directly
- Screen composable = stateful (holds VM); all children = stateless

```kotlin
// ✅ Stateless, testable, reusable
@Composable
fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) { ... }

// ❌ Unhoisted — untestable
@Composable
fun AmountInputField() {
    var amount by remember { mutableStateOf("") }
}
```

### 6.4 Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| Screen (stateful) | `<Feature>Screen` | `DashboardScreen` |
| Content (stateless) | `<Feature>Content` | `DashboardContent` |
| Card component | `<Entity>Card` | `ExpenseCard`, `BudgetCard` |
| Header component | `<Context>Header` | `SummaryHeader`, `CategoryHeader` |
| Bottom sheet | `<Action>Sheet` | `FilterSheet`, `DatePickerSheet` |
| Dialog | `<Action>Dialog` | `DeleteConfirmDialog` |
| ViewModel | `<Feature>ViewModel` | `AnalyticsViewModel` |
| UI State | `<Feature>UiState` | `DashboardUiState` |
| UI Event | `<Feature>UiEvent` | `AddExpenseUiEvent` |
| Room Entity | `<n>Entity` | `ExpenseEntity` |
| DAO | `<n>Dao` | `ExpenseDao` |
| Repository interface | `<n>Repository` | `ExpenseRepository` |
| Repository impl | `<n>RepositoryImpl` | `ExpenseRepositoryImpl` |
| Use Case | `<Verb><Noun>UseCase` | `GetExpensesUseCase` |
| DI Module | `<Scope>Module` | `DatabaseModule`, `RepositoryModule` |

### 6.5 String Resources — Zero Tolerance
- Every user-visible string must live in the feature module's `strings.xml`
- No string literals in `Text()`, `placeholder`, `label`, `contentDescription`, `Toast`, `Snackbar`
- Key naming: `<screen>_<element>_<descriptor>` (e.g. `add_expense_amount_hint`)

```kotlin
// ✅
Text(text = stringResource(R.string.dashboard_total_this_month))
// ❌
Text(text = "Total this month")
```

### 6.6 Dimensions — No Magic Numbers
- Never write `16.dp` inline in composables
- All spacing/sizing lives in `core/ui/theme/Dimens.kt`

```kotlin
object Dimens {
    val spacingXs  = 4.dp;  val spacingSm = 8.dp
    val spacingMd  = 16.dp; val spacingLg = 24.dp
    val spacingXl  = 32.dp; val cardRadius = 12.dp
    val iconSize   = 24.dp
}
```

---

## 7. AI Guidelines for Claude

### 7.1 Pre-Response Checklist (run every time)

**Before Writing or Rewriting Any File**
- [ ] Read the file first — never rewrite from memory or assumptions
- [ ] Every import in a rewritten file is verified against the actual source (function names, class names, package paths)
- [ ] Cross-file references (e.g. calling a composable from another file) are confirmed by reading the target file — never assumed from the file path
- [ ] If renaming a symbol used by compiled/cached artefacts, run `./gradlew clean` before building — stale caches cause `NoSuchMethodError` at runtime while compile succeeds

**Performance — List Rendering**
- [ ] Dynamic lists use `LazyColumn`/`LazyRow` — never `Column` inside `verticalScroll`
- [ ] Every item has `key = { it.id }` in `LazyColumn`
- [ ] Heavy calculations inside `remember { }` or `derivedStateOf { }`
- [ ] No side effects / coroutine launches inside `items { }` lambdas
- [ ] `Modifier` chains not rebuilt on every recomposition

```kotlin
// ✅
LazyColumn {
    items(expenses, key = { it.id }) { expense ->
        ExpenseCard(expense = expense, onClick = { onExpenseClick(expense.id) })
    }
}
// ❌ — missing key, full recomposition on every change
LazyColumn { items(expenses) { expense -> ExpenseCard(expense = expense) } }
```

**Strings**
- [ ] Every `Text()`, `placeholder`, `label`, `contentDescription` uses `stringResource(...)`
- [ ] Suggest the correct `strings.xml` key using `<screen>_<element>_<descriptor>` pattern

### 7.2 Code Generation Rules
- Always generate **Kotlin** — never suggest Java
- Generated composables are **stateless** unless it is an explicit screen-level request
- ViewModel results only come through `UiState` or `UiEvent`
- Room queries that are observed return `Flow<List<T>>` not one-shot `suspend`
- New `var` declarations require a justification comment
- New hardcoded dimensions require a note to move them to `Dimens.kt`

### 7.3 Prohibited Patterns

| Pattern | Why | Fix |
|---|---|---|
| `var` in data class | Breaks immutability | `val` + `copy()` |
| String literal in UI | Not localisable | `stringResource(...)` |
| `Column` + `verticalScroll` for dynamic lists | No virtualisation | `LazyColumn` |
| `!!` null assertion | Crash risk | `?.let {}` / `requireNotNull()` |
| `LiveData` in new code | Superseded | `StateFlow` |
| Business logic in Composable | Violates MVVM | Move to `ViewModel` or `UseCase` |
| Raw `Context` in ViewModel | Memory leak | `@ApplicationContext` via Hilt |
| `GlobalScope` | Unscoped | `viewModelScope` / `lifecycleScope` |
| `LazyColumn` items without `key` | Recomposition waste | Add `key = { it.id }` |
| Magic numbers in Compose | Inconsistent UI | Use `Dimens.*` tokens |
| Rewriting a file without reading it first | Wrong imports / wrong symbol names | Always `Read` before `Write` |
| Assuming a function/class name from its file path | Runtime `NoSuchMethodError` if wrong | Read the target file to confirm the exact name |
| Renaming a symbol without `./gradlew clean` | Stale cache → compile passes, runtime crashes | Run `clean` before any symbol rename |
| Snackbar/Toast for budget alerts | User-visible only when screen is open | Use `NotificationManagerCompat` system notification |

### 7.4 Regression Prevention (Mandatory)

Before delivering any change, verify that existing features are not broken:

- **Read every file you modify** — understand what it currently does before changing it
- **Trace downstream impact** — if a function signature, class name, or interface changes, find every caller and update them
- **Never silently drop working code** — when rewriting a file, preserve all existing functionality unless explicitly asked to remove it
- **Revert-safe changes first** — for risky refactors, change one thing at a time so regressions are easy to isolate
- **Check the feature list** — before marking a task done, mentally walk through: Dashboard, Add Expense, Analytics, OCR, Budgets, Settings — does each still work given the change?

### 7.5 Communication Style
- Cite the relevant `CLAUDE.md` section when enforcing a rule
- Show a corrected diff rather than describing the fix in prose
- List options with trade-offs when multiple approaches exist
- Explain non-obvious architectural decisions

---

## 8. Post-Change Build Verification (Mandatory)

**After every set of code changes — no exceptions — run the build and iterate until it passes.**

### 8.1 Verification Loop

```
1. Run:   ./gradlew assembleDebug
2. FAIL?  Read the full error log (do not guess — read it)
3.        Fix the root cause identified in the log
4.        Go to step 1
5. PASS?  Done — report success to the user
```

If tests were added or modified, also run `./gradlew test` after `assembleDebug` passes.

### 8.2 Rules

- **Never report a task as done without a passing build.** If the build cannot be run (e.g. no Android SDK in the environment), state this explicitly rather than assuming the code is correct.
- **Read the log before fixing.** Do not guess at the fix from the error message preview — scroll to the actual `> Task :module:task FAILED` section and read the full compiler/linker output.
- **Fix root causes, not symptoms.** If the same error recurs after a fix, re-read the log — the first fix was wrong or incomplete.
- **One fix at a time when the cause is unclear.** Changing multiple things simultaneously makes it impossible to know which change resolved (or broke) something.
- **Update memory after new build errors.** If a new error type is encountered and fixed, add it to `.claude/memory/feedback_build_errors.md` so future sessions don't repeat it.
- **`NoSuchMethodError` at runtime = stale build cache.** When compile passes but the app crashes with `NoSuchMethodError`, the installed APK has an older compiled class than the source. Fix: `./gradlew clean assembleDebug` + reinstall.
- **No build environment → say so.** If `./gradlew` cannot run (no JDK/Android SDK in the terminal), explicitly tell the user and provide the exact command to run themselves. Never silently skip verification.

### 8.3 Common Build Failure Checklist

Before reading the log, quickly check these common causes:
- [ ] New use case / class not registered in a Hilt `@Module`?
- [ ] New import references a module that is not in the module's `build.gradle.kts` dependencies?
- [ ] `@StringRes` used as a generic type argument? (See §6.1 — use plain `Int` with a comment)
- [ ] KSP needs regenerating? Run `./gradlew kspDebugKotlin` first
- [ ] New Room entity not added to `@Database(entities = [...])`?
- [ ] Stale build cache after a symbol rename? Run `./gradlew clean assembleDebug`

### 8.4 Project-Specific Constants

Do not change these without a full clean rebuild:

| Constant | Value | Location |
|---|---|---|
| App theme composable | `ExpenseTrackerTheme(darkTheme, content)` — **1 boolean param only** | `app/.../ui/theme/Theme.kt` |
| Budget alert channel ID | `"budget_alerts"` | `ExpenseTrackerApp` + `AddExpenseScreen` (intentionally duplicated — §CLAUDE.md no-helpers rule) |

---

## 9. Testing Standards

```kotlin
// ViewModel unit test template
@Test
fun `addExpense emits isSaved true on success`() = runTest {
    val useCase = mockk<AddExpenseUseCase>()
    coEvery { useCase(any()) } returns Result.success(1L)
    val vm = AddExpenseViewModel(useCase)
    vm.onTitleChange("Coffee"); vm.onAmountChange("150")
    vm.saveExpense()
    vm.uiState.test { assertTrue(awaitItem().isSaved) }
}
```

**Rules:**
- ViewModels tested with `TestCoroutineDispatcher` + Turbine
- Repositories tested against in-memory Room database
- Composables tested with `ComposeContentTestRule`
- No `Thread.sleep()` — use `advanceUntilIdle()` or `runTest`
- All tests must be deterministic

---

---

## 10. Feature Development Workflow (End-to-End)

This is the exact process Claude Code follows for every new feature. Follow it in order — never skip steps.

### Step 1 — Understand the feature
Before writing a single line of code:
- Read CLAUDE.md + all memory files (§0)
- Identify which modules are affected (`:feature:*`, `:core:domain`, `:core:data`)
- List every file that needs to be created or modified
- Confirm the architecture path: UI → ViewModel → UseCase → Repository → DAO

### Step 2 — Implement (bottom-up)
Build in dependency order to avoid circular references:
```
1. Domain layer first   → model changes, new UseCase, Repository interface update
2. Data layer second    → DAO query, RepositoryImpl update
3. Feature layer last   → UiState, UiEvent, ViewModel, Screen, Content, components
4. Resources last       → strings.xml entries, drawables, if needed
```

### Step 3 — Build verify loop
```bash
./gradlew assembleDebug
# FAIL → read full log → fix root cause → repeat
# PASS → proceed
```
Never move to Step 4 without a green build.

### Step 4 — Unit tests
```bash
./gradlew test
# or single module:
./scripts/test.sh --module dashboard
```

### Step 5 — Deploy to device
```bash
# Quick deploy (incremental build + install + launch):
./scripts/deploy.sh

# Full clean deploy (use after any rename/signature change):
./scripts/deploy.sh --clean
```

### Step 6 — Monitor on device
```bash
# Stream all app logs:
./scripts/logcat.sh

# Errors + crashes only:
./scripts/logcat.sh --errors

# Clear old logs then stream:
./scripts/logcat.sh --clear
```

### Step 7 — Crash? Fix loop
```
1. Read logcat — find the actual Exception line, not just "FATAL"
2. Decode JVM descriptor if NoSuchMethodError (Z=bool, L...;=obj, I=int)
3. Fix root cause in code
4. ./scripts/deploy.sh  (incremental if signature unchanged, --clean if signature changed)
5. ./scripts/logcat.sh --clear  (clear old crash from buffer)
6. Repeat until no crash
```

### Step 8 — Full verify (before marking done)
```bash
./scripts/verify.sh
# Runs: clean → lint → test → assembleDebug → install → launch
```

### Step 9 — Update memory
After any feature is complete:
- Update `.claude/memory/project_overview.md` if capabilities changed
- Update `.claude/memory/project_architecture.md` if new patterns were introduced
- Update `.claude/memory/reference_key_files.md` if new key files were created
- Update `.claude/memory/feedback_build_errors.md` if a new error type was fixed

---

### Scripts Reference

| Script | What it does | When to use |
|---|---|---|
| `./scripts/deploy.sh` | Build → install → launch | After code changes |
| `./scripts/deploy.sh --clean` | Clean build → install → launch | After any rename / signature change |
| `./scripts/logcat.sh` | Stream filtered logcat | While testing on device |
| `./scripts/logcat.sh --errors` | Errors + crashes only | Investigating a crash |
| `./scripts/test.sh` | Lint + unit tests | Before marking a task done |
| `./scripts/test.sh --module X` | Single module unit tests | Faster feedback during dev |
| `./scripts/verify.sh` | Full CI pipeline | Final check before done |

---

*Last updated: 2026-04-11 · Sessions: initial build, budget alerts, system notifications, build-verify loop, automation scripts · Update this file whenever architectural decisions change.*

<!-- code-review-graph MCP tools -->
## MCP Tools: code-review-graph

**IMPORTANT: This project has a knowledge graph. ALWAYS use the
code-review-graph MCP tools BEFORE using Grep/Glob/Read to explore
the codebase.** The graph is faster, cheaper (fewer tokens), and gives
you structural context (callers, dependents, test coverage) that file
scanning cannot.

### When to use graph tools FIRST

- **Exploring code**: `semantic_search_nodes` or `query_graph` instead of Grep
- **Understanding impact**: `get_impact_radius` instead of manually tracing imports
- **Code review**: `detect_changes` + `get_review_context` instead of reading entire files
- **Finding relationships**: `query_graph` with callers_of/callees_of/imports_of/tests_for
- **Architecture questions**: `get_architecture_overview` + `list_communities`

Fall back to Grep/Glob/Read **only** when the graph doesn't cover what you need.

### Key Tools

| Tool | Use when |
|------|----------|
| `detect_changes` | Reviewing code changes — gives risk-scored analysis |
| `get_review_context` | Need source snippets for review — token-efficient |
| `get_impact_radius` | Understanding blast radius of a change |
| `get_affected_flows` | Finding which execution paths are impacted |
| `query_graph` | Tracing callers, callees, imports, tests, dependencies |
| `semantic_search_nodes` | Finding functions/classes by name or keyword |
| `get_architecture_overview` | Understanding high-level codebase structure |
| `refactor_tool` | Planning renames, finding dead code |

### Workflow

1. The graph auto-updates on file changes (via hooks).
2. Use `detect_changes` for code review.
3. Use `get_affected_flows` to understand impact.
4. Use `query_graph` pattern="tests_for" to check coverage.
