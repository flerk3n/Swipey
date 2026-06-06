# Swipey Backend — PRD (Local-First, Gemini-Powered)

A small, runnable backend that feeds the Swipey Android app a deck of content ideas, records
swipes, learns from rejections, and (optionally) generates fresh ideas + expanded scripts with
Gemini. Built to run on **one laptop** and be reachable by a **phone on the same Wi-Fi**.

> This supersedes the brief notes in `/backend.md` and `/docs/BACKEND_INTEGRATION.md`. It is
> intentionally minimal: no auth, no message queue, no vector DB — just enough to demo the loop
> end-to-end and grow later.

---

## 1. Goal

Make the existing app's swipe loop work against a real API:
**deck → swipe (approve / reject / save) → saved list → expand an idea**, plus a one-command
"generate more ideas" pipeline. Showcase **two niches**: `tech_reviews` and `gaming`.

Non-goals for v1: real scraping, auth, multi-user, Notion/Calendar (these are later phases).

---

## 2. Stack (chosen for simplicity)

| Concern | Choice | Why |
| :- | :- | :- |
| Language/API | Python 3.9 + **FastAPI** + Uvicorn | Tiny, async, auto OpenAPI docs at `/docs` |
| Storage | **SQLite** (file in `data/`) | Zero setup; perfect for a laptop demo |
| LLM | **Gemini** (`gemini-3.1-flash-lite`) | Fast, low-cost; one SDK call |
| Config | `.env` via `python-dotenv` | Keep the API key out of code |
| User model | single `demo` user (header `X-User-Id`, default `demo`) | No auth needed for the demo |

**Degrades gracefully:** with no `GEMINI_API_KEY`, seed ideas still serve the full loop;
generation endpoints return `503` with a clear message.

---

## 3. Data model (SQLite)

`ideas`
| col | type | notes |
| :- | :- | :- |
| id | TEXT PK | uuid |
| user_id | TEXT | owner / feed scope (`demo`) |
| niche | TEXT | `tech_reviews` \| `gaming` |
| content_type | TEXT | `tweet`,`short_form`,`long_form`,`post`,`live_stream` |
| title | TEXT | the hook (card title) |
| description | TEXT | 1–2 sentence card teaser |
| full_text | TEXT | expanded script (Expanded Echo); may be empty until generated |
| summary_tokens | TEXT | ~10 comma-separated nouns/actions → dedup key |
| tags | TEXT (JSON array) | e.g. `["#Camera","#Flagship"]` |
| status | TEXT | `pending` \| `approved` \| `rejected` |
| source | TEXT | `seed` \| `gemini` |
| created_at | TEXT (ISO) | |
| updated_at | TEXT (ISO) | |

`saved_ideas` — `(user_id, idea_id)` PK, `created_at`. (Approve/save adds a row.)

`swipes` — `id`, `user_id`, `idea_id`, `direction` (`left`/`right`/`up`), `created_at`.

`feedback_markers` — `id`, `user_id`, `marker` (text), `weight`, `created_at`.
Negative markers extracted from rejected ideas; injected into future generation prompts.

Soft, demo-grade migrations: `schema.sql` is idempotent (`CREATE TABLE IF NOT EXISTS`). A
`UNIQUE(user_id, title)` constraint makes ingestion idempotent (re-running generation won't dupe).

---

## 4. API (v1)

All under `/api/v1`. Idea responses use the **app's `IdeaModel` shape** so the Kotlin side maps
1:1: `{ id, category, title, description, tags }` (where `category` = a friendly content-type
label, e.g. `UNBOXING`, `LIVE STREAM`).

| Method | Path | Purpose |
| :- | :- | :- |
| GET | `/health` | liveness + whether Gemini is configured |
| GET | `/ideas/deck?limit=20` | pending deck for the swipe screen (`List<IdeaModel>`) |
| GET | `/ideas/next` | a single pending idea |
| POST | `/ideas/{id}/swipe` | body `{ "action": "approve"\|"reject"\|"save" }` |
| GET | `/ideas/saved` | saved/approved ideas (`List<IdeaModel>`) |
| DELETE | `/ideas/{id}/saved` | unsave |
| POST | `/ideas/{id}/echo` | generate + return the expanded `full_text` (Gemini) |
| POST | `/generate` | body `{ "niche", "count" }` → run the generation pipeline |
| GET | `/catalog/niches` | the showcase niches (for reference/onboarding) |

Swipe behavior:
- `reject` → status `rejected`, record swipe `left`, extract negative markers (Gemini if available, else heuristic from tags/tokens).
- `approve` → status `approved`, record swipe `right`, add to `saved_ideas`.
- `save` → record swipe `up`, add to `saved_ideas` (status unchanged).

---

## 5. Generation pipeline (Gemini)

`POST /generate` for a niche runs:

1. **Trends in** — curated static trend snippets per niche (`seed.py`), e.g. "new flagship phone
   launch", "GPU price drop". (Real RSS/scraper is a later phase; the interface stays the same.)
2. **LLM transform** — one Gemini call returns N structured ideas as JSON: `title`,
   `description`, `full_text`, `content_type`, `tags`, and a **10-word `summary_tokens`** string
   (nouns/actions only).
3. **Behavioral filter (RAG-lite)** — the prompt appends the user's negative markers:
   *"DO NOT use these patterns/topics: …"*.
4. **Dedup** — each idea's `summary_tokens` is compared (Jaccard word overlap) against existing
   ideas; anything above the threshold (default 0.6) is discarded silently.
5. **Persist** — survivors saved as `pending`, `source=gemini`.

`POST /ideas/{id}/echo` makes a separate Gemini call to expand one idea into a full script/post,
stores it in `full_text`, and returns it (the app's typewriter streams this).

---

## 6. Phased build plan

- **Phase 0 — Scaffold:** FastAPI app, config, SQLite init, `/health`, run script, `.env.example`. ✅ in this commit
- **Phase 1 — Schema + repo:** tables, `repository.py` CRUD, idempotent migrations. ✅
- **Phase 2 — Core API + seed:** deck / next / swipe / saved / unsave, 20 seed ideas (tech + gaming) so the app works end-to-end with **no Gemini key**. ✅
- **Phase 3 — Gemini generation:** `/generate` pipeline + 10-word tokenizer. ✅
- **Phase 4 — Dedup engine:** Jaccard overlap on `summary_tokens`. ✅
- **Phase 5 — RAG feedback:** marker extraction on reject + prompt injection. ✅
- **Phase 6 — Expanded Echo:** `/ideas/{id}/echo` full-text generation. ✅
- **Phase 7 — Android wiring:** `IdeaRepository` over Retrofit/Ktor; cleartext config for LAN IP. (guide in README)
- **Phase 8 — Notion sync:** push approved ideas to a Notion DB. (later)
- **Phase 9 — Google Calendar:** schedule approved ideas. (later)

---

## 7. Run + connect the phone

```bash
cd backend-swipey
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env        # add GEMINI_API_KEY (optional for the core loop)
./run.sh                    # uvicorn on 0.0.0.0:8000
```

- Laptop: open `http://localhost:8000/docs` (interactive Swagger).
- Phone (same Wi-Fi): find the laptop's LAN IP (`ipconfig getifaddr en0` on macOS) and hit
  `http://<LAPTOP_IP>:8000/api/v1/...`. Android needs cleartext allowed for that IP (see README).

---

## 8. Showcase niches (seed)

- **tech_reviews** — a phone reviewer: unboxings, camera shootouts, battery endurance tests,
  "should you upgrade?", budget vs flagship, accessory reviews.
- **gaming** — a streamer: game reviews, first-impressions live streams, speedrun breakdowns,
  settings/optimization guides, indie spotlights.

Each idea is tagged and typed so the swipe card eyebrow + tags look real immediately.
