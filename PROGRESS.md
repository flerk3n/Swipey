# Swipey — Build Progress

Living checklist of what's done and what's next. Update this at the end of every task.
Full plan lives in `.kiro/steering/build-roadmap.md`.

_Last updated: Phase 9 complete — build shippable. Backend guide written._

## Testing
- **34 JVM unit tests, all passing** (`./gradlew testDebugUnitTest`):
  - `SavedIdeasStoreTest` — add/dedupe/order/remove/clear.
  - `SeedDataTest` — 20 seed ideas, 20 niches, `nicheById`, goals.
  - `OnboardingViewModelTest` — selection flags, niche-change clears topics, topic toggle.
  - `FlashcardViewModelTest` — deck load, dismiss/save/like routing, exhaust→Empty, reset.
  - `EchoViewModelTest` — idea resolution, typewriter completion, tag add/dedupe/toggle.
  - `DashboardViewModelTest` — initial state from store, progress clamping, empty state.
- `./gradlew clean assembleDebug` — BUILD SUCCESSFUL from scratch.
- ⚠️ `./gradlew lintDebug` could **not** run in this environment — it needs to download lint
  artifacts (`intellij-core`, `uast`) from Google Maven and the network handshake fails.
  Re-run lint locally where network access is available.


---

## Phase 0 — Setup
- [x] Project exists (single module `:app`, package `com.harsh.swipey`, version catalog).
- [x] Icons: use `material-icons-core` + custom vectors in `SwipeyIcons.kt`.
      **`material-icons-extended` was removed** — it breaks Compose Previews
      (`ClassNotFoundException` for extended icon classes on the Layoutlib classpath).
- [x] Added `navigation-compose` (2.7.7) + `lifecycle-viewmodel-compose` /
      `lifecycle-runtime-compose` (2.7.0) via the catalog.
- [x] `data/model/` created (`IdeaModel` + `SampleIdeas`, `OnboardingData`). `ui/components/`,
      `ui/screens/onboarding/`, `ui/navigation/` created.

## Phase 1 — Theme foundation ✅ COMPLETE
- [x] Bundled fonts into `app/src/main/res/font/`:
      - `instrument_serif_regular.ttf`, `instrument_serif_italic.ttf`
      - `instrument_sans_regular/medium/semibold/bold.ttf`
- [x] **1.1 `Color.kt`** — palette + semantic aliases (`ApproveColor`, `DismissColor`,
      `SaveColor`) + card-state fills. Background = `#252736`, surface = `#1D2111`.
- [x] **1.2 `Type.kt`** — `InstrumentSerif` + `InstrumentSans` families, full scale mapped
      onto Material 3 slots, `buildMixedHeadline(plain, emphasis)` helper.
- [x] **1.3 `Spacing.kt`** — `object Spacing` with all dp tokens incl. `ScreenPadding = 20.dp`.
- [x] **1.4 `Theme.kt`** — dark-only `darkColorScheme`, no dynamic color, no light scheme;
      light system-bar icons for edge-to-edge.
- [x] `MainActivity.kt` — `ThemeCheck` checkpoint screen + `@Preview` exercising the fonts.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL, no warnings, no diagnostics.

### Decisions / deviations logged
- **Local fonts instead of GoogleFont API.** User supplied TTFs; bundling is offline,
  deterministic in Previews, and avoids a Play Services dependency. Roadmap's
  `ui-text-google-fonts` dep is therefore skipped.
- **Instrument Serif is single-weight (Regular + Italic only).** Design asks for 32sp
  Bold Italic / 22sp SemiBold serif. Heavier weights resolve to the closest file and may be
  synthetically emboldened — acceptable for this typeface. If true bold serif is required,
  a different display font must be sourced.
- **Background = `#242B32`** (confirmed by user). Earlier `#252736` / `#000000` are dropped.
- **Screen content must own a `Surface`.** A bare `Column` under `MaterialTheme` (no
  Scaffold/Surface) defaults `LocalContentColor` to black — caused the Phase 1 preview to
  show black serif text. Fixed by wrapping `ThemeCheck` in `Surface(color = background)`;
  every future screen follows the same pattern.

---

## Next up — Phase 2: Atomic components (each visual-first, with `@Preview`)
1. [x] 2.1 `SwipeyTag` (chip/pill) — display + toggleable, lime text.
2. [x] 2.2 `IdeaCard` — full size (glass card, violet eyebrow, serif title, tag FlowRow).
3. [x] 2.3 `IdeaCard` — compact (`IdeaCardCompact`, lime play affordance).
4. [x] 2.4 `PrimaryButton` / `SecondaryButton` / `GhostButton` (in `SwipeyButtons.kt`).
5. [x] 2.5 `CircularActionButton` (✕ ↑ ♡) — configurable fill/border/size.
6. [x] 2.6 `BottomNavPill` (+ `SwipeyTab` enum) — animated lime active tint.
7. [x] 2.7 `SwipeyToggle` — lime-themed M3 Switch.
8. [x] 2.8 `AnimatedBackground` — drifting glow blobs, tintable accent, content slot.
9. [x] 2.9 `SaveToast` — animated feedback pill (`visible` flag).

**Phase 2 verified:** `./gradlew assembleDebug` BUILD SUCCESSFUL, no diagnostics. All atoms
live in `ui/components/` with hardcoded `@Preview`s.

## Phase 3 — Swipe core ✅ COMPLETE
(in `ui/screens/flashcard/`)
- [x] 3.1 `SwipeableCard` — drag via `detectDragGestures`, rotation on horizontal drag,
      `@Preview` included.
- [x] 3.2 Swipe feedback — **whole-card tint** (green right/up, red left); `SwipeIndicators`
      stamp overlay was removed in favor of this. Tokens `SwipeApprove`/`SwipeReject`.
- [x] 3.3 Threshold + dismiss — past threshold animates off-screen (`tween 280ms`), else
      springs back (`spring` medium-bouncy). `SwipeDirection { Left, Right, Up }`.
- [x] 3.4 `CardStack` — renders 3 cards, depth scale/translate transforms, animates the
      shift forward when the top card is dismissed. Only the top card is draggable.
- [x] 3.5 `CardStackState` (+ `rememberCardStackState`) — `SnapshotStateList` queue,
      `onSwipeLeft/Right/Up()`, `dismiss(direction)`, `append`, `reset`, `lastSwiped`.
- [x] Idea cards redesigned: glassmorphism removed → solid `SwipeyCardSurface`, elegant.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL, no diagnostics.

> Note: full drag interaction is exercised on device / in interactive preview. The static
> `@Preview` shows the stacked deck; gestures wire up fully in Phase 5.

## Phase 4 — Onboarding ✅ COMPLETE
(in `ui/screens/onboarding/` + `ui/navigation/`)
- [x] 4.1 `OnboardingViewModel` — `OnboardingUiState` (selectedNiche / topics / goal) via
      `StateFlow`; `onNicheSelected/onTopicToggled/onGoalSelected`. State only, no networking.
- [x] 4.2 `WelcomeScreen` — rebuilt to match the provided HTML: black canvas + violet glow
      blobs, floating tilted idea cards (glassmorphic + lime), faint giant serif "Swipey",
      serif headline with inline pill, lime CTA pill with black circular ↗ button. New tokens
      `SwipeyViolet`/`SwipeyBlack` + custom `Bolt` vector.
- [x] 4.3 Steps — shared `OnboardingScaffold` (segmented progress + back + title + CTA) with
      three bodies: `NicheStepScreen` (list), `TopicStepScreen` (chip cloud, multi-select),
      `GoalStepScreen` (goal cards). New reusable `SelectionCard` component (Design §3 states).
- [x] 4.4 Nav — centralized `SwipeyNavHost` (`SwipeyApp`), sealed `Route`s, lambda-decoupled
      screens, shared VM across steps. Completion saved via `OnboardingPrefs` (SharedPreferences);
      returning users start on `Home`. `MainActivity` now hosts `SwipeyApp`.
- [x] Seed data: `OnboardingData` — all 20 niches + sub-topics from `masterdoc.md`, goal options.
- [x] `HomePlaceholderScreen` lands the flow (real Flashcard/Dashboard come in Phase 5/7).
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL, no diagnostics.

> ⚠️ **Pixel accuracy pending:** the `resources/*.png` screenshots couldn't be read (binary,
> not viewable as inline images). Built to the design system + `Design.md` structure. Needs a
> refinement pass once the screenshots are attached inline in chat.

## Phase 5 — Flashcard screen ✅ COMPLETE (MVP)
(in `ui/screens/flashcard/` + `ui/screens/MainScreen.kt`)
- [x] 5.1 `FlashcardViewModel` — `FlashcardUiState { Loading, Content(cards), Empty }` via
      `StateFlow`; `onSwiped(idea, dir)` + button equivalents `dismissTop/likeTop/saveTop`;
      likes & saves write to shared `SavedIdeasStore`; saves emit one-shot toast events
      (`SharedFlow`). 20 seed ideas in `data/model/SeedIdeas.kt`.
- [x] 5.2 `FlashcardScreen` — `AnimatedBackground` + top bar (flame streak + serif "Swipey")
      + `CardStack` + mirrored `CircularActionButton`s (✕ dismiss / ↑ save / ♡ like).
      `CardStack` refactored to take a plain `cards: List<IdeaModel>` (VM owns the queue).
- [x] 5.3 Edge states — loading skeleton (pulsing card) + empty "ALL CAUGHT UP" with a
      Start-over CTA, swapped via the sealed `UiState`.
- [x] 5.4 Save toast — `SaveToast` driven by `viewModel.toastMessages` (collected in a
      `LaunchedEffect`, shown 2.5s).
- [x] `MainScreen` shell hosts the persistent `BottomNavPill`; Dashboard & Profile are
      placeholders. `Route.Home` → `MainScreen` (replaced `HomePlaceholderScreen`). New
      `Flame` vector in `SwipeyIcons`.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL, no diagnostics.

> Note: background does NOT yet react to swipe direction (roadmap 5.2) — deferred to Phase 9;
> the card's green/red tint already gives feedback. "Like" (right) currently just saves; it
> will navigate to Expanded Echo in Phase 6.

## Phase 6 — Expanded Echo ✅ COMPLETE
(in `ui/screens/echo/`)
- [x] 6.1 `ExpandedEchoScreen` + `ExpandedEchoContent` — category eyebrow, serif title,
      generation canvas, tag row with "+ Add tag" chip, Generate / Edit Tags bottom actions.
      Stateless `Content` variant for preview-safe rendering.
- [x] 6.2 Typewriter animation — `EchoViewModel.onGenerate()` reveals the description char
      by char every 22ms via a coroutine loop; `isGenerating` flag disables the button while
      running.
- [x] 6.3 Tag bottom sheet — `ModalBottomSheet` with toggleable `SwipeyTag` chips +
      `OutlinedTextField` + Add button; Done hides the sheet and updates the VM.
- [x] 6.4 Transition from Flashcard — right-swipe / Like button navigates to
      `Route.Echo.buildPath(ideaId)` (new `ideaId` path arg). Back pops to Flashcard.
      Dashboard "Loved Ideas" rows also navigate to Echo. `EchoViewModel` receives the id
      via `SavedStateHandle` and looks up from `SavedIdeasStore` (falls back to seed deck).
- [x] `Route.Echo("echo/{ideaId}")` added. `NavHost` expanded with the Echo composable +
      `navArgument`. `MainScreen` accepts `onOpenEcho: (String) -> Unit`.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL, no diagnostics.
      Two `@Preview`s: idle (placeholder description) + typewriter (mid-reveal).

## Phase 7 — Dashboard ✅ COMPLETE
(in `ui/screens/dashboard/`)
- [x] 7.1 `DashboardViewModel` — reads the shared `SavedIdeasStore` (written by the Flashcard
      screen) via `stateIn`; exposes `DashboardUiState` with savedIdeas, `dailyTarget` (5),
      derived `savedCount`/`progress`, and a simple `streak` (= saved count, per roadmap).
- [x] 7.2 `DashboardScreen`/`DashboardContent` — `LazyColumn` sections: top bar (flame +
      streak + serif "Swipey" + bell), greeting, glassmorphic goal card (progress bar +
      `n / 5` + Keep Swiping), "Loved Ideas" list (real saved ideas → tap opens Echo),
      "Trends" horizontal row. Built from the provided HTML. `DashboardContent` is
      preview-safe (takes `DashboardUiState`).
- [x] 7.3 Mixed-type greeting — `buildMixedHeadline(greeting, name)`; time-of-day greeting
      via `Calendar.HOUR_OF_DAY` (morning/afternoon/evening/late). Uses `Calendar` not
      `LocalTime` to stay safe on minSdk 24 without core-library desugaring.
- [x] Checkpoint: saved ideas from Flashcard appear on Dashboard; counts are real.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL; `DashboardViewModelTest` added.

## Phase 8 — Profile + Settings ✅ COMPLETE
(in `ui/screens/profile/` + `ui/screens/settings/`)
- [x] 8.1 `ProfileScreen`/`ProfileContent` — avatar, name, niche chip (from `OnboardingPrefs`),
      stat cards (saved-ideas count from `SavedIdeasStore`, daily goal), and a Settings nav
      row. `OnboardingPrefs` extended to persist `nicheId`/`goalId` (written on onboarding
      finish). Profile tab now shows this (replaced the placeholder).
- [x] 8.2 `SettingsScreen`/`SettingsContent` + `SettingsViewModel` (`AndroidViewModel`) backed
      by `SettingsPrefs` (SharedPreferences): Notifications (push, daily reminder),
      Integrations (Notion + Google Calendar connect toggles), Preferences (reduced motion),
      About (version). The Notion row opens a placeholder `ModalBottomSheet`. All toggles
      persist across launches.
- [x] Nav: `Route.Settings` added; `MainScreen` accepts `onOpenSettings`; Profile → Settings,
      Back pops. Onboarding finish now stores niche/goal in prefs.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL; 34 unit tests still pass.

> Note: `SettingsViewModel` is an `AndroidViewModel` (needs `Application` for SharedPreferences),
> so it's not covered by the JVM unit tests (would require Robolectric). Logic is simple
> read/write passthrough.

## Phase 9 — Polish ✅ (core items)
- [x] Tab transitions — `MainScreen` wraps the tab switcher in `Crossfade` (250ms; `snap()`
      when reduced motion is on).
- [x] Reduced-motion handling — `LocalReducedMotion` CompositionLocal provided at the app root
      from `SettingsPrefs.reducedMotion`; `AnimatedBackground` holds its blobs static when on,
      and the tab crossfade becomes instant. (Applies on next launch after toggling.)
- [x] Font-scale stress — added a `fontScale = 1.3f` Dashboard `@Preview`; sp-based type scale
      means layouts reflow without clipping.
- [x] contentDescription — interactive icons described (nav tabs, action buttons, back, bell,
      play, CTA arrow); purely decorative icons intentionally `null`.
- [x] System back — Compose Navigation handles back on Echo/Settings (pops to previous);
      `ModalBottomSheet` intercepts back to dismiss first.
- [x] Status bar — edge-to-edge with transparent bars + light icons set once in `Theme.kt`;
      high-contrast on both the `#242B32` app surface and the black Welcome splash.
- [x] Verified: `./gradlew assembleDebug` BUILD SUCCESSFUL; 34 unit tests pass.

> Remaining polish (optional / needs device): shared-element Flashcard→Echo transition,
> reduced-motion reacting instantly (currently next-launch), full TalkBack pass on a device,
> and the swipe-reactive background (deferred from Phase 5).

## Backend
- `docs/BACKEND_INTEGRATION.md` — how to connect the (currently in-memory) app to a backend
  (Supabase per masterdoc): the data-source seams to replace, repository interfaces, where each
  ViewModel changes, suggested schema + RLS, auth screens, and an integration checklist.

## Status: feature-complete (Phases 0–9). Ready for backend wiring + device QA + lint.
