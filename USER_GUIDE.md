# User Guide — Agentic Development Automation

> How to use the automated pipeline to go from a GitHub issue to a running feature on your device — with zero manual coding steps.

---

## Table of Contents

1. [How It Works (30-second overview)](#1-how-it-works)
2. [Mode A — GitHub Actions (no server needed)](#2-mode-a--github-actions)
3. [Mode B — Local Webhook (full device deployment)](#3-mode-b--local-webhook)
4. [Writing Issues That Claude Can Implement](#4-writing-good-issues)
5. [What Claude Does Step by Step](#5-what-claude-does-step-by-step)
6. [Monitoring Progress](#6-monitoring-progress)
7. [Reviewing Claude's Pull Requests](#7-reviewing-claudes-pull-requests)
8. [Terminal Commands Reference](#8-terminal-commands-reference)
9. [Troubleshooting](#9-troubleshooting)

---

## 1. How It Works

```
You create a GitHub issue
          │
          ▼
You apply the label  "ready-to-implement"
          │
          ▼  (everything below is automatic)
          │
     ┌────┴────────────────────────────────────┐
     │         MODE A — GitHub Actions         │
     │   Claude runs in the cloud              │
     │   Build + test in CI                    │
     │   Opens a PR automatically              │
     │   You deploy to device manually         │
     └────────────────────────────────────────┘
     ─── OR ───
     ┌────┴────────────────────────────────────┐
     │         MODE B — Local Webhook          │
     │   Claude runs on your machine           │
     │   Build + test + deploy to device       │
     │   Full cycle, no PR needed              │
     └────────────────────────────────────────┘
```

**Both modes are triggered the same way:** label an issue. The difference is where Claude runs and whether it can reach your physical device.

---

## 2. Mode A — GitHub Actions

Best for: teams, CI integration, automatic PRs, no server required.

### One-Time Setup (5 minutes)

#### Step 1 — Add your API key as a GitHub secret

1. Go to your repo on GitHub
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `ANTHROPIC_API_KEY`
5. Value: your key (`sk-ant-...`)
6. Click **Add secret**

#### Step 2 — Create the trigger label

1. Go to **Issues** → **Labels** → **New label**
2. Name: `ready-to-implement`
3. Color: `#0075ca` (blue)
4. Click **Create label**

#### Step 3 — Push the workflow file

The workflow is already in your repo at `.github/workflows/auto-implement.yml`.
Just make sure it is committed and pushed to `main` or `develop`:

```bash
git add .github/workflows/auto-implement.yml
git commit -m "ci: add auto-implement workflow"
git push
```

That is all. Setup is complete.

---

### Using It (every time)

#### Step 1 — Create a GitHub issue

Go to **Issues** → **New issue**. Write a clear title and description.
(See [Writing Good Issues](#4-writing-good-issues) for tips.)

#### Step 2 — Apply the label

In the issue sidebar, click **Labels** → select **ready-to-implement**.

#### Step 3 — Watch it happen

Within 30 seconds, Claude Code posts a comment on the issue:

```
🤖 Claude Code is on it
Working on branch: feature/issue-42-add-search-bar

Steps running now:
1. 📖 Reading project memory and architecture rules
2. 🗺️  Planning domain → data → feature → resources
3. 💻 Implementing bottom-up following CLAUDE.md
4. 🔨 Build verify loop until assembleDebug passes
5. 🧪 Running unit tests
6. 📬 Opening a PR
```

When done (usually 10–20 minutes), another comment appears with a link to the PR.

#### Step 4 — Review and deploy

1. Open the PR Claude created
2. Review the code diff
3. Pull the branch locally: `git pull && git checkout feature/issue-42-...`
4. Deploy to your device: `./scripts/deploy.sh`
5. Test the feature manually
6. Merge the PR if everything looks good

---

## 3. Mode B — Local Webhook

Best for: solo developers, needs to deploy to a physical device automatically, full cycle verification.

### One-Time Setup

#### Step 1 — Install prerequisites

```bash
# Install ngrok (creates the public tunnel)
brew install ngrok/ngrok/ngrok       # macOS
# OR
brew install cloudflare/cloudflare/cloudflared  # macOS (free, no account needed)
# OR
snap install ngrok                   # Ubuntu/Linux

# Make scripts executable (already done if you cloned fresh)
chmod +x scripts/setup-webhook.sh scripts/webhook-server.py scripts/agent.sh
```

#### Step 2 — Set environment variables

```bash
export ANTHROPIC_API_KEY=sk-ant-...

# Optional but recommended — lets Claude post status to GitHub
export GITHUB_TOKEN=ghp_...          # needs: repo + issues:write scope
```

Add these to `~/.zshrc` or `~/.bash_profile` so they persist between sessions.

#### Step 3 — Run the setup script

```bash
./scripts/setup-webhook.sh
```

This will:
1. Ask for a webhook secret (or generate one for you)
2. Start the webhook server on `localhost:8080`
3. Open an ngrok tunnel
4. Print the exact URL and configuration instructions

**Keep this terminal open.** The server and tunnel run until you press `Ctrl+C`.

#### Step 4 — Configure GitHub webhook (first time only)

The setup script prints the exact instructions. They look like this:

```
1. Open: https://github.com/<owner>/<repo>/settings/hooks/new
2. Payload URL  → https://abc123.ngrok.io/webhook
3. Content type → application/json
4. Secret       → <your-secret>
5. Events       → Select "Issues" only
6. Click "Add webhook"
```

Fill in the form and click **Add webhook**.

#### Step 5 — Create the trigger label (same as Mode A)

Go to **Issues** → **Labels** → **New label** → `ready-to-implement`

---

### Using It (every time)

#### Before you start

Make sure the webhook server is running:
```bash
./scripts/setup-webhook.sh
```

Connect your Android device via USB (or start an emulator in Android Studio).

#### Trigger

Create a GitHub issue → apply `ready-to-implement` label.

Claude will:
1. Comment on the issue: "Working on it"
2. Run `./scripts/agent.sh "feature description"` on your local machine
3. Implement the feature, build, test, deploy to your device
4. Monitor logcat for crashes, fix them if found
5. Comment on the issue: "Implementation complete"

You can watch the progress in the terminal running `setup-webhook.sh`.

---

## 4. Writing Good Issues

Claude reads the issue title and body to understand what to build. The better the issue, the better the implementation.

### Issue Title

The title becomes the feature name and the git commit message. Be specific:

| Title | Quality |
|---|---|
| `Add feature` | ❌ Too vague |
| `Add a filter` | ❌ Unclear |
| `Add date filter chips (Day/Month/Year) to the Dashboard screen` | ✅ Clear |
| `Show budget exceeded alert cards on Dashboard when a category exceeds its limit` | ✅ Clear |

### Issue Body — What to Include

```markdown
## What to build
A search bar at the top of the Dashboard screen that filters the expense list 
in real-time as the user types.

## Screen affected
Dashboard (feature/dashboard module)

## Behaviour
- Search matches against expense title (case-insensitive, partial match)
- List updates immediately as user types (no submit button)
- Show a clear (×) button when there is text in the field
- When search is empty, show all expenses (default state)
- Empty state message if no expenses match the search query

## What NOT to do
- Do not add search to other screens
- Do not persist the search query across app restarts
```

### What Claude Uses Automatically

Even without a detailed body, Claude always:
- Reads `CLAUDE.md` architecture rules
- Reads all project memory files (module structure, past build errors)
- Follows the bottom-up implementation order
- Uses `stringResource` for all strings, `Dimens.*` for all spacing

### What Claude Cannot Do

- Access the internet or external APIs (no web browsing)
- Deploy to a device in Mode A (GitHub Actions has no connected device)
- Implement features that require knowledge of undocumented external systems
- Test on a physical device in Mode A (only Mode B can do this)

---

## 5. What Claude Does Step by Step

When you apply the `ready-to-implement` label, this is the exact sequence:

```
Step 0 — Load context (30 seconds)
   Read: project_overview.md, project_build_config.md,
         project_architecture.md, feedback_build_errors.md,
         reference_key_files.md
   Goal: understand the 11-module structure, known build errors to avoid,
         where key files like AppNavHost and ExpenseRepository live

Step 1 — Explore (1–3 minutes)
   Read every file that will be created or modified
   Verify exact class names, function signatures, package paths
   Trace the full architecture path: UI → VM → UseCase → Repo → DAO

Step 2 — Plan (30 seconds)
   List every file to CREATE and MODIFY
   Confirm build order: domain → data → feature → resources

Step 3 — Implement bottom-up (5–10 minutes)
   1. Domain: model changes, new UseCase, repository interface update
   2. Data:   DAO query, entity mapping, RepositoryImpl, Hilt @Module
   3. Feature: UiState, UiEvent, ViewModel, Screen, Content, components
   4. Resources: strings.xml, drawables

Step 4 — Build verify loop (2–10 minutes)
   Run: ./gradlew assembleDebug
   FAIL → read full error from "Task :module:task FAILED" line
          identify root cause (Hilt missing? Import wrong? KSP needed?)
          fix one thing → rebuild
   Repeat until BUILD SUCCESSFUL

Step 5 — Unit tests (1–3 minutes)
   Run: ./gradlew test
   Fix any test compile errors or failures
   Confirm all tests green

Step 6 — Deploy (Mode B only, 1–2 minutes)
   Run: ./scripts/deploy.sh
   Uses --clean if any class names or signatures changed

Step 7 — Logcat monitoring (Mode B only, 1 minute)
   Watch for: FATAL EXCEPTION, NoSuchMethodError, NullPointerException
   If crash: read full exception chain, fix root cause, redeploy
   Repeat until 15 seconds of clean logcat

Step 8 — Commit / PR
   Mode A: git commit → Action pushes → Action creates PR
   Mode B: git commit (local, no push)

Step 9 — Update memory
   Add new patterns or errors to .claude/memory/ files
   So future sessions don't repeat the same mistakes
```

---

## 6. Monitoring Progress

### Mode A — GitHub Actions

Watch the Actions tab:
```
https://github.com/<owner>/<repo>/actions
```

Click the **Auto-Implement Feature** workflow run to see Claude's live output.

You will also get comments on the issue itself:
- Comment 1 (immediate): "Working on it + steps"
- Comment 2 (when done): "PR ready" or "Failed + reason"

### Mode B — Local Webhook

The terminal running `setup-webhook.sh` shows everything:

```
10:42:31  INFO      Accepted: issue #15 "Add search bar" labeled "ready-to-implement"
10:42:31  INFO      Agent starting — issue #15: Add search bar...
# Claude's output streams here in real-time
10:43:01  INFO      Build step 1: compiling...
10:48:22  INFO      BUILD SUCCESSFUL
10:49:15  INFO      All tests PASSED
10:50:02  INFO      Deployed to device EX1234
10:50:17  INFO      Logcat: clean (no crashes in 15s)
10:50:18  INFO      Agent completed successfully in 467s
```

---

## 7. Reviewing Claude's Pull Requests (Mode A)

Claude's PRs always include this checklist in the PR body:

```
- [ ] Review code follows CLAUDE.md architecture rules
- [ ] No hardcoded strings (Text("...") must use stringResource)
- [ ] New composables are stateless
- [ ] LazyColumn items have key = { it.id }
- [ ] Deploy to device and test manually (./scripts/deploy.sh)
```

### What to verify before merging

**Architecture (2 minutes)**
- Does the implementation follow the correct module? Feature code in `:feature:*`, business logic in `:core:domain`
- Are there any cross-feature imports? (`:feature:X` importing `:feature:Y` — forbidden)
- Is every new string in `strings.xml`?

**Code quality (2 minutes)**
- No `!!` null assertions
- No `var` in data classes (use `val` + `copy()`)
- `LazyColumn` items have `key = { it.id }`

**Manual testing (5–10 minutes)**
```bash
git checkout feature/issue-NNN-<slug>
./scripts/deploy.sh
```
Test the new feature and check that existing features still work:
- Dashboard loads correctly
- Add Expense form submits
- Analytics charts render
- OCR camera screen opens
- Budgets list loads
- Settings saves preferences

**If something is wrong:**

Option 1 — Request changes on the PR (Claude will re-run):
> Not yet supported — re-apply the label after closing the PR branch.

Option 2 — Fix it yourself and push to the branch:
```bash
git checkout feature/issue-NNN-<slug>
# make your fix
git add -A && git commit -m "fix: <what you fixed>"
git push
```

Option 3 — Close the PR, update the issue description, re-apply the label.

---

## 8. Terminal Commands Reference

### Setting up

```bash
# Mode B: start webhook server + ngrok (keep this terminal open)
./scripts/setup-webhook.sh

# Mode B: start server only (if you already have a tunnel)
export WEBHOOK_SECRET=... ANTHROPIC_API_KEY=...
python3 scripts/webhook-server.py

# Health check
curl http://localhost:8080/
```

### Manual agent (without GitHub issue)

```bash
# Implement a feature directly from your terminal
./scripts/agent.sh "Add a dark mode toggle to the Settings screen"

# Fix current build errors in a loop
./scripts/agent.sh fix-build

# Deploy to device and verify
./scripts/agent.sh deploy-verify

# Review recent code changes
./scripts/agent.sh review
```

### Build and deploy

```bash
./scripts/deploy.sh             # incremental build + install + launch
./scripts/deploy.sh --clean     # clean build (use after renames)
./scripts/logcat.sh             # stream app logs
./scripts/logcat.sh --errors    # errors and crashes only
./scripts/verify.sh --skip-install  # full CI pipeline, no device needed
```

---

## 9. Troubleshooting

### "No commits made — implementation may have failed" (Mode A)

Claude ran but did not commit. Check the Actions log for:
- Build errors it couldn't fix (complex dependency issue)
- Test failures it couldn't resolve
- Issue description too vague to implement

**Fix:** Add more detail to the issue body. Remove and re-apply the `ready-to-implement` label.

### Claude posts "Working on it" but the PR never appears

Check the Actions run — Claude may have exited early due to:
- `ANTHROPIC_API_KEY` secret not set → verify in repo **Settings → Secrets**
- Build failing after 5+ attempts → a complex dependency issue needs a manual fix first

### Mode B: webhook receives events but agent doesn't start

```bash
# Check server is running
curl http://localhost:8080/
# {"status": "running", "trigger_label": "ready-to-implement", "agent_busy": false}

# Check ANTHROPIC_API_KEY is set
echo $ANTHROPIC_API_KEY

# Check claude CLI is installed
claude --version
```

### Mode B: ngrok URL expires between sessions

ngrok free tier gives a new random URL each time. You must update the GitHub webhook URL every time you restart:

```
GitHub → Settings → Webhooks → Edit → update Payload URL
```

**To avoid this:** Sign up for a free ngrok account and use a static domain:
```bash
ngrok config add-authtoken <your-token>
ngrok http 8080 --domain=<your-static-domain>.ngrok-free.app
```

Or use `cloudflared` which is always free:
```bash
cloudflared tunnel --url http://localhost:8080
```

### "WEBHOOK_SECRET not set — anyone can trigger the agent!"

Set the secret in `.env.webhook` (created by `setup-webhook.sh`) or export it:
```bash
export WEBHOOK_SECRET=my-secret-here
```

Make sure the same secret is configured in the GitHub webhook settings.

### GitHub webhook shows "Recent Deliveries" with red ✗

Click the delivery to see the response. Common causes:
- Server not running → start it with `./scripts/setup-webhook.sh`
- ngrok expired → restart setup to get a new URL and update GitHub webhook
- Wrong secret → verify secret in `.env.webhook` matches GitHub webhook settings
- Signature mismatch → make sure you selected `application/json` as content type

### Build fails in CI with "Android SDK not found"

The `ubuntu-latest` runner has Android SDK at `/usr/local/lib/android/sdk`. This is set automatically. If it's missing:

```yaml
# Add to auto-implement.yml after setup-java step:
- name: Accept Android SDK licenses
  run: yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses > /dev/null 2>&1 || true
```

### Feature is too complex for one automated run

Very large features (new screen with new DAO, new Hilt module, new navigation route) may exceed a single Claude run. Split into smaller issues:

Instead of:
> "Add a recurring expenses feature with weekly/monthly/yearly options, calendar UI, notification scheduling, and automatic expense creation"

Write:
> Issue 1: "Add RecurringExpense domain model and GetRecurringExpensesUseCase"
> Issue 2: "Add RecurringExpense Room entity and DAO"
> Issue 3: "Add recurring expenses list screen (feature:recurring-expenses)"
> Issue 4: "Add notification scheduling for recurring expenses"

Label them one at a time, in order.

---

## Quick Reference Card

```
TRIGGER        Apply label "ready-to-implement" to a GitHub issue

MODE A         Runs in GitHub Actions CI
               • No server needed
               • Build + test in cloud
               • Opens PR automatically  
               • You deploy to device manually

MODE B         Runs on your local machine
               • Requires: ./scripts/setup-webhook.sh running
               • Full cycle: build + test + deploy + logcat
               • No PR (commits are local)
               • Device must be connected via USB

GOOD ISSUE     Clear title + screen name + behaviour description + what NOT to do
BAD ISSUE      "Add feature" or "Fix the thing"

CHECK STATUS   GitHub issue comments (both modes)
               Actions tab (Mode A)
               Terminal (Mode B)

REVIEW PR      git checkout feature/issue-NNN-slug
               ./scripts/deploy.sh
               Test manually, then merge
```

---

*Last updated: 2026-04-11*
*See also: [AUTOMATION.md](AUTOMATION.md) — technical deep-dive into every component*
