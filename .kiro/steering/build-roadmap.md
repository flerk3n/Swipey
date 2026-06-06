---
inclusion: manual
---

# Build Roadmap

Phased plan from `Phases.md`. Manual-include this file (`#build-roadmap`) when working a
phase so the always-on steering stays lean.

## The golden rule
**Theme → Atoms → Core interaction → one screen complete → next screen complete.**
Never start a new screen until the current one has working state, real navigation, and real
edge cases handled. Breadth-first produces "a beautiful corpse." Build depth-first.

## Prompting discipline
One task per session. The AI drifts when asked for "the whole Dashboard." Ask for a single
composable (e.g. `GoalCard`), then `StatsStrip`, then the screen that assembles them. State:
current phase/step, what already exists, the single artifact to build, and the constraints
(no networking, passed-in/hardcoded data, must have `@Preview`, follow theme tokens).

---

## Phase 0 — Setup (once)
Add deps via the version catalog (see `tech.md`): navigation-compose, viewmodel-compose,
lifecycle-runtime-compose, ui-text-google-fonts. Create the package layout from
`structure.md` lazily as phases need it.

## Phase 1 — Theme foundation (one file at a time)
1.1 `Color.kt` — the four palette colors + surface variants + semantic aliases
    (`approveColor`, `dismissColor`, card-state fills). Nothing else.
1.2 `Type.kt` — Instrument Serif + Instrument Sans via GoogleFont; every text style in the
    scale; `buildMixedHeadline(plain, emphasis)` helper.
1.3 `Spacing.kt` — `object Spacing` with all dp tokens, incl. `ScreenPadding = 20.dp`.
1.4 `Theme.kt` — wire colors + type into `MaterialTheme`; **force dark always**; no light
    scheme, no dynamic color.
**Checkpoint:** a `Text("Hello")` preview renders with the correct fonts.

## Phase 2 — Atomic components (each with a `@Preview`, visual-first)
Order: 2.1 `SwipeyTag` → 2.2 `IdeaCard` (full) → 2.3 `IdeaCard` (compact) →
2.4 Primary/Secondary/Ghost buttons → 2.5 `CircularActionButton` (✕ ↑ ♡) →
2.6 `BottomNavPill` → 2.7 `SwipeyToggle` → 2.8 `AnimatedBackground` → 2.9 `SaveToast`.
Build each with hardcoded data, no state/callbacks, pixel-perfect in Preview first.

## Phase 3 — The swipe core (do before any screen)
3.1 `SwipeableCard` — draggable via `Modifier.pointerInput` + `detectDragGestures`;
    rotation on drag; opacity fade near threshold.
3.2 Swipe indicators — "NOPE" (violet), "MORE" (lime), "SAVE" (up), fade in on drag.
3.3 Threshold + dismiss animation — past threshold animates off-screen, else springs back.
    Pure animation, hardcoded `IdeaModel`s.
3.4 `CardStack` — renders 3 cards with stack-depth transforms (scale + translationY);
    shifts remaining cards on dismiss and appends a new one at index 2.
3.5 `CardStackState` — plain state holder over a `SnapshotStateList` with
    `onSwipeLeft/Right/Up()`. **Not** a ViewModel yet.
**Checkpoint:** swipe through 5 hardcoded cards with correct animations + stack behavior.
This is the demo-able heart of the product.

## Phase 4 — Onboarding (complete)
4.1 `OnboardingViewModel` — `selectedNiche/Frequency/Goal` + setters. State only.
4.2 `WelcomeScreen` — static; one CTA navigates onward.
4.3 `NicheSelectionScreen` — reusable: `(question, options, onOptionSelected, onContinue)`;
    same composable renders all three onboarding steps.
4.4 Onboarding NavGraph — Welcome → Step1 → Step2 → Step3 → Flashcard; persist completion in
    SharedPreferences so returning users skip onboarding.
**Checkpoint:** cold launch → Welcome → 3 steps → selections persist → navigates away;
relaunch skips onboarding.

## Phase 5 — Flashcard screen (complete) — MVP
5.1 `FlashcardViewModel` — card queue as state; `onDismiss/onLike/onSave(id)`; `onSave`
    appends to an in-memory saved list; ~20 realistic seed ideas.
5.2 `FlashcardScreen` — wires `CardStack` + `CircularActionButton`s + `AnimatedBackground` +
    `BottomNavPill`; buttons call the same functions as gestures; background reacts to swipe.
5.3 Edge states — empty ("ALL CAUGHT UP"), loading skeleton; swapped via a sealed `UiState`.
5.4 Save toast — `onSave` drives `SaveToastState` showing `SaveToast` ~2.5s via
    `LaunchedEffect` + `snapshotFlow`.
**Checkpoint:** full swipe loop; saves toast; nav-pill tabs work (placeholders OK).

## Phase 6 — Expanded Echo (complete) — ship-ready point
6.1 `ExpandedEchoScreen` — takes `ideaId` nav arg; title, description, empty generation canvas.
6.2 Typewriter — `LaunchedEffect(generatedText)` reveals one char every ~22ms into
    `displayedText`. No library.
6.3 Tag bottom sheet — `ModalBottomSheet`; existing tags as toggleable `SwipeyTag`s; text
    field for new tag; Done updates the VM.
6.4 Transition — right swipe / Like navigates to Echo with the id; back press pops to Flashcard.
**Checkpoint:** liked card opens Echo; typewriter plays on Generate (dummy text); tags add.

## Phase 7 — Dashboard (complete)
7.1 `DashboardViewModel` — reads saved ideas from the same in-memory store
    `FlashcardViewModel` writes; goal state; simple streak (count saves by date).
7.2 `DashboardScreen` — `LazyColumn` of sections; horizontal `LazyRow` of saved cards; stats
    strip; goal card; activity list. Each section its own composable.
7.3 Mixed-type greeting — `buildMixedHeadline`; time-of-day via `when` on `LocalTime.now().hour`.
**Checkpoint:** saved ideas appear on Dashboard; numbers are real, not hardcoded.

## Phase 8 — Profile + Settings (quick, mostly UI)
8.1 `ProfileScreen` — static; reads niche from onboarding state + saved count from shared VM.
8.2 `SettingsScreen` — toggles backed by `SettingsViewModel` → SharedPreferences; Notion row
    navigates out or shows a placeholder sheet.

## Phase 9 — Polish (only after end-to-end works)
Screen transitions (crossfade tabs; shared-element Flashcard→Echo), reduced-motion handling,
130% font-scale stress test, all `contentDescription`s, per-screen status-bar color, system
back-gesture handling on Expanded Echo.

---

## Milestones
- **Phases 0–3:** foundation (theme, atoms, swipe core).
- **Phases 4–5:** MVP.
- **Phase 6:** shippable if time is short.
- **Phases 7–9:** full experience + polish.
