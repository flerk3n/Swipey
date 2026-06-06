# Project Structure & Architecture

## Base package
The real package is **`com.harsh.swipey`** (the Phases doc's `com.swipey` is illustrative —
always use the actual one). All new code goes under `app/src/main/java/com/harsh/swipey/`.

## Target package layout
Create folders as the relevant phase needs them — don't scaffold empty directories ahead
of time.

```
com.harsh.swipey/
├── MainActivity.kt          ← single Activity, hosts setContent { SwipeyTheme { SwipeyApp() } }
├── ui/
│   ├── theme/               ← Color.kt, Type.kt, Spacing.kt, Theme.kt
│   ├── components/          ← every reusable composable (atoms)
│   ├── screens/
│   │   ├── onboarding/      ← Welcome, Niche/Topic/Goal + OnboardingViewModel
│   │   ├── flashcard/       ← FlashcardScreen + FlashcardViewModel + swipe core
│   │   ├── echo/            ← ExpandedEchoScreen (+ VM)
│   │   ├── dashboard/       ← DashboardScreen + DashboardViewModel
│   │   ├── profile/         ← ProfileScreen
│   │   └── settings/        ← SettingsScreen + SettingsViewModel
│   └── navigation/          ← SwipeyNavHost, Routes (sealed class)
├── data/
│   └── model/               ← data classes only (IdeaModel, Niche, etc.) — no networking
└── (later) data/repository, data/local — only when backend phase begins
```

The swipe-core composables (`SwipeableCard`, `CardStack`, `CardStackState`) live under
`ui/screens/flashcard/` (or `ui/components/` if reused). Keep them together.

## Current state (Android Studio template — not yet migrated)
The project still ships the default template. Expect to replace, not extend, these:
- `ui/theme/Color.kt` — has `Purple80/40` etc. Replace with the Swipey palette.
- `ui/theme/Theme.kt` — has dynamic color + light/dark switching. Replace with **dark-only**
  Swipey theme (no dynamic color, no light scheme).
- `ui/theme/Type.kt` — default `Typography`. Replace with Instrument Serif/Sans scale.
- `MainActivity.kt` — has `Greeting` demo. Replace body with the nav host once it exists.

## Architecture (strict)
- **Single Activity.** Never create a second Activity. All screens are composables routed
  through one `NavHost`.
- **MVVM.** UI (composables) is separate from state/logic (ViewModels).
- **Unidirectional Data Flow (UDF).** Composables are **stateless**: hoist all state up and
  pass events down as lambdas. A screen composable receives `uiState` + callbacks; it does
  not own `ViewModel` references except at the top route-level composable.
- **State holders:** ViewModels expose `StateFlow` / `MutableStateFlow`. Collect in Compose
  with `collectAsStateWithLifecycle()`. Model screen state with a sealed `UiState` where
  there are loading/empty/content/error variants (e.g. Flashcard empty = "ALL CAUGHT UP").
- Phases 3.5's `CardStackState` is a plain state holder (`SnapshotStateList`), **not** a
  ViewModel. It graduates into `FlashcardViewModel` in Phase 5.

## Navigation
- One centralized `NavHost` inside the top-level `SwipeyApp` composable.
- Define destinations with a strongly-typed **sealed class** of routes (e.g.
  `sealed class Route(val path: String)`), not loose string literals.
- Screens receive navigation as lambdas (`onNavigateToEcho: (ideaId: String) -> Unit`,
  `onContinue: () -> Unit`) — they never reference `NavController` directly.
- Expand the existing `NavHost` incrementally per phase; don't rewrite it each time.
- Persist onboarding completion (SharedPreferences) so returning users skip onboarding.

## Naming conventions
- Composables: `PascalCase` (`IdeaCard`, `BottomNavPill`). One public composable per concept;
  put its `@Preview` in the same file, named `<Name>Preview`.
- ViewModels: `<Feature>ViewModel`. State: `<Feature>UiState`. Routes: `Route.<Screen>`.
- Files match the primary composable/class name.
- Shared in-memory store: `FlashcardViewModel` writes saved ideas; `DashboardViewModel`
  and `ProfileScreen` read from that same shared source (don't duplicate state).
