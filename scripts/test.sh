#!/bin/bash
# test.sh — Run lint, unit tests, and (optionally) instrumented tests.
# Usage:  ./scripts/test.sh              (lint + unit tests)
#         ./scripts/test.sh --module add-expense  (single module unit tests)
#         ./scripts/test.sh --instrumented        (unit + connected Android tests)

set -e

export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ADB="$HOME/Library/Android/sdk/platform-tools/adb"

cd "$(dirname "$0")/.."

FAILED=0

run_step() {
  local label="$1"; shift
  echo -e "${YELLOW}▶ $label...${NC}"
  if "$@"; then
    echo -e "${GREEN}  ✓ $label passed${NC}\n"
  else
    echo -e "${RED}  ✗ $label FAILED${NC}\n"
    FAILED=1
  fi
}

# ── Single module mode ───────────────────────────────────────────────────────
if [ "$1" == "--module" ] && [ -n "$2" ]; then
  MODULE="$2"
  run_step "Unit tests: feature/$MODULE" ./gradlew ":feature:$MODULE:test"
  [ $FAILED -eq 0 ] && echo -e "${GREEN}✓ All checks passed.${NC}" || echo -e "${RED}✗ Checks failed.${NC}"
  exit $FAILED
fi

# ── Full test suite ──────────────────────────────────────────────────────────
run_step "Lint"        ./gradlew lint
run_step "Unit tests"  ./gradlew test

# ── Instrumented tests (requires connected device) ───────────────────────────
if [ "$1" == "--instrumented" ]; then
  DEVICE=$("$ADB" devices | grep -v "List of devices" | grep "device$" | head -1 | awk '{print $1}')
  if [ -z "$DEVICE" ]; then
    echo -e "${RED}✗ No device connected for instrumented tests.${NC}"
    FAILED=1
  else
    run_step "Instrumented tests on $DEVICE" ./gradlew connectedDebugAndroidTest
  fi
fi

# ── Summary ──────────────────────────────────────────────────────────────────
echo ""
if [ $FAILED -eq 0 ]; then
  echo -e "${GREEN}✓ All checks passed. Safe to merge / ship.${NC}"
else
  echo -e "${RED}✗ One or more checks failed. See output above.${NC}"
fi
exit $FAILED
