# AUTOMATION.md — Agentic Development Automation Guide

> End-to-end documentation of the automated development pipeline built for this project.
> Covers everything from writing a feature prompt to verifying it runs crash-free on a device.

---

## What This System Does

When you describe a feature in plain English, the automation system:

1. **Reads** project context (architecture, version pins, past build errors)
2. **Plans** which files to create/modify and in what order
3. **Implements** the feature bottom-up (domain → data → feature → resources)
4. **Builds** in a loop until the APK compiles with zero errors
5. **Tests** the affected module's unit tests
6. **Deploys** to a connected device or emulator
7. **Monitors** logcat for crashes and fixes them
8. **Updates** project memory so the next session has full context

All of this runs without you manually intervening between steps.

---

## System Architecture — 4 Layers

```
┌─────────────────────────────────────────────────────────────┐
│  Layer 4 — GitHub Actions (CI/CD)                           │
│  architecture-check.yml + claude-review.yml                 │
│  Runs on every PR: rule enforcement + AI code review        │
├─────────────────────────────────────────────────────────────┤
│  Layer 3 — Headless Agent CLI                               │
│  scripts/agent.sh                                           │
│  Dispatches Claude to implement/fix/deploy/review           │
├─────────────────────────────────────────────────────────────┤
│  Layer 2 — Claude Code Hooks  (.claude/settings.json)       │
│  PostToolUse: violation check after every file edit         │
│  PreCommit: violation check before every git commit         │
├─────────────────────────────────────────────────────────────┤
│  Layer 1 — Claude Code Skills  (.claude/skills/)            │
│  /implement-feature  /fix-build-error  /deploy-and-verify   │
│  Prompt templates that encode the full development workflow  │
└─────────────────────────────────────────────────────────────┘
```

---

## Layer 1 — Claude Code Skills (Slash Commands)

Skills are markdown prompt templates stored in `.claude/skills/`. They are invoked inside a Claude Code session using `/skill-name`.

### Available Skills

| Skill | Command | What it does |
|---|---|---|
| Implement Feature | `/implement-feature <description>` | Full 9-step lifecycle: context → plan → code → build loop → tests → deploy → logcat → memory update |
| Fix Build Error | `/fix-build-error` | Reads the current build log, identifies root cause, fixes it, rebuilds in a loop until green |
| Deploy and Verify | `/deploy-and-verify` | Incremental or clean deploy, logcat monitoring, crash fix loop |
| Review Changes | `/review-changes` | Reviews recent git changes against CLAUDE.md rules |
| Debug Issue | `/debug-issue` | Systematic debugging workflow |
| Explore Codebase | `/explore-codebase` | Structured codebase exploration before implementation |
| Refactor Safely | `/refactor-safely` | Impact-traced refactoring with regression checks |

### File Location

```
.claude/skills/
├── implement-feature.md    ← Main feature development skill
├── fix-build-error.md      ← Build error diagnosis and fix loop
├── deploy-and-verify.md    ← Deploy + logcat monitoring
├── review-changes.md       ← Code review against CLAUDE.md
├── debug-issue.md
├── explore-codebase.md
└── refactor-safely.md
```

### How a Skill Works (implement-feature.md in detail)

The `/implement-feature` skill encodes the exact 9-step workflow from CLAUDE.md §10:

```
Step 0 — Load Context       Read all 5 memory files in parallel
Step 1 — Explore            Read every file before modifying it
Step 2 — Plan               List every file to create/modify, build order
Step 3 — Implement          Bottom-up: domain → data → feature → resources
Step 4 — Build Verify Loop  ./gradlew assembleDebug until green
Step 5 — Unit Tests         ./gradlew :feature:X:testDebugUnitTest
Step 6 — Deploy             ./scripts/deploy.sh (or --clean if signatures changed)
Step 7 — Monitor Logcat     ./scripts/logcat.sh --clear, watch 15 seconds minimum
Step 8 — Crash Fix Loop     Read full exception chain → fix → redeploy → repeat
Step 9 — Update Memory      Update project_overview.md, feedback_build_errors.md etc.
```

The skill only reports "done" when a crash-free run on device is confirmed.

---

## Layer 2 — Claude Code Hooks

Hooks are shell commands that Claude Code runs automatically in response to events. They are configured in `.claude/settings.json`.

### Current Hook Configuration

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write|Bash",
        "command": "code-review-graph update --skip-flows",
        "timeout": 5000
      },
      {
        "matcher": "Edit|Write",
        "command": "FILE=$(echo \"$CLAUDE_TOOL_INPUT\" | python3 -c \"import sys,json; d=json.load(sys.stdin); print(d.get('file_path',''))\" 2>/dev/null); [ -n \"$FILE\" ] && [ -f \"$FILE\" ] && bash scripts/check-violations.sh \"$FILE\" || true",
        "timeout": 4000
      }
    ],
    "SessionStart": [
      {
        "command": "code-review-graph status",
        "timeout": 3000
      }
    ],
    "PreCommit": [
      {
        "command": "code-review-graph detect-changes --brief",
        "timeout": 10000
      },
      {
        "command": "bash scripts/check-violations.sh",
        "timeout": 8000
      }
    ]
  }
}
```

### What Each Hook Does

| Event | Hook | Purpose |
|---|---|---|
| `PostToolUse` (Edit/Write/Bash) | `code-review-graph update` | Keeps the knowledge graph current after every file change |
| `PostToolUse` (Edit/Write) | `check-violations.sh <file>` | Immediately checks the just-edited file for CLAUDE.md violations |
| `SessionStart` | `code-review-graph status` | Reports graph health at the start of every session |
| `PreCommit` | `code-review-graph detect-changes` | Scores risk of uncommitted changes before committing |
| `PreCommit` | `check-violations.sh` | Scans all recently modified Kotlin files for violations |

### How File Path is Extracted in Hooks

The `CLAUDE_TOOL_INPUT` environment variable contains the raw JSON input of the tool call. Python extracts the `file_path` field:

```bash
FILE=$(echo "$CLAUDE_TOOL_INPUT" | python3 -c \
  "import sys,json; d=json.load(sys.stdin); print(d.get('file_path',''))" 2>/dev/null)
```

---

## Layer 3 — Violation Checker Script

`scripts/check-violations.sh` is a fast static analysis script that checks Kotlin files for the five most common CLAUDE.md violations.

### Usage

```bash
# Check a specific file (called by PostToolUse hook automatically):
./scripts/check-violations.sh feature/dashboard/src/main/.../DashboardContent.kt

# Check all recently modified files (called by PreCommit hook):
./scripts/check-violations.sh
```

### What It Checks

| Check | Pattern | Rule |
|---|---|---|
| Hardcoded strings | `Text("some text")` | CLAUDE.md §6.5 — use `stringResource()` |
| Magic dp values | `16.dp`, `8.dp` (outside Dimens.kt) | CLAUDE.md §6.6 — use `Dimens.*` tokens |
| Null assertion | `!!` | CLAUDE.md §6.1 — use `?.let` or `requireNotNull()` |
| GlobalScope | `GlobalScope` | CLAUDE.md §5 — use `viewModelScope` |
| Missing key | `items(list)` without `key =` | CLAUDE.md §7.1 — `items(list, key = { it.id })` |

### Key Behaviours

- **Always exits 0** — violations are warnings, never blocking. Claude's workflow is never stopped.
- Skips test files (`/test/`, `/androidTest/`) — relaxed rules for tests.
- Skips `Dimens.kt` itself for the `.dp` check.
- Checks only `*.kt` files in `feature/` and `app/` source, not in `build/` or `.gradle/`.

---

## Layer 3 — Headless Agent CLI

`scripts/agent.sh` is a terminal entry point that runs Claude Code in headless (`-p`) mode to automate full development tasks without an interactive session.

### Prerequisites

```bash
# 1. Claude CLI installed
npm install -g @anthropic-ai/claude-code

# 2. API key set
export ANTHROPIC_API_KEY=sk-ant-...

# 3. Android SDK + ADB for deploy mode
# (already configured if Android Studio is installed)
```

### Usage

```bash
# Implement a feature — full lifecycle (plan → code → build → deploy → verify)
./scripts/agent.sh "Add recurring expense support with weekly/monthly options"

# Explicit implement mode (same as above)
./scripts/agent.sh implement "Export expenses to CSV from Settings screen"

# Fix the current build errors in a loop
./scripts/agent.sh fix-build

# Deploy to device and monitor logcat for crashes
./scripts/agent.sh deploy-verify

# Review recent code changes against CLAUDE.md rules
./scripts/agent.sh review
```

### How It Works Internally

```bash
agent.sh "description"
    │
    ▼
validates: claude CLI on PATH + ANTHROPIC_API_KEY set
    │
    ▼
dispatches: claude -p "/implement-feature <description>" --allowedTools "Read,Write,Edit,Bash,Glob,Grep,Agent"
    │
    ▼
Claude reads memory files → plans → implements → builds in loop → deploys → monitors
```

The `--allowedTools` flag grants Claude permission to read, write, run builds, and launch sub-agents — everything needed for a full implementation cycle without interruption.

### Tool Permissions per Mode

| Mode | Allowed Tools | Why |
|---|---|---|
| `implement` | `Read,Write,Edit,Bash,Glob,Grep,Agent` | Full access — needs to write code and run builds |
| `deploy-verify` | `Bash` | Only needs to run shell commands (deploy + logcat) |
| `review` | `Read,Glob,Grep,Bash` | Read-only — no file modifications during review |

---

## Layer 3 — Shell Scripts Reference

All scripts set `JAVA_HOME` to Android Studio's bundled JDK so they work regardless of the system Java version.

| Script | Command | What It Does |
|---|---|---|
| `scripts/deploy.sh` | `./scripts/deploy.sh` | Incremental build → install → launch |
| `scripts/deploy.sh` | `./scripts/deploy.sh --clean` | Clean build → install → launch (use after signature changes) |
| `scripts/logcat.sh` | `./scripts/logcat.sh` | Stream filtered logcat for the app |
| `scripts/logcat.sh` | `./scripts/logcat.sh --errors` | Errors and crashes only |
| `scripts/logcat.sh` | `./scripts/logcat.sh --clear` | Clear old buffer then stream |
| `scripts/test.sh` | `./scripts/test.sh` | Lint + all unit tests |
| `scripts/test.sh` | `./scripts/test.sh --module dashboard` | Single module unit tests |
| `scripts/verify.sh` | `./scripts/verify.sh` | Full CI pipeline: clean → lint → test → assemble → install → launch |
| `scripts/verify.sh` | `./scripts/verify.sh --skip-install` | Same but skip install/launch (no device needed) |
| `scripts/agent.sh` | `./scripts/agent.sh "<prompt>"` | Headless Claude agent (see Layer 3 above) |
| `scripts/check-violations.sh` | `./scripts/check-violations.sh [file]` | CLAUDE.md rule checker |

---

## Layer 4 — GitHub Actions

Two workflows run automatically on every pull request to `main` or `develop`.

### Workflow 1: Architecture Check (`.github/workflows/architecture-check.yml`)

Runs 6 static rule checks against the diff. Fails the PR if any rule is violated.

| Check | What It Catches |
|---|---|
| Cross-feature imports | `:feature:X` importing from `:feature:Y` |
| Hardcoded strings | `Text("literal")` instead of `stringResource()` |
| Magic dp values | `16.dp` inline instead of `Dimens.*` |
| Null assertions | `!!` anywhere in feature/app source |
| GlobalScope | Unscoped coroutines |
| LiveData in feature code | Superseded by `StateFlow` |

This workflow is fast (no build, pure grep) — it gives rule-violation feedback within ~30 seconds of a push.

### Workflow 2: Claude AI Code Review (`.github/workflows/claude-review.yml`)

Posts an automated code review comment on every PR using the Claude API.

**What it does:**
1. Gets the PR diff (Kotlin + XML + Gradle files only, capped at 10,000 chars)
2. Sends the diff to `claude-sonnet-4-6` with a structured prompt listing all 10 CLAUDE.md prohibited patterns
3. Deletes any previous bot review comments (no noise from re-pushes)
4. Posts the review result as a PR comment

**Output format:**
- If violations found: `❌ [Rule N] file.kt:line — explanation` for each
- If clean: `✅ No CLAUDE.md violations found in this PR.`

**Setup required (one time):**
```
GitHub → Settings → Secrets and variables → Actions → New repository secret
Name: ANTHROPIC_API_KEY
Value: sk-ant-...
```

---

## Full Automated Workflow — Example

Here is how the system handles `./scripts/agent.sh "Add a search bar to filter expenses by title"` end to end:

```
1. agent.sh validates environment (claude CLI + API key)

2. Invokes: claude -p "/implement-feature Add a search bar..." --allowedTools "..."

3. Claude reads memory files:
   - project_overview.md        → knows the 11-module structure
   - project_build_config.md    → knows Kotlin 2.0.21, KSP 2.0.21-1.0.25
   - feedback_build_errors.md   → won't repeat the 8 known build errors
   - reference_key_files.md     → knows where ExpenseRepository, NavHost etc. live

4. Plans:
   CREATE: none
   MODIFY: ExpenseRepository.kt  → add searchByTitle(query: Flow<String>)
           ExpenseRepositoryImpl.kt → implement query
           ExpenseDao.kt           → add @Query SELECT ... WHERE title LIKE
           DashboardUiState.kt     → add searchQuery: String field
           DashboardViewModel.kt   → add onSearchChange(), combine with expenses flow
           DashboardContent.kt     → add SearchBar composable
           strings.xml             → add dashboard_search_hint key

5. Implements bottom-up (domain → data → feature → resources)

6. Build verify loop:
   ./gradlew assembleDebug
   → FAIL: new DAO method references column not in entity → reads error → fixes
   ./gradlew assembleDebug
   → PASS

7. Tests:
   ./gradlew :feature:dashboard:testDebugUnitTest
   → PASS

8. Deploy:
   ./scripts/deploy.sh

9. Logcat monitoring (15 seconds):
   No crashes → done

10. Memory update:
    Updates project_overview.md with new search capability
    Updates reference_key_files.md with new DAO method location

11. Reports: "Search feature complete. Build green. All 15 dashboard tests passing.
    Deployed and crash-free on device."
```

---

## Adding a New Skill

1. Create `.claude/skills/my-skill.md` with YAML frontmatter:

```markdown
---
name: My Skill
description: One-line description of what this skill does
---

## What to do step by step...
```

2. Invoke it in a Claude Code session: `/my-skill optional arguments`
3. To run it headlessly: `claude -p "/my-skill arguments" --allowedTools "Read,Write,Edit,Bash,Glob,Grep"`

---

## Adding a New GitHub Actions Rule

To add a new architecture rule to `architecture-check.yml`:

```yaml
- name: "Rule — no Thread.sleep in tests"
  run: |
    MATCHES=$(grep -rn 'Thread.sleep' feature/ --include="*.kt" \
      | grep -v "^\s*//" \
      || true)
    if [ -n "$MATCHES" ]; then
      echo "❌ VIOLATION: Thread.sleep in tests — use advanceUntilIdle() or runTest"
      echo "$MATCHES"
      exit 1
    fi
    echo "✅ No Thread.sleep found."
```

The same pattern works for any grep-detectable violation.

---

## Troubleshooting

### `claude: command not found` in agent.sh
```bash
npm install -g @anthropic-ai/claude-code
# Verify:
claude --version
```

### `ANTHROPIC_API_KEY is not set`
```bash
export ANTHROPIC_API_KEY=sk-ant-...
# Make it permanent:
echo 'export ANTHROPIC_API_KEY=sk-ant-...' >> ~/.zshrc
```

### Hooks not running
Hooks require Claude Code v1.x+. Check `.claude/settings.json` is valid JSON:
```bash
python3 -c "import json; json.load(open('.claude/settings.json'))" && echo "Valid JSON"
```

### check-violations.sh reports false positives
The script checks `*.kt` files modified in the last 60 seconds. If it's scanning the wrong file, pass the path explicitly:
```bash
./scripts/check-violations.sh path/to/specific/File.kt
```

### GitHub Actions: `ANTHROPIC_API_KEY` secret not found
Go to: **GitHub repo → Settings → Secrets and variables → Actions → New repository secret**
Name: `ANTHROPIC_API_KEY`, Value: your API key.

---

*Last updated: 2026-04-11 — Covers: skills, hooks, check-violations.sh, agent.sh, architecture-check.yml, claude-review.yml*
