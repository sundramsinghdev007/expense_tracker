#!/bin/bash
# logcat.sh — Stream filtered logcat for the app. Shows errors, crashes, and debug logs.
# Usage:  ./scripts/logcat.sh           (all app logs)
#         ./scripts/logcat.sh --errors  (errors + crashes only)
#         ./scripts/logcat.sh --clear   (clear buffer then stream)

ADB="$HOME/Library/Android/sdk/platform-tools/adb"
PACKAGE="com.sundram.expense_tracker"
RED='\033[0;31m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'

cd "$(dirname "$0")/.."

DEVICE=$("$ADB" devices | grep -v "List of devices" | grep "device$" | head -1 | awk '{print $1}')
if [ -z "$DEVICE" ]; then
  echo -e "${RED}✗ No device connected.${NC}"
  exit 1
fi

if [ "$1" == "--clear" ] || [ "$2" == "--clear" ]; then
  "$ADB" -s "$DEVICE" logcat -c
  echo -e "${YELLOW}  Log buffer cleared.${NC}"
fi

echo -e "${CYAN}▶ Streaming logcat for $PACKAGE on $DEVICE (Ctrl+C to stop)...${NC}"
echo ""

if [ "$1" == "--errors" ]; then
  # Errors and crashes only
  "$ADB" -s "$DEVICE" logcat "*:E" | grep --line-buffered -i "$PACKAGE\|AndroidRuntime\|FATAL"
else
  # All app logs + system errors relevant to the app
  "$ADB" -s "$DEVICE" logcat | grep --line-buffered -i \
    "$PACKAGE\|AndroidRuntime\|FATAL EXCEPTION\|System.err"
fi
