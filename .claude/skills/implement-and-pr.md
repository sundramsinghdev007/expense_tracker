---
name: Implement and PR
description: CI-optimised feature implementation — context → plan → code → build loop → tests → git commit. No device deploy (CI handles push + PR). Used by auto-implement.yml GitHub Actions workflow.
---

You are implementing a feature requested in a GitHub issue and running inside a CI environment (GitHub Actions).
**Do not stop until `assembleDebug` is green, all tests pass, and changes are committed.**
The issue title, body, branch name, and commit message are provided below the skill invocation.

---

## Step 0 — Load Context (mandatory)

Read these memory files in parallel before touching any code:
- `.claude/memory/project_overview.md`
- `.claude/memory/project_build_config.md`
- `.claude/memory/project_architecture.md`
- `.claude/memory/feedback_build_errors.md`
- `.claude/memory/reference_key_files.md`

Then state: which modules are affected and what the full architecture path is:
`UI → ViewModel → UseCase → Repository → DAO`

---

## Step 1 — Explore (read every file before writing it)

For every file you will create or modify:
1. Read it first — never write from memory or assumptions
2. Confirm exact class/function names by reading the target file
3. Trace all callers if you change a function signature
4. Identify every Hilt `@Module` that needs updating

---

## Step 2 — Plan

State explicitly:
- Every file to **CREATE** (full path + what it contains)
- Every file to **MODIFY** (full path + what changes)
- Build order: `domain → data → feature → resources`

---

## Step 3 — Implement Bottom-Up

**Strictly in this order:**
1. Domain layer — model changes, new UseCase, Repository interface update
2. Data layer — DAO query, Entity, RepositoryImpl, `@Module` update
3. Feature layer — UiState, UiEvent, ViewModel, Screen, Content, components
4. Resources — `strings.xml`, drawables

**Non-negotiable rules (each one blocks Step 4 if violated):**
- Read every file before writing it
- Every `Text(...)` → `stringResource(R.string.key)` — never a string literal
- Every dp value → `Dimens.*` token — never inline `16.dp`
- Every composable below screen level → stateless (no `viewModel()` inside)
- Every `LazyColumn` → `items(list, key = { it.id })`
- No `!!` — use `?.let`, `requireNotNull()`, or `?:`
- No `GlobalScope` — use `viewModelScope`
- No `LiveData` — use `StateFlow`

---

## Step 4 — Build Verify Loop

> **CI NOTE: Do NOT set JAVA_HOME — it is already configured by the CI environment.**

```bash
./gradlew assembleDebug 2>&1 | tail -80
```

**FAIL** → Read from `> Task :module:task FAILED` to the end of output. Check these in order:
- [ ] New UseCase/class not in a Hilt `@Module`?
- [ ] Import references a module not in `build.gradle.kts` dependencies?
- [ ] `@StringRes` used as a generic type argument? (use plain `Int`)
- [ ] KSP not regenerated? Run `./gradlew kspDebugKotlin` first
- [ ] New Room entity not in `@Database(entities = [...])`?
- [ ] Symbol renamed without clean? Run `./gradlew clean assembleDebug`

Fix ONE root cause, then restart this step. Never guess.

**PASS** → Proceed to Step 5.

---

## Step 5 — Unit Tests

```bash
./gradlew test 2>&1 | tail -60
```

If tests fail: read the failure message, fix root cause, re-run.
If tests pass: proceed to Step 6.

---

## Step 6 — Commit

Commit all changes. Use the commit message provided in the prompt.
If no commit message was given, use: `feat(<module>): <feature description>`

```bash
git add -A
git commit -m "<commit message from the prompt>"
```

**DO NOT push** — the CI workflow pushes the branch and creates the PR after this step completes.

---

## Step 7 — Report

Output a summary:
```
Implementation complete.
Branch: <branch name>
Commit: <commit hash>
Files created: <list>
Files modified: <list>
Build: PASSED
Tests: PASSED
```