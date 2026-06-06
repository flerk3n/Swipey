# Swipey Backend

A small, local-first FastAPI service that feeds the Swipey Android app a deck of content
ideas, records swipes, learns from rejections, and (optionally) generates fresh ideas and
expanded scripts with Google Gemini.

Runs on one laptop; reachable from your phone on the same Wi-Fi. See `PRD.md` for the full
spec.

> The core swipe loop works **without** a Gemini key — 20 seed ideas (tech reviews + gaming)
> are served from SQLite. A key only unlocks `/generate` and `/ideas/{id}/echo`.

---

## Quick start

```bash
cd backend-swipey
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env          # optional: add GEMINI_API_KEY
./run.sh                      # uvicorn on 0.0.0.0:8000
```

- Swagger UI: http://localhost:8000/docs
- Health: http://localhost:8000/api/v1/health

The SQLite file is created automatically at `data/swipey.db` and seeded on first run.

---

## Connect your phone (same Wi-Fi)

1. Find your laptop's LAN IP:
   ```bash
   ipconfig getifaddr en0      # macOS Wi-Fi
   ```
2. From the phone's browser, confirm it reaches:
   `http://<LAPTOP_IP>:8000/api/v1/health`
3. Point the Android app's base URL at `http://<LAPTOP_IP>:8000/api/v1/`.

### Android cleartext (HTTP) note

Local dev is plain HTTP, which Android blocks by default. Allow cleartext for your LAN IP
via a network security config (do **not** ship this to production):

`app/src/main/res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>   <!-- emulator -> host -->
        <domain includeSubdomains="true">192.168.0.0</domain> <!-- replace with your LAN IP -->
    </domain-config>
</network-security-config>
```
Reference it in `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```
The emulator reaches the host machine at `http://10.0.2.2:8000/`.

---

## API (all under `/api/v1`)

| Method | Path | Purpose |
| :- | :- | :- |
| GET | `/health` | liveness + whether Gemini is configured |
| GET | `/ideas/deck?limit=20` | pending deck for the swipe screen |
| GET | `/ideas/next` | a single pending idea |
| POST | `/ideas/{id}/swipe` | body `{ "action": "approve" \| "reject" \| "save" }` |
| GET | `/ideas/saved` | saved/approved ideas |
| DELETE | `/ideas/{id}/saved` | unsave |
| POST | `/ideas/{id}/echo` | generate + return the expanded full text (Gemini) |
| POST | `/generate` | body `{ "niche", "count" }` → run the generation pipeline |
| GET | `/catalog/niches` | the showcase niches |

Idea responses match the app's `IdeaModel`: `{ id, category, title, description, tags }`.

The acting user comes from the `X-User-Id` header (defaults to `demo`).

### Examples

```bash
BASE=http://localhost:8000/api/v1

curl $BASE/health
curl "$BASE/ideas/deck?limit=5"

# swipe right (approve) on an idea you got from the deck
curl -X POST $BASE/ideas/<IDEA_ID>/swipe -H 'Content-Type: application/json' \
  -d '{"action":"approve"}'

curl $BASE/ideas/saved

# generation (needs GEMINI_API_KEY)
curl -X POST $BASE/generate -H 'Content-Type: application/json' \
  -d '{"niche":"gaming","count":5}'

curl -X POST $BASE/ideas/<IDEA_ID>/echo
```

---

## How it works

- **SQLite** (`app/database.py`, `app/schema.sql`) — idempotent schema, `UNIQUE(user_id, title)`
  keeps ingestion duplicate-free.
- **Seed** (`app/seed.py`) — 10 tech + 10 gaming ideas, plus curated trend snippets per niche.
- **Generation** (`app/services/gemini.py`) — one Gemini call returns N structured ideas
  (title, description, full_text, content_type, tags, 10-word `summary_tokens`).
- **Dedup** (`app/services/dedup.py`) — Jaccard overlap on `summary_tokens`, threshold `0.6`.
- **Feedback / RAG-lite** (`app/services/feedback.py`) — rejecting an idea stores negative
  markers that are injected as "avoid these" guidance into future generation prompts.

Everything degrades gracefully: with no key, generation endpoints return `503` and the seed
deck keeps the swipe loop fully working.

---

## Configuration (`.env`)

| Var | Default | Notes |
| :- | :- | :- |
| `GEMINI_API_KEY` | _(empty)_ | optional; enables generation + echo |
| `GEMINI_MODEL` | `gemini-3.1-flash-lite` | model name |
| `DATABASE_PATH` | `data/swipey.db` | relative to `backend-swipey/` |
| `DEFAULT_USER_ID` | `demo` | used when `X-User-Id` is absent |
| `DEDUP_THRESHOLD` | `0.6` | higher = stricter dedup |

---

## Roadmap

Phases 0–6 (scaffold → schema → core API + seed → Gemini generation → dedup → RAG feedback →
expanded echo) are implemented. Phase 7 is the Android wiring above; Notion (Phase 8) and
Google Calendar (Phase 9) sync come later.
