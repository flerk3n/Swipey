# Swipey — Backend Integration Guide

How to connect the existing Swipey app (currently 100% local / in-memory) to a real backend.
The UI, navigation, ViewModels, and state contracts are already built — this guide shows the
**seams** where networking plugs in, so you don't have to touch the UI layer.

> Target backend per `masterdoc.md`: **Supabase** (Postgres + Auth + Realtime). Nothing here
> is Supabase-specific except Section 8 — any REST/GraphQL backend maps the same way.

---

## 1. Current architecture (what exists today)

```
UI (Composables, stateless *Content)         ← do not change for data wiring
        ▲ state          │ events
        │                ▼
ViewModels (StateFlow<UiState>)               ← inject repositories HERE
        ▲                │
        │                ▼
In-memory data sources (REPLACE THESE):
  • data/SavedIdeasStore (object)             saved/liked ideas
  • data/model/SeedIdeas (object)             the 20-card swipe deck
  • data/model/OnboardingData (object)        20 niches + sub-topics + goals
  • data/OnboardingPrefs (SharedPreferences)  onboarding done + nicheId/goalId
  • data/SettingsPrefs (SharedPreferences)    toggles + integration flags
```

Pattern already in place everywhere: each screen is split into a stateful entry point
(`FlashcardScreen`) that owns the ViewModel, and a stateless `*Content` composable used by
previews. **Networking changes live in the ViewModels and a new repository layer only.**

Every screen already models loading/empty/content via a sealed `UiState`
(e.g. `FlashcardUiState { Loading, Content, Empty }`), so async data has a home already.

---

## 2. The replacement plan (in priority order)

| # | Replace | With | Screens affected |
| :- | :- | :- | :- |
| 1 | `SeedIdeas.deck` | `IdeaRepository.fetchDeck()` | Flashcard |
| 2 | `SavedIdeasStore` | `IdeaRepository.saved()` + `save/unsave` | Flashcard, Dashboard, Echo, Profile |
| 3 | `OnboardingData` | `CatalogRepository.niches()/goals()` (cache locally) | Onboarding |
| 4 | `OnboardingPrefs` | server `profiles` row (+ local cache) | Onboarding, Profile |
| 5 | `EchoViewModel.onGenerate()` | `GenerationRepository.generate(ideaId)` | Expanded Echo |
| 6 | `SettingsPrefs` | `profiles.settings` JSON (+ local cache) | Settings |
| 7 | (new) | `AuthRepository` + auth screens | Splash, Welcome, Sign in/up |

Do them top-down; the swipe loop (1–2) is the highest-value integration.

---

## 3. Add the networking stack

In `gradle/libs.versions.toml` (`[versions]` + `[libraries]`), then reference as `libs.*`:

```toml
[versions]
supabase = "2.5.4"      # check latest stable
ktor = "2.3.12"
coroutines = "1.8.1"

[libraries]
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
supabase-auth      = { group = "io.github.jan-tennert.supabase", name = "auth-kt", version.ref = "supabase" }
supabase-realtime  = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version.ref = "supabase" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
```

```kotlin
// app/build.gradle.kts
implementation(libs.supabase.postgrest)
implementation(libs.supabase.auth)
implementation(libs.supabase.realtime)
implementation(libs.ktor.client.android)
```

**Manifest:** add `<uses-permission android:name="android.permission.INTERNET" />`.

**Secrets** — never commit keys. Put them in `local.properties` (git-ignored) and surface via
`BuildConfig`:

```kotlin
// app/build.gradle.kts → android { defaultConfig { ... } }
buildFeatures { buildConfig = true }
val props = Properties().apply { load(rootProject.file("local.properties").inputStream()) }
buildConfigField("String", "SUPABASE_URL", "\"${props.getProperty("SUPABASE_URL")}\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"${props.getProperty("SUPABASE_ANON_KEY")}\"")
```

Single client in an `Application` subclass (register it in the manifest `android:name`):

```kotlin
val supabase = createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
    install(Auth); install(Postgrest); install(Realtime)
}
```

---

## 4. Domain models ↔ DTOs

The app's domain models are stable; keep them and map DTOs to them in the repository.

```kotlin
// EXISTING domain model — keep as-is
data class IdeaModel(
    val id: String, val category: String, val title: String,
    val description: String, val tags: List<String> = emptyList(),
)
```

Network DTO (snake_case to match Postgres) + mapper:

```kotlin
@Serializable
data class IdeaDto(
    val id: String, val category: String, val title: String,
    val description: String, val tags: List<String> = emptyList(),
)
fun IdeaDto.toDomain() = IdeaModel(id, category, title, description, tags)
```

Other existing models that need tables/mapping: `Niche(id, emoji, name, topics)`,
`Goal(id, title, subtitle)` (both in `data/model/OnboardingData.kt`).

---

## 5. Repository interfaces to introduce

Create `data/repository/`. Keep these **interfaces** so ViewModels depend on abstractions and
tests can fake them.

```kotlin
interface IdeaRepository {
    suspend fun fetchDeck(limit: Int = 20): List<IdeaModel>   // replaces SeedIdeas.deck
    fun saved(): Flow<List<IdeaModel>>                        // replaces SavedIdeasStore.saved
    suspend fun save(idea: IdeaModel)                         // like/save → persists
    suspend fun unsave(id: String)
    suspend fun recordSwipe(ideaId: String, direction: String) // analytics / dedupe feed
}

interface CatalogRepository {                                 // replaces OnboardingData
    suspend fun niches(): List<Niche>
    suspend fun goals(): List<Goal>
}

interface ProfileRepository {                                 // replaces OnboardingPrefs (+cache)
    suspend fun completeOnboarding(nicheId: String, goalId: String)
    fun profile(): Flow<UserProfile>                          // niche, goal, name, streak
}

interface GenerationRepository {                              // replaces Echo typewriter source
    fun generate(ideaId: String): Flow<String>                // emit growing text or final blob
}

interface SettingsRepository {                                // replaces SettingsPrefs
    fun settings(): Flow<SettingsUiState>
    suspend fun update(settings: SettingsUiState)
}
```

Keep a thin **local cache** (Room or DataStore) behind each repo so the app works offline and
the UI's existing `Loading`/`Content` states feel instant. SharedPreferences (`OnboardingPrefs`,
`SettingsPrefs`) can stay as the cache layer initially.

---

## 6. Exactly where each ViewModel changes

All ViewModels currently construct state from the in-memory objects. Switch them to take a
repository (constructor injection via a `ViewModelProvider.Factory`, Hilt, or Koin).

**`FlashcardViewModel`** (`ui/screens/flashcard/`)
- `loadDeck()` → `repo.fetchDeck()` inside the existing `viewModelScope.launch`; keep
  `_uiState.value = Loading` before and `Content/Empty` after. The 450ms fake delay goes away.
- `save()` / `like()` → `repo.save(idea)` (and `repo.recordSwipe(...)`); the toast emit stays.
- `dismiss()` → `repo.recordSwipe(id, "left")`.

**`DashboardViewModel`** (`ui/screens/dashboard/`)
- Currently `SavedIdeasStore.saved.map { ... }`. Swap source to `ideaRepo.saved()`. Replace the
  placeholder `streak = saved.size` with `profileRepo.profile().streak`.

**`EchoViewModel`** (`ui/screens/echo/`)
- `onGenerate()` currently types out `idea.description`. Swap to collect
  `generationRepo.generate(ideaId)` and push each emission into `displayedGenerated` — the
  typewriter UI already renders incremental text, so streaming "just works".
- Tag edits (`onTagAdded/onTagToggled`) should also persist via `ideaRepo` (PATCH the idea's tags).

**`SettingsViewModel`** (`ui/screens/settings/`)
- Reads/writes go through `SettingsRepository` instead of `SettingsPrefs` directly. The Notion /
  Google Calendar toggles become real OAuth connect/disconnect calls (see Section 9).

**Onboarding** (`OnboardingViewModel` + `SwipeyNavHost`)
- Step 1/2 options come from `CatalogRepository`. On finish, the nav host currently writes
  `OnboardingPrefs`; additionally call `profileRepo.completeOnboarding(nicheId, goalId)`.

---

## 7. Auth (per masterdoc)

Screens to add (the nav graph in `ui/navigation/` is ready to extend with new `Route`s):
Splash (auto-login check) → Welcome (exists) → Sign Up / Sign In (email+password) → Google One
Tap sheet → Forgot Password → Email Verification.

```kotlin
// Email
supabase.auth.signUpWith(Email) { email = ...; password = ... }
supabase.auth.signInWith(Email) { email = ...; password = ... }
// Google (get the ID token via Google Identity Services, then:)
supabase.auth.signInWith(IDToken) { idToken = googleIdToken; provider = Google }
```

Gate the start destination on session: in `SwipeyApp`, replace the `OnboardingPrefs.isComplete`
check with `supabase.auth.currentSessionOrNull()` + the profile's onboarding flag.

---

## 8. Suggested Supabase schema

```sql
-- Static seed catalog (ships pre-populated; see masterdoc taxonomy)
create table platforms        (id int primary key, name text, icon_slug text, active bool);
create table content_formats  (id int primary key, platform_id int references platforms, name text);
create table niches           (id text primary key, emoji text, name text);
create table niche_subtopics  (id bigserial primary key, niche_id text references niches, name text);
create table goals            (id text primary key, title text, subtitle text);

-- Per-user
create table profiles (
  id uuid primary key references auth.users on delete cascade,
  display_name text, niche_id text references niches, goal_id text references goals,
  streak int default 0, settings jsonb default '{}', onboarded bool default false
);

create table ideas (
  id text primary key, category text, title text, description text, tags text[] default '{}'
);

create table saved_ideas (
  user_id uuid references auth.users on delete cascade,
  idea_id text references ideas, created_at timestamptz default now(),
  primary key (user_id, idea_id)
);

create table swipes (
  id bigserial primary key, user_id uuid references auth.users,
  idea_id text references ideas, direction text, created_at timestamptz default now()
);
```

**Row Level Security (required):** enable RLS on every per-user table and add
`auth.uid() = user_id` policies for select/insert/update/delete. The static catalog tables get a
read-only `using (true)` policy. Without RLS the anon key exposes all rows — do not skip this.

Mapping back to the app: `ideas` → `IdeaModel`; `saved_ideas` join → `SavedIdeasStore.saved`;
`niches` + `niche_subtopics` → `Niche`; `goals` → `Goal`; `profiles.settings` → `SettingsUiState`.

---

## 9. Integrations (Settings screen)

The Notion and Google Calendar rows in `SettingsScreen` are connect/disconnect toggles today
(persisted booleans, with a placeholder sheet for Notion). Real wiring:
- **Notion**: OAuth → store the access token server-side (an `integrations` table keyed by
  `user_id`); the toggle reflects token presence. On save-idea, optionally push to a Notion DB
  via a server function (don't put the Notion secret in the app).
- **Google Calendar**: OAuth scope for calendar; create events for scheduled ideas server-side.

Keep all third-party secrets on the server. The app should only ever hold its own session token.

---

## 10. Threading, errors, and offline

- All repo calls are `suspend` / `Flow`; call them inside `viewModelScope` (already used). Do I/O
  on `Dispatchers.IO`.
- Add an `Error` case to each sealed `UiState` (currently `Loading/Content/Empty`) and render a
  retry state — the `when(uiState)` blocks make this a one-line addition per screen.
- Cache-first: emit cached data immediately, then refresh from network, so the existing instant
  UI is preserved.
- `bump minSdk` consideration: Supabase/Ktor are fine on `minSdk 24`. Revisit `java.time` usage if
  you swap the `Calendar`-based greeting in `DashboardViewModel` for `LocalTime`
  (needs API 26 or core-library desugaring).

---

## 11. Integration checklist

- [ ] Add networking deps + `INTERNET` permission + `BuildConfig` secrets (no keys committed).
- [ ] Create Supabase client in an `Application` subclass.
- [ ] Create schema + **enable RLS** with `auth.uid()` policies; seed catalog + `ideas`.
- [ ] Implement `IdeaRepository` (deck fetch, saved flow, save/unsave, swipe) + local cache.
- [ ] Inject it into `FlashcardViewModel` and `DashboardViewModel`; delete `SeedIdeas` /
      `SavedIdeasStore` once nothing references them.
- [ ] Implement `GenerationRepository`; stream into `EchoViewModel.displayedGenerated`.
- [ ] Implement `CatalogRepository`; feed onboarding steps; cache locally.
- [ ] Implement `ProfileRepository` + `SettingsRepository`; migrate the two `*Prefs` to caches.
- [ ] Add Auth screens + session-gated start destination.
- [ ] Add an `Error` state to each `UiState` + retry UI.
- [ ] Update unit tests to use fake repositories (the ViewModel tests already isolate logic).
```
