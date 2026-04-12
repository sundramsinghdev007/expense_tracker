---
name: Deploy and Verify
description: Build → install on physical/virtual device → monitor logcat → fix any crashes → confirm stable run. Full device verification loop.
---

You are running the deploy-and-verify loop. **Do not stop until the app runs without any crash for at least 15 seconds.**

---

## Step 1 — Choose deploy mode

**Incremental deploy** (use when no function signatures or class names changed):
```bash
./scripts/deploy.sh
```

**Clean deploy** (use after any rename, signature change, or `NoSuchMethodError` at runtime):
```bash
./scripts/deploy.sh --clean
```

If unsure, use `--clean`. A few extra seconds is worth avoiding a stale-DEX crash.

---

## Step 2 — Check device connected
The deploy script reports the device ID. If no device found:
1. For physical device: check USB debugging is enabled, cable connected
2. For emulator: start it from Android Studio AVD Manager or run `emulator -avd <avd-name> &`
3. Re-run `./scripts/deploy.sh` once device appears

---

## Step 3 — Monitor logcat (15 seconds minimum)
```bash
./scripts/logcat.sh --clear
```

Watch for any of these in the output:
- `FATAL EXCEPTION` — app crash
- `NoSuchMethodError` — stale APK / DEX mismatch
- `ClassNotFoundException` — missing class in DEX
- `NullPointerException` — null dereference
- `IllegalStateException` — bad state transition
- `NetworkOnMainThreadException` — coroutine missing dispatcher

---

## Step 4 — No crash found
Report:
- Device ID
- Whether it was clean or incremental deploy
- Any warnings observed (non-fatal, but worth noting)
- Status: **verified clean**

---

## Step 5 — Crash found → Fix Loop

### Read the full exception
```bash
./scripts/logcat.sh --errors
```
Read from the `FATAL EXCEPTION` line **all the way through the `Caused by:` chain**.
The root cause is always in the deepest `Caused by:` — not the first line.

### Decode NoSuchMethodError
If you see: `NoSuchMethodError: ... method 'void X.methodName(Z, Lpackage/ClassName;, I)'`
- `Z` = boolean
- `I` = int, `J` = long, `F` = float, `D` = double
- `L<class>;` = object of that class
- `[` prefix = array

This tells you exactly which method signature the installed APK was compiled against vs what's in the DEX now.

### Fix strategy
| Crash type | Fix |
|---|---|
| `NoSuchMethodError` | `./scripts/deploy.sh --clean` — stale DEX, not a code bug |
| `NullPointerException` in ViewModel | Check Flow/StateFlow initial values |
| `NullPointerException` in Composable | Check `uiState.field?.let { }` guards |
| `IllegalStateException: Already resumed` | Coroutine resumed twice — check `emit` calls |
| `ClassNotFoundException` | Class not included in DEX — check module dependency in `build.gradle.kts` |

### After fixing
```bash
./scripts/deploy.sh --clean   # always --clean after code fixes
./scripts/logcat.sh --clear   # clear old crashes from buffer
```
Then monitor again for 15 seconds. Repeat until clean.

---

## Step 6 — Update memory if crash was a new type
If the crash was a new error pattern not yet in `.claude/memory/feedback_build_errors.md`, add it.
