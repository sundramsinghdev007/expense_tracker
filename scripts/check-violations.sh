#!/bin/bash
# check-violations.sh — Quick CLAUDE.md rule check on recently edited Kotlin files.
# Called automatically by Claude Code hook after every Edit/Write tool use.
# Usage: ./scripts/check-violations.sh [optional-file-path]

RED='\033[0;31m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'; NC='\033[0m'

cd "$(dirname "$0")/.."

# ── Resolve target files ─────────────────────────────────────────────────────
FILE_ARG="${1:-}"
if [ -n "$FILE_ARG" ] && [ -f "$FILE_ARG" ] && [[ "$FILE_ARG" == *.kt ]]; then
  FILES="$FILE_ARG"
else
  # Check all Kotlin files modified in the last 60 seconds, excluding build dirs
  FILES=$(find . -name "*.kt" -newer .claude/settings.json \
    -not -path "*/build/*" \
    -not -path "*/.gradle/*" \
    2>/dev/null | head -10)
fi

if [ -z "$FILES" ]; then
  exit 0
fi

VIOLATIONS=0

for F in $FILES; do
  [ -f "$F" ] || continue
  # Only check feature and app source — not tests, not core/ui/Dimens.kt
  [[ "$F" == *"/test/"* ]] && continue
  [[ "$F" == *"/androidTest/"* ]] && continue

  # ── Check 1: Hardcoded string in Text() ───────────────────────────────────
  MATCHES=$(grep -n 'Text("' "$F" 2>/dev/null | grep -v "^\s*//" || true)
  if [ -n "$MATCHES" ]; then
    echo -e "${RED}[VIOLATION] Hardcoded string in Text() — use stringResource():${NC}"
    echo "$MATCHES" | sed "s|^|  $F:|"
    VIOLATIONS=$((VIOLATIONS+1))
  fi

  # ── Check 2: Magic .dp value (not in Dimens.kt definition) ────────────────
  if [[ "$F" != *"Dimens.kt"* ]]; then
    MATCHES=$(grep -n '\b[0-9]\+\.dp\b' "$F" 2>/dev/null | grep -v "^\s*//" || true)
    if [ -n "$MATCHES" ]; then
      echo -e "${RED}[VIOLATION] Magic .dp value — use Dimens.kt tokens:${NC}"
      echo "$MATCHES" | sed "s|^|  $F:|"
      VIOLATIONS=$((VIOLATIONS+1))
    fi
  fi

  # ── Check 3: !! null assertion ─────────────────────────────────────────────
  MATCHES=$(grep -n '!!' "$F" 2>/dev/null | grep -v "^\s*//" || true)
  if [ -n "$MATCHES" ]; then
    echo -e "${RED}[VIOLATION] !! null assertion — use ?.let, requireNotNull(), or ?::${NC}"
    echo "$MATCHES" | sed "s|^|  $F:|"
    VIOLATIONS=$((VIOLATIONS+1))
  fi

  # ── Check 4: GlobalScope ──────────────────────────────────────────────────
  MATCHES=$(grep -n 'GlobalScope' "$F" 2>/dev/null | grep -v "^\s*//" || true)
  if [ -n "$MATCHES" ]; then
    echo -e "${RED}[VIOLATION] GlobalScope — use viewModelScope or lifecycleScope:${NC}"
    echo "$MATCHES" | sed "s|^|  $F:|"
    VIOLATIONS=$((VIOLATIONS+1))
  fi

  # ── Check 5: LazyColumn items without key ─────────────────────────────────
  MATCHES=$(grep -n 'items(' "$F" 2>/dev/null | grep -v "key\s*=" | grep -v "^\s*//" || true)
  if [ -n "$MATCHES" ]; then
    echo -e "${YELLOW}[WARNING] LazyColumn items() may be missing key = { it.id }:${NC}"
    echo "$MATCHES" | sed "s|^|  $F:|"
  fi
done

# ── Summary ───────────────────────────────────────────────────────────────────
if [ $VIOLATIONS -eq 0 ]; then
  echo -e "${GREEN}[check-violations] No CLAUDE.md violations in edited files.${NC}"
fi

exit 0   # never block Claude — violations are warnings, not hard stops
