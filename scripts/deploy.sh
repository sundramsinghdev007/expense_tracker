#!/bin/bash
# deploy.sh — Build, install, and launch the app on the connected device.
# Usage:  ./scripts/deploy.sh
#         ./scripts/deploy.sh --clean   (full clean build before install)

set -e

ADB="$HOME/Library/Android/sdk/platform-tools/adb"
PACKAGE="com.sundram.expense_tracker"
APK="app/build/outputs/apk/debug/app-debug.apk"
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

cd "$(dirname "$0")/.."

# ── 1. Check device ──────────────────────────────────────────────────────────
echo -e "${YELLOW}▶ Checking device...${NC}"
DEVICE=$("$ADB" devices | grep -v "List of devices" | grep "device$" | head -1 | awk '{print $1}')
if [ -z "$DEVICE" ]; then
  echo -e "${RED}✗ No device connected. Connect a device or start an emulator.${NC}"
  exit 1
fi
echo -e "${GREEN}  Device: $DEVICE${NC}"

# ── 2. Build ─────────────────────────────────────────────────────────────────
if [ "$1" == "--clean" ]; then
  echo -e "${YELLOW}▶ Clean build...${NC}"
  ./gradlew clean assembleDebug
else
  echo -e "${YELLOW}▶ Building (incremental)...${NC}"
  ./gradlew assembleDebug
fi

if [ $? -ne 0 ]; then
  echo -e "${RED}✗ Build failed. Fix errors above before deploying.${NC}"
  exit 1
fi
echo -e "${GREEN}  Build SUCCESS${NC}"

# ── 3. Install ───────────────────────────────────────────────────────────────
echo -e "${YELLOW}▶ Installing APK...${NC}"
"$ADB" -s "$DEVICE" install -r "$APK"
echo -e "${GREEN}  Installed${NC}"

# ── 4. Launch ────────────────────────────────────────────────────────────────
echo -e "${YELLOW}▶ Launching app...${NC}"
"$ADB" -s "$DEVICE" shell am start -n "$PACKAGE/$PACKAGE.MainActivity"
echo -e "${GREEN}  App launched on $DEVICE${NC}"

echo ""
echo -e "${GREEN}✓ Done. Run ./scripts/logcat.sh to monitor logs.${NC}"
