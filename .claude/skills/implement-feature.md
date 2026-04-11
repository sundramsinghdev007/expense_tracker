---
name: Implement Feature
description: Full feature development lifecycle — context → plan → code → build loop → test → deploy → logcat → memory update. Follows CLAUDE.md §10 exactly.
---

You are executing the 9-step Feature Development Workflow from CLAUDE.md §10.
**Do not report the task done until a crash-free run on device is confirmed.**
The feature to implement follows this prompt. If no feature is specified, ask the user.

---

## Step 0 — Load Context (mandatory, never skip)

Read these memory files in parallel before touching any code:
- `.claude/memory/project_overview.md`
- `.claude/memory/project_build_config.md`
- `.claude/memory/project_architecture.md`
- `.claude/memory/feedback_build_errors.md`
- `.claude/memory/reference_key_files.md`

Then state explicitly which modules are affected: `:core:domain`, `:core:data`, `:feature:*`

---

## Step 1 — Explore (read before write)

For every file you will modify or create:
1. Read the file first — never rewrite from memory
2. Confirm exact function/class names by reading the target file (never assume from file path)
3. Trace the architecture path: UI → ViewModel → UseCase → Repository → DAO
4. Identify every Hilt `@Module` that needs updating

---

## Step 2 — Plan (write it out before coding)

State:
- Every file to CREATE (full path + what it contains)
- Every file to MODIFY (full path + what changes)
- Build order: domain → data → feature → resources

---

## Step 3 — Implement Bottom-Up

**Order is strictly enforced:**
1. Domain layer: model changes → UseCase → Repository interface update
2. Data layer: DAO query → Entity → RepositoryImpl → `@Module` update
3. Feature layer: UiState → UiEvent → ViewModel → Screen → Content → components
4. Resources: `strings.xml`, drawables

**Non-negotiable rules (violating any of these blocks Step 4):**
- Read every file before writing it
- Every user-visible string → `stringResource(R.string.*)`, never a string literal in `Text()`
- Every dp value → `Dimens.*`, never inline `16.dp`
- Composables below screen level → stateless (no `viewModel()` call inside)
- Every `LazyColumn` → `items(list, key = { it.id })`
- No `!!` — use `?.let`, `requireNotNull()`, or `?:`
- No `GlobalScope` — use `viewModelScope`

---

## Step 4 — Build Verify Loop

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug 2>&1 | tail -80
```

**FAIL** → Read from `> Task :module:task FAILED` to end of output. Check CLAUDE.md §8.3:
- [ ] New UseCase/class not registered in Hilt `@Module`?
- [ ] New import references a module not in `build.gradle.kts` dependencies?
- [ ] `@StringRes` used as generic type argument? (use plain `Int`)
- [ ] KSP needs regenerating? Run `./gradlew kspDebugKotlin` first
- [ ] New Room entity not in `@Database(entities = [...])`?
- [ ] Symbol renamed without clean? Run `./gradlew clean assembleDebug`

Fix ONE root cause, then restart Step 4. Never guess — read the error.

**PASS** → Proceed to Step 5.

---

## Step 5 — Tests

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :feature:<module>:testDebugUnitTest 2>&1 | tail -40
```

FAIL → read the failure, fix root cause, re-run. PASS → proceed.

---

## Step 6 — Deploy

```bash
./scripts/deploy.sh
```

If any function signature or class name changed since the last installed APK:
```bash
./scripts/deploy.sh --clean
```

---

## Step 7 — Monitor Logcat (15 seconds minimum)

```bash
./scripts/logcat.sh --clear
```

Collect lines containing: `FATAL EXCEPTION`, `NoSuchMethodError`, `ClassNotFoundException`,
`NullPointerException`, `IllegalStateException`, `Unresolved`.

**No crash** → Proceed to Step 9.
**Crash found** → Go to Step 8.

---

## Step 8 — Crash Fix Loop

1. Read the **full exception chain** — not just `FATAL`, read the `Caused by:` lines
2. `NoSuchMethodError`: decode the JVM descriptor to find the mismatch:
   - `Z`=boolean, `I`=int, `J`=long, `L<class>;`=object, `[`=array prefix
3. Fix the root cause in source
4. Always use `./scripts/deploy.sh --clean` after any signature change (stale DEX = runtime crash even when compile passes)
5. Run `./scripts/logcat.sh --clear` and monitor again
6. Repeat until no crashes

---

## Step 9 — Update Memory

After a crash-free run, update whichever files changed:
- `.claude/memory/project_overview.md` — if new capabilities were added
- `.claude/memory/project_architecture.md` — if new patterns were introduced
- `.claude/memory/reference_key_files.md` — if new key files were created
- `.claude/memory/feedback_build_errors.md` — if a new error type was encountered and fixed

Then report: feature complete, build green, tests passing, deployed to device, no crashes.
