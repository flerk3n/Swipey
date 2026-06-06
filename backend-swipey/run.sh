#!/usr/bin/env bash
# Start the Swipey backend, reachable from other devices on the same Wi-Fi.
set -euo pipefail

cd "$(dirname "$0")"

HOST="${HOST:-0.0.0.0}"
PORT="${PORT:-8000}"

# Prefer the project venv if it exists.
if [ -f ".venv/bin/activate" ]; then
  # shellcheck disable=SC1091
  source .venv/bin/activate
fi

echo "Swipey backend starting on http://${HOST}:${PORT}"
echo "Swagger UI:  http://localhost:${PORT}/docs"

LAN_IP="$(ipconfig getifaddr en0 2>/dev/null || true)"
if [ -n "${LAN_IP}" ]; then
  echo "Phone (same Wi-Fi):  http://${LAN_IP}:${PORT}/api/v1/health"
fi

exec uvicorn app.main:app --host "${HOST}" --port "${PORT}" --reload
