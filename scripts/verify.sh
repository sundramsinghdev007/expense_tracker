#!/bin/bash
# verify.sh — Full CI-equivalent check: clean → lint → test → build → install → launch.
#             This is what Claude Code runs after implementing a feature end-to-end.
# Usage:  ./scripts/verify.sh
#         ./scripts/verify.sh --skip-install  (stop after assembleDebug, no device needed)

set -e

ADB="$HOME/Library/Android/sdk/platform-tools/adb"
PACKAGE="com.sundram.expense_tracker"
APK="app/build/outputs/apk/debug/app-debug.apk"
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BOLD='\033[1m'; NC='\033[0m'

cd "$(dirname "$0")/.."

SKIP_INSTALL=false
[ "$1" == "--skip-install" ] && SKIP_INSTALL=true

PASS=0; FAIL=0
step() {
  local label="$1"; shift
  echo -e "${YELLOW}▶ $label${NC}"
  if "$@"; then
    echo -e "${GREEN}  ✓ PASSED${NC}\n"
    PASS=$((PASS+1))
  else
    echo -e "${RED}  ✗ FAILED — stopping${NC}\n"
    FAIL=$((FAIL+1))
    echo -e "${RED}${BOLD}Pipeline stopped at: $label${NC}"
    exit 1
  fi
}

echo -e "${BOLD}━━━ Full Verify Pipeline ━━━${NC}\n"

step "1/5  Clean"         ./gradlew clean
step "2/5  Lint"          ./gradlew lint
step "3/5  Unit Tests"    ./gradlew test
step "4/5  Assemble"      ./gradlew assembleDebug

if [ "$SKIP_INSTALL" = false ]; then
  DEVICE=$("$ADB" devices | grep -v "List of devices" | grep "device$" | head -1 | awk '{print $1}')
  if [ -z "$DEVICE" ]; then
    echo -e "${YELLOW}  No device found — skipping install/launch.${NC}\n"
  else
    step "5/5  Install + Launch" bash -c "
      '$ADB' -s '$DEVICE' install -r '$APK' &&
      '$ADB' -s '$DEVICE' shell am start -n '$PACKAGE/$PACKAGE.MainActivity'
    "
  fi
fi

echo -e "${GREEN}${BOLD}━━━ All $PASS steps passed ━━━${NC}"
echo -e "Run ${CYAN}./scripts/logcat.sh${NC} to monitor the app."
