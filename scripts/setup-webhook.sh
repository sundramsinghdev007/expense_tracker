#!/bin/bash
# setup-webhook.sh — One-command setup for the local agentic webhook pipeline.
#
# What it does:
#   1. Validates prerequisites (python3, ngrok, ANTHROPIC_API_KEY)
#   2. Creates / loads .env.webhook (webhook secret, port)
#   3. Starts the webhook server on localhost
#   4. Opens an ngrok tunnel to expose it to GitHub
#   5. Prints the exact URL and instructions to paste into GitHub webhook settings
#
# Usage:
#   ./scripts/setup-webhook.sh
#
# Prerequisites:
#   export ANTHROPIC_API_KEY=sk-ant-...
#   brew install ngrok/ngrok/ngrok          (macOS)
#   — OR —
#   brew install cloudflare/cloudflare/cloudflared   (alternative)

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
CYAN='\033[0;36m'
NC='\033[0m'

cd "$(dirname "$0")/.."

banner() {
  echo ""
  echo -e "${BOLD}━━━ $1 ━━━${NC}"
  echo ""
}

# ── 1. Validate prerequisites ─────────────────────────────────────────────────

banner "Agentic Webhook Setup"

MISSING=()
[ -z "$ANTHROPIC_API_KEY" ] && MISSING+=("ANTHROPIC_API_KEY")
command -v python3 &>/dev/null || MISSING+=("python3")

if [ ${#MISSING[@]} -gt 0 ]; then
  echo -e "${RED}✗ Missing prerequisites:${NC}"
  for M in "${MISSING[@]}"; do
    echo -e "  ${RED}•${NC} $M"
  done
  echo ""
  [ -z "$ANTHROPIC_API_KEY" ] && \
    echo -e "  Set API key: ${CYAN}export ANTHROPIC_API_KEY=sk-ant-...${NC}"
  exit 1
fi

# Check tunnel tool (ngrok or cloudflared)
TUNNEL_CMD=""
if command -v ngrok &>/dev/null; then
  TUNNEL_CMD="ngrok"
elif command -v cloudflared &>/dev/null; then
  TUNNEL_CMD="cloudflared"
else
  echo -e "${RED}✗ No tunnel tool found. Install one:${NC}"
  echo -e "  macOS:  ${CYAN}brew install ngrok/ngrok/ngrok${NC}"
  echo -e "  macOS:  ${CYAN}brew install cloudflare/cloudflare/cloudflared${NC} (free, no account)"
  echo -e "  Linux:  ${CYAN}snap install ngrok${NC}"
  echo -e "  Any:    ${CYAN}https://ngrok.com/download${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Prerequisites satisfied${NC}"
echo -e "  Tunnel: ${CYAN}$TUNNEL_CMD${NC}"

# ── 2. Configure / load webhook secret ───────────────────────────────────────

ENV_FILE=".env.webhook"

if [ -f "$ENV_FILE" ]; then
  # shellcheck source=/dev/null
  source "$ENV_FILE"
  echo -e "${GREEN}✓ Loaded existing config from ${ENV_FILE}${NC}"
else
  echo ""
  echo -e "${YELLOW}First-time setup — creating ${ENV_FILE}${NC}"
  echo ""
  echo -n "  Webhook secret (press Enter to auto-generate): "
  read -r SECRET_INPUT

  if [ -z "$SECRET_INPUT" ]; then
    WEBHOOK_SECRET=$(python3 -c "import secrets; print(secrets.token_hex(32))")
    echo -e "  ${GREEN}Generated: ${WEBHOOK_SECRET}${NC}"
  else
    WEBHOOK_SECRET="$SECRET_INPUT"
  fi

  WEBHOOK_PORT="${WEBHOOK_PORT:-8080}"

  {
    echo "export WEBHOOK_SECRET=$WEBHOOK_SECRET"
    echo "export WEBHOOK_PORT=$WEBHOOK_PORT"
  } > "$ENV_FILE"

  echo -e "  ${GREEN}✓ Saved to ${ENV_FILE}${NC}"
  echo -e "  ${YELLOW}Add ${ENV_FILE} to .gitignore if not already there.${NC}"
fi

WEBHOOK_PORT="${WEBHOOK_PORT:-8080}"

# Make sure .env.webhook is gitignored
if [ -f ".gitignore" ] && ! grep -q ".env.webhook" .gitignore; then
  echo ".env.webhook" >> .gitignore
  echo -e "${GREEN}✓ Added .env.webhook to .gitignore${NC}"
fi

# ── 3. Start webhook server ───────────────────────────────────────────────────

echo ""
echo -e "${YELLOW}▶ Starting webhook server on port ${WEBHOOK_PORT}...${NC}"

export WEBHOOK_SECRET WEBHOOK_PORT ANTHROPIC_API_KEY GITHUB_TOKEN LABEL_TRIGGER

python3 scripts/webhook-server.py &
SERVER_PID=$!

# Give it a moment to start
sleep 2

if ! kill -0 "$SERVER_PID" 2>/dev/null; then
  echo -e "${RED}✗ Webhook server failed to start. Check the output above for errors.${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Webhook server running (PID ${SERVER_PID})${NC}"

# ── 4. Start tunnel ───────────────────────────────────────────────────────────

echo ""
echo -e "${YELLOW}▶ Starting ${TUNNEL_CMD} tunnel on port ${WEBHOOK_PORT}...${NC}"

PUBLIC_URL=""

if [ "$TUNNEL_CMD" = "ngrok" ]; then
  ngrok http "$WEBHOOK_PORT" --log=stdout > /tmp/ngrok.log 2>&1 &
  TUNNEL_PID=$!
  sleep 4

  # Read URL from ngrok's local API
  PUBLIC_URL=$(python3 -c "
import urllib.request, json, sys
try:
    with urllib.request.urlopen('http://localhost:4040/api/tunnels', timeout=5) as r:
        d = json.loads(r.read())
        tunnels = d.get('tunnels', [])
        https = [t['public_url'] for t in tunnels if t['proto'] == 'https']
        print(https[0] if https else (tunnels[0]['public_url'] if tunnels else ''))
except Exception as e:
    print('')
" 2>/dev/null)

elif [ "$TUNNEL_CMD" = "cloudflared" ]; then
  cloudflared tunnel --url "http://localhost:${WEBHOOK_PORT}" --no-autoupdate \
    > /tmp/cloudflared.log 2>&1 &
  TUNNEL_PID=$!
  sleep 5

  # Parse URL from cloudflared log
  PUBLIC_URL=$(grep -o 'https://[^ ]*\.trycloudflare\.com' /tmp/cloudflared.log 2>/dev/null | head -1 || true)
fi

if [ -z "$PUBLIC_URL" ]; then
  echo -e "${YELLOW}⚠ Could not auto-detect public URL.${NC}"
  echo -e "  Check ${TUNNEL_CMD} output manually."
  PUBLIC_URL="https://<your-tunnel-url>"
fi

WEBHOOK_URL="${PUBLIC_URL}/webhook"

# ── 5. Print setup instructions ───────────────────────────────────────────────

echo ""
echo -e "${GREEN}${BOLD}━━━ Setup Complete ━━━${NC}"
echo ""
echo -e "${BOLD}Your webhook URL:${NC}"
echo -e "  ${CYAN}${WEBHOOK_URL}${NC}"
echo ""
echo -e "${BOLD}Configure GitHub (one-time setup):${NC}"
echo -e "  1. Open: ${CYAN}https://github.com/<owner>/<repo>/settings/hooks/new${NC}"
echo -e "  2. Payload URL   → ${CYAN}${WEBHOOK_URL}${NC}"
echo -e "  3. Content type  → ${CYAN}application/json${NC}"
echo -e "  4. Secret        → ${CYAN}${WEBHOOK_SECRET}${NC}"
echo -e "  5. Which events? → Select ${CYAN}\"Issues\"${NC} only"
echo -e "  6. Click ${CYAN}\"Add webhook\"${NC}"
echo ""
echo -e "${BOLD}Create the label (one-time setup):${NC}"
echo -e "  ${CYAN}https://github.com/<owner>/<repo>/labels${NC}"
echo -e "  → New label → Name: ${CYAN}ready-to-implement${NC} → Color: #0075ca"
echo ""
echo -e "${BOLD}Trigger a feature:${NC}"
echo -e "  Create a GitHub issue → apply label ${CYAN}ready-to-implement${NC}"
echo -e "  Claude will comment on the issue and start working immediately."
echo ""
echo -e "${BOLD}Health check:${NC}"
echo -e "  ${CYAN}curl http://localhost:${WEBHOOK_PORT}/${NC}"
echo ""
echo -e "${YELLOW}Keep this terminal open. Press Ctrl+C to stop everything.${NC}"
echo ""

# ── 6. Cleanup on exit ────────────────────────────────────────────────────────

cleanup() {
  echo ""
  echo -e "${YELLOW}Shutting down...${NC}"
  kill "$SERVER_PID"  2>/dev/null || true
  kill "$TUNNEL_PID"  2>/dev/null || true
  echo -e "${GREEN}Done.${NC}"
}

trap cleanup EXIT INT TERM

# Wait for server process
wait "$SERVER_PID"
