# PROMPTS.md — Expense Tracker Build Playbook
> Use these prompts in order, one at a time, inside a Claude conversation.
> Always paste the contents of `CLAUDE.md` as the very first message (Prompt 0).
> Each prompt builds on the previous one — do not skip steps.

---

## HOW TO USE THIS FILE

1. Open a new Claude conversation
2. Send **Prompt 0** (paste your full `CLAUDE.md` contents)
3. Send prompts **1 → 20** in order
4. Copy each response into Android Studio before sending the next prompt
5. After every 5 prompts, send the **Checkpoint prompt** to catch drift

---

## PROMPT 0 — Load Project Context (Always First)
```
I am building a multi-module Android Expense Tracker app. 
The following CLAUDE.md is your technical guide for this entire project. 
Read it fully and confirm you understand the architecture, coding rules, 
naming conventions, and AI guidelines before we write any code.

[PASTE FULL CONTENTS OF CLAUDE.md HERE]

Once you have read it, reply only with:
"Ready. I have loaded the Expense Tracker technical guide. 
Awaiting your first build prompt."
```

---

## PHASE 1 — Project Foundation

### PROMPT 1 — Gradle Setup
```
Following the CLAUDE.md tech stack exactly, generate these root Gradle files 
for the multi-module Expense Tracker project:

1. `settings.gradle.kts` — include all modules:
   :app, :core:common, :core:data, :core:domain, :core:ui,
   :feature:dashboard, :feature:add-expense, :feature:analytics,
   :feature:ocr, :feature:budgets, :feature:settings

2. `gradle/libs.versions.toml` — version catalog with all dependencies 
   from the CLAUDE.md tech stack. Include bundles for: compose, room, 
   retrofit, testing.

3. Root `build.gradle.kts` — apply all plugins as false.

4. `gradle.properties` — enable parallel builds, caching, 
   nonTransitiveRClass, and set JVM args to 4g.

Output each file with its full path as a comment on line 1.
Enforce all CLAUDE.md rules. Flag any decision you make that 
is not explicitly covered in CLAUDE.md.
```

---

### PROMPT 2 — Convention Plugins
```
Create the build-logic convention plugins for the Expense Tracker.

Files needed:
- `build-logic/settings.gradle.kts`
- `build-logic/convention/build.gradle.kts`
- `AndroidApplicationPlugin.kt` — compileSdk 35, minSdk 26, JVM 17
- `AndroidLibraryPlugin.kt` — same SDK config
- `AndroidFeaturePlugin.kt` — applies library + hilt + compose plugins
- `AndroidHiltPlugin.kt` — applies hilt + ksp

Each plugin lives in:
`build-logic/convention/src/main/kotlin/`

Register all four as Gradle plugins with IDs prefixed 
`expensetracker.android.*`

Follow CLAUDE.md §3 module dependency rules strictly.
```

---

### PROMPT 3 — core:domain (Models + Interfaces + UseCases)
```
Generate the complete :core:domain module for the Expense Tracker.

Domain models (pure Kotlin, zero Android dependencies):
- `Expense` — id, title, amount (Double), category (Category enum), 
  date (LocalDate), notes, receiptUri (nullable)
- `Category` enum — FOOD, TRANSPORT, SHOPPING, HEALTH, UTILITIES, 
  ENTERTAINMENT, OTHER — each with a displayName and emoji property
- `Budget` — id, category, limitAmount, spentAmount, month (YearMonth) 
  Include computed properties: remainingAmount, progressFraction (Float 0-1), 
  isOverBudget (Boolean)

Repository interfaces:
- `ExpenseRepository` — getAllExpenses(): Flow<List<Expense>>, 
  getExpensesByCategory, getExpensesByDateRange, getById, 
  insert (returns Long), update, delete
- `BudgetRepository` — getBudgetsForMonth, upsertBudget, deleteBudget

Use Cases (one class per file, @Inject constructor):
- `GetExpensesUseCase` — operator fun invoke(): Flow<List<Expense>>
- `AddExpenseUseCase` — validates title not blank + amount > 0, 
  returns Result<Long>
- `DeleteExpenseUseCase`
- `GetBudgetsUseCase`
- `UpsertBudgetUseCase`

Also generate `build.gradle.kts` for this module.

Apply all CLAUDE.md §6.1 and §6.2 rules. All models use val only.
```

---

### PROMPT 4 — core:common
```
Generate the :core:common module for the Expense Tracker.

Contents:
1. `Result<T>` sealed class — Success(data), Error(exception), Loading
2. `Flow<T>.asResult()` extension — maps to Flow<Result<T>>, 
   catches exceptions as Result.Error
3. `DateUtils.kt` — extension functions on LocalDate: 
   toDisplayString() (MMM dd, yyyy), isCurrentMonth(), 
   toStartOfMonth(), toEndOfMonth()
4. `CurrencyUtils.kt` — formatAmount(amount: Double, currencyCode: String): String
5. `StringExt.kt` — isValidAmount(): Boolean (checks parseable positive Double)

Also generate `build.gradle.kts`.

Follow CLAUDE.md §6.2 functional programming rules.
All functions must be pure and deterministic.
No Android dependencies in this module.
```

---

### PROMPT 5 — core:data (Room + Repositories)
```
Generate the complete :core:data module for the Expense Tracker.

1. Room Entity:
   - `ExpenseEntity` — mirrors Expense domain model, stores Category as String, 
     LocalDate as ISO-8601 String
   - Extension functions: `ExpenseEntity.toDomain()` and `Expense.toEntity()`

2. DAO:
   - `ExpenseDao` — getAllExpenses(): Flow<List<ExpenseEntity>>, 
     getByCategory(category: String): Flow<List<ExpenseEntity>>,
     getByDateRange(start: String, end: String): Flow<List<ExpenseEntity>>,
     getById(id: Long): ExpenseEntity? (suspend),
     insert (suspend, returns Long), update (suspend), delete (suspend)

3. Database:
   - `ExpenseTrackerDatabase` — @Database, version 1, exportSchema true

4. Repository Implementation:
   - `ExpenseRepositoryImpl` — implements ExpenseRepository, 
     maps entities to domain models using Flow.map

5. Hilt DI Modules:
   - `DatabaseModule` — @Singleton Room database + DAO providers
   - `RepositoryModule` — @Binds ExpenseRepositoryImpl → ExpenseRepository

6. `build.gradle.kts` for this module

All Flow queries return domain models (not entities) to feature modules.
Apply CLAUDE.md §5 architecture rules strictly.
```

---

### PROMPT 6 — core:ui (Design System)
```
Generate the :core:ui module — the shared design system for the Expense Tracker.

1. `Theme.kt` — MaterialTheme wrapper `ExpenseTrackerTheme(darkTheme: Boolean)`
   Light palette: primary #2E7D32 (green), error #C62828 (red)
   Dark palette: primary #66BB6A, adjusted surfaces
   
2. `Type.kt` — `ExpenseTypography` with headlineMedium (Bold 28sp), 
   titleLarge (SemiBold 22sp), bodyLarge (Normal 16sp), labelSmall (Medium 11sp)

3. `Dimens.kt` — object with: spacingXs(4dp), spacingSm(8dp), spacingMd(16dp), 
   spacingLg(24dp), spacingXl(32dp), cardRadius(12dp), iconSize(24dp)

4. Shared components (stateless, fully hoisted):
   - `LoadingIndicator` — centred CircularProgressIndicator, fills available space
   - `EmptyState(message: String, emoji: String)` — centred column with emoji + text
   - `ErrorState(message: String, onRetry: () -> Unit)` — message + retry button
   - `ExpenseTrackerTopBar(title: String, onBack: (() -> Unit)? = null)` — shows 
     back arrow only when onBack is provided

5. `build.gradle.kts` for this module

Every string in these components must use a parameter — no hardcoded strings.
Apply CLAUDE.md §6.3 state hoisting and §6.6 Dimens rules.
```

---

## PHASE 2 — Features

### PROMPT 7 — App Module + Navigation
```
Generate the :app module for the Expense Tracker.

Files:
1. `AndroidManifest.xml` — CAMERA + INTERNET permissions, 
   HiltAndroidApp application class, single activity, edge-to-edge
2. `ExpenseTrackerApp.kt` — @HiltAndroidApp Application subclass
3. `MainActivity.kt` — @AndroidEntryPoint, enableEdgeToEdge(), 
   setContent with ExpenseTrackerTheme wrapping AppNavHost
4. `AppRoutes.kt` — object with const route strings for all 6 screens
5. `AppNavHost.kt` — NavHost with composable destinations for:
   Dashboard, AddExpense, Analytics, Ocr, Budgets, Settings
   Pass navController::navigate as onNavigate lambda to screens
   Pass navController::popBackStack as onBack
6. `BottomNavBar.kt` — Material 3 NavigationBar with 4 tabs: 
   Dashboard (Home icon), Analytics (BarChart icon), 
   Budgets (Wallet icon), Settings (Settings icon)
7. `build.gradle.kts` — depends on all feature + core modules

Apply CLAUDE.md §5 — app depends on features only for wiring, 
no business logic in app module.
```

---

### PROMPT 8 — feature:dashboard
```
Generate the complete :feature:dashboard module.

UiState:
- `DashboardUiState` — isLoading, totalThisMonth (Double), 
  totalYesterday (Double), recentExpenses (List<Expense>), 
  topCategory (Category?), errorMessage (String?)

ViewModel:
- `DashboardViewModel` — @HiltViewModel, injects GetExpensesUseCase
  Computes totalThisMonth, topCategory from the expense flow
  Exposes uiState: StateFlow<DashboardUiState>

UI files:
- `DashboardScreen.kt` — stateful, collects VM state, passes to Content
- `DashboardContent.kt` — stateless, shows Scaffold + LazyColumn
- `component/SummaryHeader.kt` — Card showing total spend + top category
- `component/ExpenseCard.kt` — Row with emoji, title, category, amount (red)
- `component/MonthlyProgressBar.kt` — LinearProgressIndicator for monthly budget
- `res/values/strings.xml` — all strings for this feature

Rules:
- LazyColumn with key = { it.id } for expense list
- All strings in strings.xml, key pattern: dashboard_*
- SummaryHeader and ExpenseCard must be stateless composables
- Apply all CLAUDE.md §7.1 checklist items

Also generate `build.gradle.kts`.
```

---

### PROMPT 9 — feature:add-expense
```
Generate the complete :feature:add-expense module.

UiState:
- `AddExpenseUiState` — title, amount (String), selectedCategory (Category), 
  date (LocalDate = today), notes, isLoading, isSaved, validationErrors (Map<String, String>)

UiEvent:
- `AddExpenseUiEvent` sealed class — NavigateBack, ShowSnackbar(message: String)

ViewModel:
- `AddExpenseViewModel` — @HiltViewModel, injects AddExpenseUseCase
  Individual update functions: onTitleChange, onAmountChange, 
  onCategoryChange, onDateChange, onNotesChange
  saveExpense() — validates, calls use case, emits event on success/failure
  events: SharedFlow<AddExpenseUiEvent>

UI files:
- `AddExpenseScreen.kt` — stateful, handles events (popBackStack, snackbar)
- `AddExpenseContent.kt` — stateless scaffold with form
- `component/CategorySelector.kt` — horizontal LazyRow of selectable Category chips
- `component/AmountInputField.kt` — OutlinedTextField, numeric keyboard, 
  currency prefix, hoisted
- `component/DatePickerField.kt` — tappable field that opens Material 3 DatePickerDialog
- `res/values/strings.xml` — all strings, key pattern: add_expense_*

Apply CLAUDE.md §6.3 state hoisting — every component is stateless.
Validation errors show as supportingText on each field.
Generate `build.gradle.kts`.
```

---

### PROMPT 10 — feature:analytics
```
Generate the complete :feature:analytics module.

UiState:
- `AnalyticsUiState` — isLoading, selectedPeriod (Period enum: WEEK/MONTH/YEAR), 
  totalSpend (Double), categoryBreakdown (Map<Category, Double>), 
  dailySpend (List<DailySpend> — date + amount), trend (Trend enum: UP/DOWN/FLAT)

ViewModel:
- `AnalyticsViewModel` — @HiltViewModel, injects GetExpensesUseCase
  Filters by selectedPeriod, computes breakdown and daily series
  onPeriodChange(period: Period) — updates filter

UI files:
- `AnalyticsScreen.kt` — stateful wrapper
- `AnalyticsContent.kt` — stateless, scrollable Column (not lazy — fixed item count)
- `component/PeriodSelector.kt` — segmented button row (Week/Month/Year)
- `component/CategoryHeader.kt` — total spend + trend arrow for the period
- `component/CategoryBreakdownChart.kt` — horizontal bar chart using Canvas API 
  (no third-party chart library), one bar per category, labelled with emoji + %
- `component/DailySpendChart.kt` — line chart using Canvas + Path API
- `res/values/strings.xml` — key pattern: analytics_*

Use Canvas API for charts — do not add any chart library dependency.
Apply all CLAUDE.md performance rules for the composables.
Generate `build.gradle.kts`.
```

---

### PROMPT 11 — feature:ocr
```
Generate the complete :feature:ocr module using ML Kit Text Recognition v2.

Files:
1. `OcrScanScreen.kt` — CameraX PreviewView integrated in Compose via AndroidView, 
   shutter button, permission handling for CAMERA
2. `OcrReviewScreen.kt` — shows extracted fields (title, amount, date) in editable 
   OutlinedTextFields, Confirm button navigates to AddExpense pre-filled
3. `OcrUiState.kt` — isScanning, extractedTitle, extractedAmount, extractedDate, 
   errorMessage
4. `OcrViewModel.kt` — @HiltViewModel, processImage(imageProxy: ImageProxy) suspend fun, 
   uses ML Kit TextRecognizer, parses amount (regex ₹?\\d+(\\.\\d{2})?), 
   emits extracted fields to UiState
5. `OcrParser.kt` — pure utility object, parseAmount(text: String): Double?, 
   parseDate(text: String): LocalDate?, parseMerchantName(text: String): String?
6. `CameraPermissionHandler.kt` — composable that handles permission request flow
7. `res/values/strings.xml` — key pattern: ocr_*

Camera integration must use CameraX (androidx.camera:camera-camera2 + camera-lifecycle).
OcrParser functions must be pure and unit-testable with no Android dependencies.
Apply CLAUDE.md §6.2 rules in OcrParser — no mutable state.
Generate `build.gradle.kts` with ML Kit + CameraX dependencies.
```

---

### PROMPT 12 — feature:budgets
```
Generate the complete :feature:budgets module.

UiState:
- `BudgetsUiState` — isLoading, budgets (List<Budget>), 
  currentMonth (YearMonth), totalBudgeted (Double), totalSpent (Double)

ViewModel:
- `BudgetsViewModel` — @HiltViewModel, injects GetBudgetsUseCase + UpsertBudgetUseCase
  Combines budget flow with expense flow to compute spentAmount per budget
  showAddBudgetSheet() / hideAddBudgetSheet() — toggle sheet state

UI files:
- `BudgetsScreen.kt` — stateful
- `BudgetsContent.kt` — stateless, LazyColumn of BudgetCards + FAB
- `component/BudgetCard.kt` — Card with category emoji, name, 
  LinearProgressIndicator (red when isOverBudget), spent/limit amounts
- `component/AddBudgetSheet.kt` — ModalBottomSheet with CategorySelector 
  + amount input + Save button (stateless, hoisted)
- `res/values/strings.xml` — key pattern: budgets_*

BudgetCard progress bar must animate with `animateFloatAsState`.
Flag overspent budgets with error colour from MaterialTheme.colorScheme.error.
Apply CLAUDE.md §7.3 prohibited patterns checklist.
Generate `build.gradle.kts`.
```

---

### PROMPT 13 — feature:settings
```
Generate the complete :feature:settings module.

Settings options:
- Currency selection (INR, USD, EUR, GBP) — stored in DataStore
- Theme toggle (Light / Dark / System)
- Notification preference for budget alerts (Boolean)
- Export expenses as CSV (trigger function in ViewModel)
- App version display (read from BuildConfig)

Files:
1. `SettingsUiState.kt` — selectedCurrency, isDarkTheme (Boolean?), 
   notificationsEnabled, appVersion (String)
2. `SettingsViewModel.kt` — @HiltViewModel, reads/writes DataStore<Preferences>
   Injects @ApplicationContext for DataStore creation
3. `SettingsScreen.kt` — stateful
4. `SettingsContent.kt` — stateless, Column of settings rows
5. `component/SettingsRow.kt` — reusable row: icon + title + subtitle + trailing slot
6. `component/CurrencyPickerDialog.kt` — AlertDialog with RadioButton list
7. `data/SettingsDataStore.kt` — DataStore keys + read/write extensions
8. `res/values/strings.xml` — key pattern: settings_*

Use `androidx.datastore:datastore-preferences` for persistence.
All ViewModel functions return Unit. 
Apply CLAUDE.md §6 rules throughout.
Generate `build.gradle.kts`.
```

---

## PHASE 3 — Polish + Testing

### PROMPT 14 — Unit Tests: UseCases
```
Generate unit tests for all Use Cases in :core:domain.

For each use case write a test class using JUnit 5 + MockK + Turbine:

`AddExpenseUseCaseTest`:
- `invoke emits success with valid expense`
- `invoke returns failure when title is blank`
- `invoke returns failure when amount is zero`
- `invoke returns failure when amount is negative`

`GetExpensesUseCaseTest`:
- `invoke returns mapped flow from repository`
- `invoke emits empty list when repository is empty`

`DeleteExpenseUseCaseTest`:
- `invoke calls repository delete with correct expense`

`UpsertBudgetUseCaseTest`:
- `invoke delegates to BudgetRepository`

Use `@ExtendWith(MockKExtension::class)`.
All tests use `runTest { }` — no Thread.sleep().
Follow CLAUDE.md §8 testing standards exactly.
```

---

### PROMPT 15 — Unit Tests: ViewModels
```
Generate unit tests for DashboardViewModel and AddExpenseViewModel.

`DashboardViewModelTest`:
- `init loads expenses and computes totalThisMonth correctly`
- `init sets topCategory to the category with highest spend`
- `init sets isLoading false after expenses emit`
- `init sets errorMessage when flow throws`

`AddExpenseViewModelTest`:
- `saveExpense emits isSaved true on use case success`
- `saveExpense emits errorMessage on use case failure`
- `onTitleChange updates uiState title`
- `onAmountChange updates uiState amount`
- `saveExpense does not call use case when title is blank`

Use `TestCoroutineDispatcher`, `Turbine`, MockK.
Verify StateFlow emissions with `vm.uiState.test { }`.
Apply CLAUDE.md §8 — no Thread.sleep, all deterministic.
```

---

### PROMPT 16 — Unit Tests: OcrParser
```
Generate unit tests for OcrParser in :feature:ocr.

`OcrParserTest` (JUnit 5, pure Kotlin — zero Android dependencies):

parseAmount tests:
- `returns 250.00 for input "₹250.00"`
- `returns 1500.00 for input "Total: 1500"`
- `returns 99.99 for input "Rs. 99.99"`
- `returns null for input "no amount here"`
- `returns null for empty string`

parseDate tests:
- `parses DD/MM/YYYY format`
- `parses DD-MM-YYYY format`
- `parses MMM DD YYYY format`
- `returns null for unparseable string`

parseMerchantName tests:
- `extracts first capitalised line as merchant name`
- `returns null for blank input`

All assertions use assertEquals / assertNull / assertNotNull.
No mocking needed — OcrParser is pure.
```

---

### PROMPT 17 — Compose UI Tests: Dashboard
```
Generate Compose UI tests for DashboardContent in :feature:dashboard.

`DashboardContentTest` using `createComposeRule()`:

- `shows LoadingIndicator when isLoading is true`
- `shows EmptyState when expenses list is empty`
- `shows SummaryHeader with correct total amount`
- `renders one ExpenseCard per expense in the list`
- `ExpenseCard displays correct title and amount`
- `tapping ExpenseCard triggers onExpenseClick with correct id`
- `FAB click triggers onAddClick`

Use `composeTestRule.setContent { DashboardContent(...) }`.
Pass fake UiState data — do not use real ViewModel.
Use `onNodeWithText`, `onNodeWithContentDescription`, `performClick`.
Apply CLAUDE.md §8 testing standards.
```

---

### PROMPT 18 — Room Database Tests
```
Generate instrumented tests for ExpenseDao in :core:data.

`ExpenseDaoTest` using in-memory Room database:
- `insertExpense returns generated id`
- `getAllExpenses emits inserted expense`
- `getByCategory returns only matching category`
- `getByDateRange returns expenses within range`
- `deleteExpense removes it from getAllExpenses`
- `updateExpense reflects new values in getAllExpenses`

Setup:
- Build in-memory `ExpenseTrackerDatabase` with `allowMainThreadQueries()`
- Use `@Before` to create DB, `@After` to close it
- Collect Flow emissions with `first()` from `kotlinx.coroutines.flow`

Follow CLAUDE.md §8 — no Thread.sleep, use `runTest`.
```

---

### PROMPT 19 — CI + Proguard + Accessibility
```
Generate the final production hardening files for the Expense Tracker.

1. `.github/workflows/ci.yml`:
   - Trigger on push to main/develop and all PRs
   - Steps: checkout → JDK 17 (temurin) → Gradle cache → 
     lint → test → assembleDebug → upload APK artifact
   - Cache key must include libs.versions.toml hash

2. `app/proguard-rules.pro`:
   - Keep Room entities and DAOs
   - Keep Hilt generated classes
   - Keep Kotlin Serialization classes
   - Keep ML Kit classes
   - Keep Retrofit + OkHttp
   - Keep data classes used in API responses

3. Accessibility audit — review these files and fix any missing 
   contentDescription, missing semantics, or touch target < 48dp:
   - ExpenseCard.kt
   - BudgetCard.kt  
   - CategorySelector.kt
   - SummaryHeader.kt

For the accessibility fixes: show a unified diff for each file.
Cite CLAUDE.md §7.1 for every string contentDescription that needs 
to be added to strings.xml.
```

---

### PROMPT 20 — README + Final Review
```
1. Generate a professional README.md for the Expense Tracker project:
   - App name, one-line description, badges (build passing, API 26+, Kotlin)
   - Screenshots section (placeholder image links)
   - Feature list with emoji bullets
   - Architecture diagram (ASCII, showing module dependencies)
   - Quick start (clone → open in Android Studio → sync → run)
   - Build commands table (from CLAUDE.md §4)
   - Module structure tree
   - Tech stack table
   - Contributing section referencing CLAUDE.md

2. Do a final full-project code review against every rule in CLAUDE.md.
   Check the following files across all modules:
   - Every ViewModel (UDF compliance, no raw Context, no GlobalScope)
   - Every Screen composable (state hoisting, no hardcoded strings)
   - Every LazyColumn usage (key argument present)
   - Every strings.xml (naming convention compliance)
   
   Output a table: File | Rule Violated | Fix Required
   If no violations, output: "All CLAUDE.md rules satisfied."
```

---

## CHECKPOINT PROMPT — Use After Every 5 Prompts
```
Checkpoint review: Re-read CLAUDE.md and audit the last 5 code 
responses you generated for this session.

Check for:
1. Any hardcoded strings not in strings.xml
2. Any LazyColumn missing key = { it.id }
3. Any var in a data class
4. Any composable below screen level that is stateful
5. Any magic dimension values (e.g. 16.dp) not using Dimens.*
6. Any !! null assertion operator
7. Any LiveData usage
8. Any business logic inside a composable

Output a table: Issue Found | File | Line (approx) | Fix
If clean: "Checkpoint passed — no violations found."
```

---

## QUICK REFERENCE — Prompt Index

| # | What It Builds |
|---|---|
| 0 | Load CLAUDE.md context |
| 1 | Root Gradle + version catalog |
| 2 | Convention plugins (build-logic) |
| 3 | :core:domain — models, repos, use cases |
| 4 | :core:common — utils, Result<T>, extensions |
| 5 | :core:data — Room, DAOs, repository impls |
| 6 | :core:ui — theme, Dimens, shared components |
| 7 | :app — MainActivity, NavHost, BottomNav |
| 8 | :feature:dashboard — home screen |
| 9 | :feature:add-expense — expense entry form |
| 10 | :feature:analytics — charts + trends |
| 11 | :feature:ocr — camera + ML Kit parsing |
| 12 | :feature:budgets — budget tracking |
| 13 | :feature:settings — DataStore preferences |
| 14 | Unit tests — Use Cases |
| 15 | Unit tests — ViewModels |
| 16 | Unit tests — OcrParser |
| 17 | UI tests — Dashboard Compose |
| 18 | Instrumented tests — Room DAO |
| 19 | CI pipeline + ProGuard + Accessibility |
| 20 | README + final full-project review |
| CP | Checkpoint — run after every 5 prompts |