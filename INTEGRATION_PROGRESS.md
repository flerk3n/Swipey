# Phase 7 — App ↔ Backend Integration Progress

Connecting the Swipey Android app to the local FastAPI backend (`backend-swipey/`) over HTTP.
The backend serves the app's `IdeaModel` shape 1:1, so the swipe loop maps directly.

> Networking target: the **FastAPI backend** (not Supabase). The Supabase guide in
> `docs/BACKEND_INTEGRATION.md` describes a later cloud milestone; this phase wires the
> local server so laptop + phone work end-to-end.

## Architecture decisions
- **HTTP client:** Retrofit + Gson + OkHttp logging (no extra Kotlin plugin; reflection maps
  the simple DTOs). Suspend functions run off the main thread automatically.
- **DI:** lightweight `ServiceLocator` singleton builds the Retrofit API + repository.
  ViewModels receive the repo through `viewModelFactory { initializer { ... } }`.
- **Source of truth for saved ideas:** `SavedIdeasStore` stays as the in-memory cache the
  whole UI already observes; the repository updates it after network calls (optimistic for
  likes/saves, server refresh on deck load).
- **Base URL:** `BuildConfig.SWIPEY_API_BASE`, read from `local.properties`
  (`SWIPEY_API_BASE=...`), default `http://10.0.2.2:8000/` (emulator → host). Physical phone:
  set it to the laptop's LAN IP.
- **Cleartext:** dev-only network security config permits HTTP for local testing.

## Endpoint mapping
| App action | Backend call |
| :- | :- |
| Load deck | `GET /api/v1/ideas/deck?limit=20` |
| Like (swipe right / ♡) | `POST /api/v1/ideas/{id}/swipe {"action":"approve"}` → cache add |
| Save (swipe up / ↑) | `POST /api/v1/ideas/{id}/swipe {"action":"save"}` → cache add |
| Dismiss (swipe left / ✕) | `POST /api/v1/ideas/{id}/swipe {"action":"reject"}` |
| Saved list (Dashboard) | `GET /api/v1/ideas/saved` |
| Expand Echo (Generate) | `POST /api/v1/ideas/{id}/echo` → typewriter streams `full_text` |
| (health check) | `GET /api/v1/health` |

## Task checklist
- [x] Add Retrofit/OkHttp deps to version catalog + `build.gradle.kts` (+ `buildConfig`)
- [x] `INTERNET` permission + dev cleartext network-security config
- [x] DTOs + mappers (`data/remote/ApiModels.kt`)
- [x] Retrofit API (`data/remote/SwipeyApi.kt`)
- [x] `IdeaRepository` interface + `RemoteIdeaRepository` impl
- [x] `ServiceLocator` (Retrofit + repo, `X-User-Id: demo` header)
- [x] `SavedIdeasStore.replaceAll()` for server refresh
- [x] `FlashcardViewModel`: repo + factory + `Error` state + server deck/saved
- [x] `FlashcardScreen`: factory + render `Error` retry state
- [x] `EchoViewModel`: repo + factory + network-backed generation
- [x] `ExpandedEchoScreen`: factory
- [x] `DashboardViewModel`: factory + refresh saved on open
- [x] `./gradlew assembleDebug` green
- [x] End-to-end contract test (curl against backend with `X-User-Id: demo`)

## Verification results
- **Build:** `./gradlew assembleDebug` → BUILD SUCCESSFUL.
- **Unit tests:** `./gradlew testDebugUnitTest` → all pass (added `FakeIdeaRepository`,
  a deck-failure → `Error` state test; updated Flashcard/Echo/Dashboard VM tests to inject
  the fake repo).
- **Contract (live backend):** verified the exact calls the app makes, with the
  `X-User-Id: demo` header, all return the shapes the DTOs expect:
  - `GET /ideas/deck` → `IdeaModel[]` (id, category, title, description, tags)
  - `POST /ideas/{id}/swipe {action:approve|save|reject}` → `{id, action, status, saved}`
  - `GET /ideas/saved` → liked/saved ideas appear
  - `POST /ideas/{id}/echo` → `{id, full_text, generated}` (Gemini text streamed by the
    typewriter)

## Files added
- `app/.../data/remote/ApiModels.kt`, `SwipeyApi.kt`
- `app/.../data/repository/IdeaRepository.kt` (+ `RemoteIdeaRepository`)
- `app/.../data/ServiceLocator.kt`
- `app/src/main/res/xml/network_security_config.xml`
- `app/src/test/.../util/FakeIdeaRepository.kt`

## Files changed
- `gradle/libs.versions.toml`, `app/build.gradle.kts` (deps + `buildConfig` + base URL)
- `app/src/main/AndroidManifest.xml` (INTERNET + cleartext config)
- `data/SavedIdeasStore.kt` (`replaceAll`)
- `FlashcardViewModel/Screen`, `EchoViewModel/ExpandedEchoScreen`,
  `DashboardViewModel/DashboardScreen`
- 3 VM test files

## How to run the connected app
1. Start the backend: `cd backend-swipey && ./run.sh` (note the printed LAN IP).
2. Point the app at the backend:
   - **Emulator:** nothing to do — defaults to `http://10.0.2.2:8000/`.
   - **Physical phone (same Wi-Fi):** add to `local.properties`:
     `SWIPEY_API_BASE=http://<LAPTOP_LAN_IP>:8000/`
3. Build/install the app. Swipe → likes/saves persist to the backend; the Dashboard shows
   the server's saved list; "Generate Content" on Expanded Echo streams real Gemini text.

## Notes / limitations
- Onboarding completion gating still uses the local flag (`OnboardingPrefs.isComplete`);
  a returning user on the same install skips onboarding. Niche/goal/settings now also live
  on the server so they survive reinstall and are read back by Profile/Settings.

_Status: COMPLETE — core swipe loop (deck → swipe → saved → echo) fully wired to the backend._

---

## Follow-up — tag persistence + profile/settings sync (COMPLETE)

Backend additions:
- `PATCH /api/v1/ideas/{id}/tags` `{tags:[...]}` → updates an idea's tags, returns IdeaModel.
- `profiles` table + `GET/PUT /api/v1/profile` and `GET/PUT /api/v1/profile/settings`
  (single demo user; default row auto-created; settings validated against known keys).

App additions:
- `IdeaRepository.updateTags(...)` + `SavedIdeasStore.updateTags(...)`; `EchoViewModel`
  persists tag toggles/adds to the backend (best-effort) and updates the cache.
- `ProfileRepository` (`UserProfile`/`UserSettings`) + `ServiceLocator.profileRepository`.
- `SettingsViewModel`: cache-first — shows local prefs instantly, hydrates from the server,
  and writes every toggle to both prefs and the server.
- Onboarding finish → `profileRepository.completeOnboarding(nicheId, goalId)` (best-effort)
  alongside the existing local prefs write.
- `ProfileViewModel` reads niche/goal/name from the server (falls back to local prefs),
  resolving display names via `OnboardingData`.

Verification:
- `./gradlew assembleDebug` + `./gradlew testDebugUnitTest` → both green (FakeIdeaRepository
  updated with `updateTags`).
- Live contract check (with `X-User-Id: demo`): `PUT /profile` (niche/goal/onboarded),
  `GET /profile` (correct shape incl. nested settings), `PUT /profile/settings` + read-back,
  and `PATCH /ideas/{id}/tags` → IdeaModel with updated tags. All persist.

---

## Follow-up — niche-filtered deck + onboarding defaults (COMPLETE)

Goal: make the onboarding niche choice actually drive the swipe deck, scoped to the three
seeded niches, so a demo is coherent. Never go empty.

Backend:
- `GET /api/v1/ideas/deck?niche=<niche>` — optional filter (`repository.list_deck` gained a
  `niche` arg). Omitted → all ideas.

App:
- `data/NicheMapping.kt` maps onboarding niche ids → seed niches:
  `tech → tech_reviews`, `gaming → gaming`, `fashion → fashion`; anything else → `null`
  (deck falls back to all ideas, so unseeded niches never break the demo).
- `IdeaRepository.fetchDeck(limit, niche)` + `SwipeyApi.getDeck(limit, niche)` (null query
  param is omitted by Retrofit).
- `FlashcardViewModel` takes an optional `niche`; its factory reads `OnboardingPrefs.nicheId`
  (via the Application in CreationExtras) and maps it through `NicheMapping`.
- Onboarding defaults pre-selected (`OnboardingUiState`): niche `tech`, a tech topic, goal
  `consistent` — so a demo can tap straight through, and can still switch to gaming/fashion.

Verification:
- `assembleDebug` + `testDebugUnitTest` green (updated `OnboardingViewModelTest` for the new
  defaults; `FakeIdeaRepository.fetchDeck` signature updated).
- Live: `deck?niche=tech_reviews|gaming|fashion` → 10 each; no filter → 30; unseeded niche
  (`finance`) → 0 (app sends null instead, getting all).

_Status: COMPLETE — onboarding niche now scopes the deck to the matching seeded content._
