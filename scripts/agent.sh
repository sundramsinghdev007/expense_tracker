#!/bin/bash
# agent.sh — Headless Claude Code automation for the full development lifecycle.
#
# Usage:
#   ./scripts/agent.sh "Add filter chips to the analytics screen"
#   ./scripts/agent.sh implement "Add recurring expense support"
#   ./scripts/agent.sh fix-build
#   ./scripts/agent.sh deploy-verify
#   ./scripts/agent.sh review
#
# Prerequisites:
#   - ANTHROPIC_API_KEY must be set in your environment
#   - claude CLI must be on PATH (npm i -g @anthropic-ai/claude-code)
#   - Android SDK + ADB for deploy-verify mode

set -e

export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BOLD='\033[1m'; CYAN='\033[0;36m'; NC='\033[0m'

cd "$(dirname "$0")/.."

# ── Validate environment ─────────────────────────────────────────────────────
if ! command -v claude &>/dev/null; then
  echo -e "${RED}✗ claude CLI not found. Install: npm install -g @anthropic-ai/claude-code${NC}"
  exit 1
fi

if [ -z "$ANTHROPIC_API_KEY" ]; then
  echo -e "${RED}✗ ANTHROPIC_API_KEY is not set.${NC}"
  echo -e "  Export it: ${CYAN}export ANTHROPIC_API_KEY=sk-ant-...${NC}"
  exit 1
fi

# ── Usage ────────────────────────────────────────────────────────────────────
usage() {
  echo -e "${BOLD}Usage:${NC}"
  echo -e "  ${CYAN}./scripts/agent.sh \"<feature description>\"${NC}          — Full lifecycle (plan→code→build→deploy→verify)"
  echo -e "  ${CYAN}./scripts/agent.sh implement \"<feature description>\"${NC} — Same as above"
  echo -e "  ${CYAN}./scripts/agent.sh fix-build${NC}                          — Fix current build errors in a loop"
  echo -e "  ${CYAN}./scripts/agent.sh deploy-verify${NC}                      — Deploy to device + monitor logcat"
  echo -e "  ${CYAN}./scripts/agent.sh review${NC}                             — Review recent code changes"
  echo ""
  echo -e "${BOLD}Examples:${NC}"
  echo -e "  ./scripts/agent.sh \"Add recurring expense support with weekly/monthly/yearly options\""
  echo -e "  ./scripts/agent.sh implement \"Export expenses to CSV from the Settings screen\""
  echo -e "  ./scripts/agent.sh fix-build"
  exit 1
}

[ $# -eq 0 ] && usage

# ── Common claude flags ───────────────────────────────────────────────────────
TOOLS="Read,Write,Edit,Bash,Glob,Grep,Agent"
TOOLS_READONLY="Read,Glob,Grep,Bash"

run_agent() {
  local PROMPT="$1"
  local ALLOWED_TOOLS="${2:-$TOOLS}"
  echo ""
  claude -p "$PROMPT" --allowedTools "$ALLOWED_TOOLS"
}

# ── Dispatch ─────────────────────────────────────────────────────────────────
CMD="$1"
shift || true

case "$CMD" in

  implement|feat|feature)
    DESCRIPTION="$*"
    if [ -z "$DESCRIPTION" ]; then
      echo -e "${RED}Error: provide a feature description after 'implement'${NC}"
      usage
    fi
    echo -e "\n${BOLD}━━━ Feature Implementation Agent ━━━${NC}"
    echo -e "${YELLOW}Feature:${NC} $DESCRIPTION\n"
    run_agent "/implement-feature $DESCRIPTION"
    ;;

  fix-build|fix|build)
    echo -e "\n${BOLD}━━━ Build Fix Agent ━━━${NC}\n"
    run_agent "/fix-build-error"
    ;;

  deploy|deploy-verify|verify-device)
    echo -e "\n${BOLD}━━━ Deploy + Verify Agent ━━━${NC}\n"
    run_agent "/deploy-and-verify" "Bash"
    ;;

  review)
    echo -e "\n${BOLD}━━━ Code Review Agent ━━━${NC}\n"
    run_agent "/review-changes" "$TOOLS_READONLY"
    ;;

  help|--help|-h)
    usage
    ;;

  *)
    # Treat the entire argument list as a feature description
    DESCRIPTION="$CMD $*"
    echo -e "\n${BOLD}━━━ Feature Implementation Agent ━━━${NC}"
    echo -e "${YELLOW}Feature:${NC} $DESCRIPTION\n"
    run_agent "/implement-feature $DESCRIPTION"
    ;;

esac
