The Golden Rule
Theme → Atoms → Core Interaction → One screen complete → Next screen complete
Never jump to a new screen until the current one has working state, real navigation, and real edge cases handled. An AI building breadth-first will produce a beautiful corpse.

Phase 0 — Project Setup
Do this once, never touch again.
1. Gradle dependencies to add:
   kotlin// Compose BOM (use latest stable)
   implementation(platform("androidx.compose:compose-bom:2024.05.00"))
   implementation("androidx.compose.ui:ui")
   implementation("androidx.compose.material3:material3")
   implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Fonts
implementation("androidx.compose.ui:ui-text-google-fonts")

// ViewModel + State
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
2. Package structure to create:
   com.swipey/
   ├── ui/
   │   ├── theme/          ← Colors, Type, Theme, Spacing
   │   ├── components/     ← Every reusable composable
   │   ├── screens/
   │   │   ├── onboarding/
   │   │   ├── flashcard/
   │   │   ├── dashboard/
   │   │   ├── profile/
   │   │   └── settings/
   │   └── navigation/     ← NavGraph, Routes
   ├── data/
   │   └── model/          ← Data classes only, no networking
   └── MainActivity.kt

Phase 1 — Theme Foundation
Prompt the AI with your design.md and ask for these files in order. One file at a time.
Step 1.1 — Color.kt
All four palette colors + surface variants + semantic aliases (approveColor, dismissColor, etc.). Nothing else in this file.
Step 1.2 — Type.kt
Instrument Serif + Instrument Sans loaded via GoogleFont. Define every text style from the scale. Include a helper for the mixed-type AnnotatedString pattern (buildMixedHeadline(plain: String, emphasis: String)).
Step 1.3 — Spacing.kt
A simple object Spacing with all dp tokens. Include ScreenPadding = 20.dp.
Step 1.4 — Theme.kt
Wire Colors + Type into MaterialTheme. Force dark theme always. No light theme surface variants needed.
Checkpoint: Preview a simple Text("Hello") in Android Studio using your theme. If fonts render correctly, Phase 1 is done.

Phase 2 — Atomic Components
Build each one with a @Preview before moving to the next. These are your Lego bricks.
Build in this order:
StepComponentWhy this order2.1SwipeyTag (chip/pill)Used inside every card2.2IdeaCard — full sizeThe entire product lives here2.3IdeaCard — compactUsed on Dashboard2.4Primary / Secondary / Ghost ButtonUsed everywhere2.5CircularActionButton (✕ ↑ ♡)Used on Flashcard screen2.6BottomNavPillPersistent across 3 screens2.7SwipeyToggleUsed in Settings2.8AnimatedBackgroundBehind every screen2.9SaveToastGlobal feedback element
Rule for each component: Build it with hardcoded preview data first. No state, no callbacks — just the visual. Get it pixel-perfect in Preview before you add any logic.

Phase 3 — The Swipe Core
This is the hardest technical piece and the heart of the product. Do it before any screen.
Step 3.1 — SwipeableCard composable
A single card that can be dragged. Uses Modifier.pointerInput with detectDragGestures. Handles rotation on drag, opacity fade near threshold.
Step 3.2 — Swipe indicators
"NOPE" (violet) and "MORE" (lime) labels that fade in on drag. "SAVE" on upward drag. These are children of SwipeableCard.
Step 3.3 — Threshold + dismiss animation
On release: if past threshold, animate card off screen. If below threshold, spring back. This is a pure animation problem — no real data yet, use hardcoded IdeaModel objects.
Step 3.4 — CardStack composable
Renders 3 cards from a list. Manages the stack-depth transforms (scale + translationY). When top card is dismissed, shifts remaining cards with spring animation and appends a new card at index 2.
Step 3.5 — CardStackState
A simple state holder (not ViewModel yet) that holds the card queue as a SnapshotStateList. onSwipeLeft(), onSwipeRight(), onSwipeUp() functions that mutate the list.
Checkpoint: You should now be able to swipe through a hardcoded list of 5 idea cards with correct animations and stack behavior. This is your demo-able moment. If this works, everything else is just UI around it.

Phase 4 — Onboarding Flow (complete)
Now build your first real screen, fully.
Step 4.1 — OnboardingViewModel
Holds: selectedNiche, selectedFrequency, selectedGoal. onNicheSelected(), onFrequencySelected(), onGoalSelected(). No networking, just state.
Step 4.2 — WelcomeScreen
Static layout. One CTA that triggers navigation to niche selection.
Step 4.3 — NicheSelectionScreen
Reusable composable that takes question: String, options: List<String>, onOptionSelected, onContinue. The same screen renders 3 times for the 3 onboarding questions. Navigation passes which step it is.
Step 4.4 — Onboarding NavGraph
Welcome → NicheStep1 → NicheStep2 → NicheStep3 → FlashcardScreen. Save completion to SharedPreferences so returning users skip onboarding.
Checkpoint: Cold launch shows Welcome. You can tap through all 3 niche screens. Selections persist in ViewModel. Final screen navigates away. Relaunching skips onboarding.

Phase 5 — Flashcard Screen (complete)
Your Phase 3 work snaps in here.
Step 5.1 — FlashcardViewModel
Holds the card queue as state. onDismiss(id), onLike(id), onSave(id). For now, onSave just adds to a saved list in memory. Use hardcoded seed data — 20 idea objects with real-ish content.
Step 5.2 — FlashcardScreen
Wires CardStack + CircularActionButtons + AnimatedBackground + BottomNavPill. Action buttons call the same functions as swipe gestures. Background blob reacts to swipe direction.
Step 5.3 — Edge states
Empty state ("ALL CAUGHT UP" layout). Loading skeleton. These are just composables swapped in based on a UiState sealed class.
Step 5.4 — Save toast
onSave triggers SaveToastState that shows the SaveToast for 2.5 seconds. Use LaunchedEffect + snapshotFlow to drive the delay.
Checkpoint: Full swipe loop works. Cards dismiss correctly. Save shows toast. Navigating to other tabs via nav pill works (even if target screens are empty placeholders for now).

Phase 6 — Expanded Echo (complete)
Step 6.1 — ExpandedEchoScreen
Takes ideaId as nav argument. Shows idea title, description, empty generation canvas.
Step 6.2 — Typewriter animation
LaunchedEffect(generatedText) loop that reveals one character every 22ms into a displayedText: String state. Simple, no libraries needed.
Step 6.3 — Tag bottom sheet
ModalBottomSheet composable. Existing tags as SwipeyTag chips (toggleable). Text field for new tag. Done button updates ViewModel.
Step 6.4 — Transition from Flashcard
Right swipe or "Like" button navigates to ExpandedEchoScreen with the idea id. On back press, pops to Flashcard.
Checkpoint: Tapping a liked card opens Expanded Echo. Typewriter animation plays when Generate is pressed (with dummy text). Tags can be added.

Phase 7 — Dashboard (complete)
Step 7.1 — DashboardViewModel
Reads saved ideas from the same in-memory store written to by FlashcardViewModel. Goal state. Streak counter (derived from dates of saves — keep it simple, just count for now).
Step 7.2 — DashboardScreen
LazyColumn with all sections. Horizontal LazyRow for saved idea cards. Stats strip. Goal card. Activity list. Each section is its own composable function.
Step 7.3 — Mixed-type greeting
Use your buildMixedHeadline helper from Phase 1. Time-of-day logic is just a when block on LocalTime.now().hour.
Checkpoint: Saved ideas from Flashcard screen appear on Dashboard. Numbers are real (not hardcoded).

Phase 8 — Profile + Settings (complete)
These are pure UI — no complex state. Build them fast.
Step 8.1 — ProfileScreen — static for now, reads niche from onboarding state and saved count from shared VM.
Step 8.2 — SettingsScreen — all toggles backed by a SettingsViewModel that writes to SharedPreferences. Notion integration row navigates out (or shows a placeholder sheet).

Phase 9 — Polish Pass
Only do this after all screens work end-to-end.

Screen transitions (crossfade for tab switches, shared element for Flashcard → Echo)
Reduced motion handling
Font scaling stress test at 130%
All contentDescription on icon buttons
Status bar color per screen
System back gesture handling on Expanded Echo


Cheat Sheet: What to Tell the AI Each Session
Structure every prompt like this:
Context: Building Swipey, Kotlin Jetpack Compose, design in design.md.
Current phase: [Phase X, Step Y]
Already built: [list what exists]
Task: Build [specific composable/screen/ViewModel].
Constraints:
- No networking or APIs
- Use hardcoded/passed-in data
- Must have @Preview
- Follow theme tokens from Color.kt and Type.kt
  Output: [single file or named composable only]
  One task per session. The AI drifts badly when asked to do "the whole Dashboard" — ask for GoalCard composable first, then StatsStrip, then the screen that assembles them.

The Order, Visualised
Phase 0   Setup
↓
Phase 1   Theme (Colors → Type → Spacing → Theme)
↓
Phase 2   Atoms (Tag → Card → Buttons → Nav → Background → Toast)
↓
Phase 3   Swipe Core  ← hardest, do not skip
↓
Phase 4   Onboarding (complete: VM + screens + nav)
↓
Phase 5   Flashcard (complete: VM + swipe wired + states)
↓
Phase 6   Expanded Echo (complete: transition + typewriter + tags)
↓
Phase 7   Dashboard (complete: real data from shared VM)
↓
Phase 8   Profile + Settings (quick)
↓
Phase 9   Polish
Phases 0–3 are setup. Phase 4–5 is your MVP. If time is short, ship after Phase 6.