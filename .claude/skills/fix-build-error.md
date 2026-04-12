---
name: Fix Build Error
description: Read the full build error log → diagnose root cause → fix → repeat until BUILD SUCCESSFUL. Never guesses — always reads the actual error.
---

You are in a build error fix loop. **Do not stop until `BUILD SUCCESSFUL` appears in output.**

---

## Protocol

### 1. Capture the full error
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug 2>&1 | tail -100
```

### 2. Read from the failure line
Scroll to the line that says `> Task :module:task FAILED` and read **everything below it**.
Do not act on the error message preview — read the actual compiler/linker output.

### 3. Check the CLAUDE.md §8.3 common failure checklist
- [ ] New UseCase/class not in a Hilt `@Module`? → Add `@Provides` or `@Binds` entry
- [ ] Import references a module not in `build.gradle.kts`? → Add the dependency
- [ ] `@StringRes` used as a generic type argument? → Use plain `Int` with a comment
- [ ] KSP code not regenerated? → Run `./gradlew kspDebugKotlin` first, then retry
- [ ] New Room `@Entity` not in `@Database(entities = [...])`? → Add it to the list
- [ ] Symbol renamed without clean build? → Run `./gradlew clean assembleDebug`
- [ ] Unresolved reference in a rewritten file? → Read the file you wrote and verify every import path is correct

### 4. Fix ONE root cause
Change the single identified cause. Do not batch multiple unverified fixes.

### 5. Re-run and check
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug 2>&1 | tail -60
```

Repeat from step 2 if still failing.

### 6. When BUILD SUCCESSFUL
Report:
- Which file was changed
- What the root cause was
- Whether `.claude/memory/feedback_build_errors.md` needs updating with this new error type

---

## Common error patterns

| Error text | Root cause | Fix |
|---|---|---|
| `e: Unresolved reference 'X'` | Wrong import or KSP not run | Fix import or run `kspDebugKotlin` |
| `MissingBinding for X` (Hilt) | Class not in any `@Module` | Add to `RepositoryModule` or `DatabaseModule` |
| `NoSuchMethodError` at runtime | Stale APK from Apply Changes | `./gradlew clean assembleDebug` + full reinstall |
| `@StringRes cannot be type arg` | Annotation misuse | Use plain `Int` |
| `incompatible types` | KSP generated wrong code | `./gradlew clean kspDebugKotlin assembleDebug` |
| `Circular dependency` | Module imports another module | Move shared code to `:core:common` or `:core:domain` |
