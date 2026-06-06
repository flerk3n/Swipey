# Product Requirements Document (PRD): Swipey Backend Engine

---

## 1. Objective & Scope

The objective is to build a lightweight, highly robust, and migration-safe backend API layer for **Swipey** (formerly Noped). This backend acts as the central orchestrator: scraping niche trends, processing them through a local/cloud LLM pipeline into unique ideas, checking for duplicates using semantic/keyword history, and handling external integrations (Notion & Google Calendar) for a flawless hackathon demo.

---

## 2. Robust System Architecture & Migration Strategy

To guarantee the system doesn't break during live demos or fast-paced database updates, the backend follows an **Event-Driven Repository Pattern**.

```
[Cron/Trigger] ➔ Niche Scraper Pipeline ➔ Queue
                                            │
[LLM Deduplicator] 💡 Deduplication Engine 🗄️ Database (Strict Schema + Migrations)
                                            │
[FastAPI / Express] ➔ API Layer ➔ [Notion / GCal Sync]

```

### Architectural Safeguards

* **Migration Isolation:** Every table utilizes soft deletes and strictly managed foreign keys. Migration scripts must be versioned sequentially to prevent state-corruption during rapid deployment.
* **Idempotent Ingestion Pipeline:** If the scraper or generator runs multiple times unexpectedly, the database rejects duplicates at the ingestion layer using a unique constraint composite key.

---

## 3. Database Schema

The database design relies on structured relations optimized for quick lookups, seamless tracking of historical "Nopes," and seamless exports to productivity suites.

### Table: `ideas`

| Column Name | Data Type | Constraints / Notes |
| --- | --- | --- |
| `id` | UUID | Primary Key, auto-generated |
| `user_id` | String / UUID | Foreign Key indexing the creator |
| `title` | String | The core headline/hook of the idea |
| `full_text` | Text | The entire expanded concept/script string |
| `summary_tokens` | String | 10-word summary (nouns/actions) used for deduplication |
| `content_type` | Enum | `tweet`, `short_form`, `long_form`, `post`, `live_stream` |
| `tags` | Array (Strings) | Topics (e.g., `["AI", "Solopreneur", "Productivity"]`) |
| `status` | Enum | `pending`, `approved`, `rejected` (swiped left) |
| `notion_page_id` | String | Nullable; populated upon successful Notion sync |
| `gcal_event_id` | String | Nullable; populated upon Google Calendar scheduling |
| `created_at` | Timestamp | Default: `now()` |
| `updated_at` | Timestamp | Standard tracking for changes |

---

## 4. Core Functional Pipelines

### A. Smart Scraping & Content Gathering

Instead of writing complex, fragile scrapers for every single platform, Swipey utilizes a unified **RSS + Content Aggregator API Layer**.

* **The Strategy:** The backend hooks into targeted subreddits, top creator RSS feeds, and keyword-specific Google News aggregators.
* **Normalization:** Raw HTML/JSON payloads are immediately stripped into a uniform structure: `[Source, Title, Raw Body, Timestamp]`.

### B. Idea Generation & Content Tokenizer

Once raw trends enter the pipeline, they pass through the LLM processing engine.

1. **Transformation:** The LLM receives the raw trend text and translates it into a structured idea matching the user's content profiles.
2. **The 10-Word Tokenizer:** The engine forces the LLM to output a precise, comma-separated, 10-word string containing only the essential **nouns and actions** summarizing the core premise (e.g., *"Developer automates workflow using local models to save five hours"*).

### C. The Deduplication & Anti-Repetition Engine

Before an idea is saved to the active swipe queue, the 10-word summary tokens are cross-referenced against the `summary_tokens` database history using keyword overlaps and vector boundaries. If an active or recently dismissed idea shares high semantic overlap with the newly generated token array, the idea is silently discarded to keep the user feed clean and distinct.

### D. RAG Memory Pipeline (The Behavioral Filter)

The RAG pipeline maps user feedback behaviors onto markers that control future prompt construction.

```
[Left Swipe]  ➔ Extracts linguistic markers ➔ Saved as "Negative Vector"
[Right Swipe] ➔ Extracts linguistic markers ➔ Saved as "Positive Vector"

```

* **Negative Markers:** When a user swipes left (`rejected`), the system parses out the structural components (e.g., "Overly salesy tone," "Uses emojis at the start"). These are stored as explicit negative constraints.
* **Context Injection:** When the daily pipeline requests new hooks, the generation prompt appends these dynamic memory markers: *“DO NOT use the following patterns or topics: [Negative Markers]”*.

---

## 5. API Layer Specifications

### `GET /api/v1/ideas/next`

Fetches a single `pending` idea for the Swipe UI.

* **Response:** Returns `id`, `title`, `full_text`, `tags`, and `content_type`.

### `POST /api/v1/ideas/{id}/swipe`

Registers the user's action immediately.

* **Payload:** `{ "action": "approve" | "reject" }`
* **Behavior:**
* If `reject`, update status to `rejected` and trigger the Feedback Processor to extract negative memory markers.
* If `approve`, update status to `approved` and trigger the background integration workers.



### `POST /api/v1/integrations/sync`

Asynchronously pushes approved items to external productivity setups.

* **Notion Sync:** Creates a clean database row inside the creator's content dashboard using the `title`, `tags`, and `content_type`.
* **Google Calendar Sync:** Blocks out tentative slots on the calendar based on available publication windows, mapping back the `gcal_event_id`.