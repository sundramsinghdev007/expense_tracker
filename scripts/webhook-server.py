#!/usr/bin/env python3
"""
webhook-server.py — Local GitHub webhook receiver for agentic feature implementation.

When a GitHub issue gets labeled 'ready-to-implement', this server:
  1. Validates the GitHub webhook signature (HMAC-SHA256)
  2. Extracts the issue title and body
  3. Runs scripts/agent.sh in the background (full cycle including device deploy)
  4. Posts status updates back to the GitHub issue

Environment variables:
  ANTHROPIC_API_KEY  (required) — your Anthropic API key
  WEBHOOK_SECRET     (required) — the secret you set when configuring the GitHub webhook
  GITHUB_TOKEN       (required) — a GitHub personal access token (issues:write scope)
  WEBHOOK_PORT       (optional, default 8080) — port to listen on
  LABEL_TRIGGER      (optional, default 'ready-to-implement') — label name to watch for

Usage:
  python3 scripts/webhook-server.py
"""

import hashlib
import hmac
import http.server
import json
import logging
import os
import subprocess
import sys
import threading
import urllib.request
from datetime import datetime

# ── Configuration ─────────────────────────────────────────────────────────────

PORT           = int(os.environ.get('WEBHOOK_PORT', '8080'))
WEBHOOK_SECRET = os.environ.get('WEBHOOK_SECRET', '')
GITHUB_TOKEN   = os.environ.get('GITHUB_TOKEN', '')
LABEL_TRIGGER  = os.environ.get('LABEL_TRIGGER', 'ready-to-implement')
PROJECT_DIR    = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))

# Serialize agent runs — only one implementation at a time
_agent_lock = threading.Lock()

# ── Logging ───────────────────────────────────────────────────────────────────

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s  %(levelname)-8s  %(message)s',
    datefmt='%H:%M:%S',
    stream=sys.stdout,
)
log = logging.getLogger('webhook')

# ── GitHub helpers ─────────────────────────────────────────────────────────────

def verify_signature(payload_bytes: bytes, signature_header: str) -> bool:
    """Validate the X-Hub-Signature-256 header using HMAC-SHA256."""
    if not WEBHOOK_SECRET:
        log.warning('WEBHOOK_SECRET not set — signature verification skipped (insecure)')
        return True
    if not signature_header or not signature_header.startswith('sha256='):
        return False
    mac      = hmac.new(WEBHOOK_SECRET.encode('utf-8'), payload_bytes, hashlib.sha256)
    expected = 'sha256=' + mac.hexdigest()
    return hmac.compare_digest(expected, signature_header)


def post_github_comment(repo_full_name: str, issue_number: int, body: str) -> None:
    """Post a comment on a GitHub issue via the REST API."""
    if not GITHUB_TOKEN:
        log.warning('GITHUB_TOKEN not set — cannot post GitHub comment')
        return
    url  = f'https://api.github.com/repos/{repo_full_name}/issues/{issue_number}/comments'
    data = json.dumps({'body': body}).encode('utf-8')
    req  = urllib.request.Request(url, data=data, headers={
        'Authorization': f'token {GITHUB_TOKEN}',
        'Content-Type':  'application/json',
        'Accept':        'application/vnd.github.v3+json',
        'User-Agent':    'expense-tracker-webhook/1.0',
    })
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            log.info(f'GitHub comment posted (HTTP {resp.status}) on issue #{issue_number}')
    except Exception as exc:
        log.error(f'Failed to post GitHub comment: {exc}')

# ── Agent runner ──────────────────────────────────────────────────────────────

def run_agent(description: str, repo_full_name: str, issue_number: int) -> None:
    """Run scripts/agent.sh in a serialized background thread."""
    with _agent_lock:
        log.info(f'Agent starting — issue #{issue_number}: {description[:80]}')

        post_github_comment(repo_full_name, issue_number, '\n'.join([
            '## 🤖 Claude Code is on it (local mode)',
            '',
            f'Running full cycle on your local machine:',
            '```',
            f'./scripts/agent.sh "{description[:80]}"',
            '```',
            '',
            '**Steps:** read context → plan → implement → build loop → tests → deploy → logcat',
            '',
            '_I will post an update when complete._',
        ]))

        start = datetime.now()
        try:
            result = subprocess.run(
                ['bash', 'scripts/agent.sh', description],
                cwd=PROJECT_DIR,
                capture_output=False,   # stream stdout/stderr to this terminal
                timeout=3600,           # 1 hour max per feature
            )

            elapsed = int((datetime.now() - start).total_seconds())

            if result.returncode == 0:
                log.info(f'Agent completed successfully in {elapsed}s')
                post_github_comment(repo_full_name, issue_number, '\n'.join([
                    f'## ✅ Implementation complete ({elapsed}s)',
                    '',
                    'Claude Code has:',
                    '- ✅ Implemented the feature following CLAUDE.md architecture rules',
                    '- ✅ Verified the build (`./gradlew assembleDebug` — PASSED)',
                    '- ✅ Unit tests passing',
                    '- ✅ Deployed to connected device',
                    '- ✅ Monitored logcat — no crashes',
                    '',
                    'Check your device. Run `git log --oneline -5` to see the commits.',
                ]))
            else:
                log.error(f'Agent exited with code {result.returncode} after {elapsed}s')
                post_github_comment(repo_full_name, issue_number, '\n'.join([
                    f'## ⚠️ Agent exited with error (code {result.returncode})',
                    '',
                    f'Elapsed: {elapsed}s',
                    '',
                    'Check the terminal output on your local machine for details.',
                    'You can retry by removing and re-applying the `ready-to-implement` label.',
                ]))

        except subprocess.TimeoutExpired:
            elapsed = int((datetime.now() - start).total_seconds())
            log.error(f'Agent timed out after {elapsed}s')
            post_github_comment(repo_full_name, issue_number, '\n'.join([
                '## ⏱️ Agent timed out (1 hour)',
                '',
                'The feature may be too large for a single automated run.',
                'Try splitting the issue into smaller pieces.',
            ]))

        except Exception as exc:
            log.error(f'Agent error: {exc}')
            post_github_comment(repo_full_name, issue_number,
                f'## ❌ Unexpected error\n\n```\n{exc}\n```')

# ── HTTP request handler ──────────────────────────────────────────────────────

class WebhookHandler(http.server.BaseHTTPRequestHandler):

    def do_GET(self):
        """Health-check endpoint."""
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(json.dumps({
            'status': 'running',
            'trigger_label': LABEL_TRIGGER,
            'agent_busy': _agent_lock.locked(),
        }).encode())

    def do_POST(self):
        # Read body
        length = int(self.headers.get('Content-Length', 0))
        body   = self.rfile.read(length)

        # Validate signature
        sig = self.headers.get('X-Hub-Signature-256', '')
        if not verify_signature(body, sig):
            log.warning(f'Invalid webhook signature from {self.client_address[0]} — rejected')
            self._respond(401, {'error': 'invalid signature'})
            return

        # Parse JSON
        try:
            payload = json.loads(body)
        except json.JSONDecodeError:
            self._respond(400, {'error': 'invalid JSON'})
            return

        event  = self.headers.get('X-GitHub-Event', '')
        action = payload.get('action', '')

        # Only handle issues.labeled
        if event != 'issues' or action != 'labeled':
            self._respond(200, {'status': 'ignored', 'event': event, 'action': action})
            return

        label_name = payload.get('label', {}).get('name', '')
        if label_name != LABEL_TRIGGER:
            self._respond(200, {'status': 'ignored', 'label': label_name})
            return

        # Extract issue data
        issue          = payload.get('issue', {})
        issue_number   = issue.get('number')
        issue_title    = issue.get('title', '')
        issue_body     = issue.get('body', '') or ''
        repo_full_name = payload.get('repository', {}).get('full_name', '')

        description = issue_title
        if issue_body.strip():
            description += '\n\n' + issue_body[:600]

        log.info(f'Accepted: issue #{issue_number} "{issue_title}" labeled "{LABEL_TRIGGER}"')

        # Respond immediately (GitHub expects < 10s)
        self._respond(202, {'status': 'accepted', 'issue': issue_number})

        # Run agent in background
        thread = threading.Thread(
            target=run_agent,
            args=(description, repo_full_name, issue_number),
            name=f'agent-issue-{issue_number}',
            daemon=True,
        )
        thread.start()

    def _respond(self, code: int, data: dict) -> None:
        body = json.dumps(data).encode()
        self.send_response(code)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, fmt, *args):
        log.debug(fmt % args)   # suppress default access log noise

# ── Entry point ───────────────────────────────────────────────────────────────

def main():
    missing = []
    if not os.environ.get('ANTHROPIC_API_KEY'):
        missing.append('ANTHROPIC_API_KEY')
    if missing:
        log.error(f'Missing required environment variables: {", ".join(missing)}')
        log.error('Export them and restart the server.')
        sys.exit(1)

    if not WEBHOOK_SECRET:
        log.warning('WEBHOOK_SECRET not set — anyone can trigger the agent!')
    if not GITHUB_TOKEN:
        log.warning('GITHUB_TOKEN not set — issue comments will be skipped')

    log.info(f'Webhook server starting on port {PORT}')
    log.info(f'Trigger label  : {LABEL_TRIGGER}')
    log.info(f'Project dir    : {PROJECT_DIR}')
    log.info(f'Signature check: {"enabled" if WEBHOOK_SECRET else "DISABLED"}')
    log.info(f'Health check   : http://localhost:{PORT}/')

    server = http.server.HTTPServer(('0.0.0.0', PORT), WebhookHandler)
    try:
        log.info(f'Ready — listening on http://0.0.0.0:{PORT}/')
        log.info('Press Ctrl+C to stop')
        server.serve_forever()
    except KeyboardInterrupt:
        log.info('Shutting down...')
        server.shutdown()


if __name__ == '__main__':
    main()
