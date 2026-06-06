#!/usr/bin/env bash
# demo_reset.sh
# ─────────────────────────────────────────────────────────────────────────────
# Resets the Swipey backend DB, re-seeds 30 fresh idea cards, restarts the
# backend server, rebuilds the Android app, and installs it on the emulator.
# Run from the project root: ./demo_reset.sh
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend-swipey"
ADB="$HOME/Library/Android/sdk/platform-tools/adb"
VENV="$BACKEND_DIR/.venv/bin/python"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()    { echo -e "${GREEN}▶ $*${NC}"; }
warn()    { echo -e "${YELLOW}⚠ $*${NC}"; }
die()     { echo -e "${RED}✖ $*${NC}"; exit 1; }

# ── 1. Kill any running backend ───────────────────────────────────────────────
info "Stopping any running uvicorn on port 8000…"
lsof -ti tcp:8000 | xargs kill -9 2>/dev/null && echo "  killed" || echo "  nothing to kill"

# ── 2. Reset + re-seed the database ──────────────────────────────────────────
info "Resetting database…"
rm -rf "$BACKEND_DIR/data"

info "Seeding 30 fresh cards (10 tech · 10 gaming · 10 fashion)…"
cd "$BACKEND_DIR"
"$VENV" - <<'PY'
from app.database import init_db
init_db()
import sqlite3, os
db = os.path.join(os.path.dirname(__file__), 'data', 'swipey.db')
c = sqlite3.connect(db)
rows = dict(c.execute('SELECT niche, COUNT(*) FROM ideas GROUP BY niche'))
total = sum(rows.values())
print(f"  Seeded {total} ideas: {rows}")
PY

# ── 3. Start the backend (background) ────────────────────────────────────────
info "Starting backend server on 0.0.0.0:8000…"
cd "$BACKEND_DIR"
nohup .venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000 \
    > /tmp/swipey_backend.log 2>&1 &
BACKEND_PID=$!
echo "  PID $BACKEND_PID  (logs: /tmp/swipey_backend.log)"

# Wait for the server to be ready.
echo -n "  Waiting for health check"
for i in $(seq 1 20); do
    sleep 1
    if curl -s -m 2 http://localhost:8000/api/v1/health > /dev/null 2>&1; then
        echo " ✓"
        break
    fi
    echo -n "."
    if [ "$i" -eq 20 ]; then
        echo ""
        die "Backend did not start within 20 s. Check /tmp/swipey_backend.log"
    fi
done

HEALTH=$(curl -s http://localhost:8000/api/v1/health)
echo "  $HEALTH"

# ── 4. Build + install the Android app ───────────────────────────────────────
cd "$PROJECT_ROOT"

# Find the first connected physical device (non-emulator).
DEVICE_SERIAL=$("$ADB" devices | grep -v "emulator" | grep "device$" | awk '{print $1}' | head -1)

if [ -z "$DEVICE_SERIAL" ]; then
    warn "No physical device detected — skipping installDebug."
    warn "Connect your phone via USB with USB debugging enabled."
else
    info "Physical device found: $DEVICE_SERIAL"
    info "Building and installing debug APK on $DEVICE_SERIAL..."
    ANDROID_SERIAL="$DEVICE_SERIAL" ./gradlew installDebug --quiet
    echo "  Installed."

    # Clear app data so onboarding runs fresh.
    info "Clearing app data on $DEVICE_SERIAL (fresh onboarding)..."
    "$ADB" -s "$DEVICE_SERIAL" shell pm clear com.harsh.swipey > /dev/null && echo "  App data cleared."
fi

# ── 5. Summary ────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  Demo reset complete!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  Backend : http://localhost:8000  (PID $BACKEND_PID)"
echo "  Swagger  : http://localhost:8000/docs"
LAN=$(ipconfig getifaddr en0 2>/dev/null || true)
[ -n "$LAN" ] && echo "  LAN (phone): http://$LAN:8000/api/v1/health"
echo ""
echo "  Device   : open Swipey on your phone, pick a niche, swipe away."
echo "  Logs     : tail -f /tmp/swipey_backend.log"
echo ""
