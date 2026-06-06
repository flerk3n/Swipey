# Design System

Source of truth: `Design.md`. Translate every spacing, radius, and color into Compose
`Modifier` / theme tokens. Never invent values not derived from these tokens.

## Color palette
| Token | Hex | Usage |
| :-- | :-- | :-- |
| `primary` | `#C6FF33` | Brand accent, active states, key icons |
| `secondary` | `#C6FF33` | Primary CTA fill (solid lime) |
| `background` | `#242B32` | Window / screen background (grey-black) — base everywhere |
| `surface` | `#1D2111` | Card/sheet surfaces (glassmorphic, used with low alpha) |
| `onSurface` / `onBackground` | `#FFFFFF` | Primary text + headings |
| `outline` | `#373B2D` | Borders, dividers, inactive states |

> `#242B32` is the single base background for all screens. `#1D2111` (`surfaceVariant`)
> is reserved for elevated card/sheet surfaces layered on top of it.

Semantic aliases to define in `Color.kt` (Phase 1.1):
- `approveColor` = lime `#C6FF33` ("MORE" / like)
- `dismissColor` = violet (the "NOPE" label) — pick a violet and document it
- Card-state colors: selected fill `Color(0x14C6FF33)` (8% lime), unselected fill
  `Color(0x0DFFFFFF)` (5% white).

### Background — decided
The window/screen background is **`#242B32`** (grey-black) everywhere, set once in
`Theme.kt` as `background`. Reference the token in screens — never hardcode the hex. Lime
accents and white text are designed for high contrast against this base. `#1D2111`
(`surfaceVariant`) is the elevated glassmorphic card/sheet tint layered on top, not the base.

> **Content color:** every screen's root should be a `Surface(color = background)` (or sit
> inside the `Scaffold`) so `LocalContentColor` resolves to `onBackground` (white). A bare
> `Column` under `MaterialTheme` with no surface defaults text to **black** — wrap it.

## Theme rules
- **Dark theme only.** No light `ColorScheme`, no `dynamicColor`. Wire the palette into a
  single `darkColorScheme(...)` in `Theme.kt` (background `#242B32`) and force it always.
- CTA buttons are **always** `#C6FF33` lime fill with **black** content. No gradients
  unless a spec explicitly calls for one.
- Glassmorphism = `surfaceVariant` at alpha `0.05–0.10` + a 1dp `outline` border.
- Selected card: 8% lime fill, 1dp solid white border, 4dp left-edge lime glow accent.
  Unselected card: 5% white fill, 1dp solid `outline` border.

## Typography
Fonts: **Instrument Serif** (headings) and **Instrument Sans** (body), loaded via
`GoogleFont` provider (`ui-text-google-fonts`). Define the full scale in `Type.kt`.

| Role | Family | Size | Weight | Style |
| :-- | :-- | :-- | :-- | :-- |
| Headline Large | Instrument Serif | 32sp | 700 | Italic |
| Title Medium | Instrument Serif | 22sp | 600 | Normal |
| Body Large | Instrument Sans | 16sp | 400 | Normal |
| Label Large | Instrument Sans | 14sp | 500 | Muted |

- Provide a helper `buildMixedHeadline(plain: String, emphasis: String): AnnotatedString`
  for mixed-weight/italic headlines (used by the Dashboard greeting in Phase 7.3).
- Map these onto Material 3 `Typography` slots so components inherit them.

## Spacing & shape (define in `Spacing.kt`)
- `ScreenPadding = 20.dp` (roadmap baseline). Design.md also cites 24dp horizontal /
  32dp vertical layout margins — use `ScreenPadding` as the single shared token and add
  named extras (`HorizontalMargin`, `VerticalMargin`) if a screen needs the larger values.
- Section spacing: `24.dp`
- Card radius: `24.dp` (large), `16.dp` (medium)
- Button radius: `100.dp` (full pill)
- Input radius: `12.dp`
- Use `Modifier` for all paddings/margins/radii. Don't hardcode width/height — prefer
  `fillMaxWidth()` + `weight()`. Fixed sizes only where strictly required (e.g. 56dp CTA
  height, 56dp circular action buttons).

## Component specs
- **TopAppBar:** transparent background; title uses Headline Large (italic). Center-aligned.
- **BottomNavPill:** `NavigationBar`, floating pill ~8dp from bottom, `Row` with
  `Arrangement.SpaceAround`. Persistent across Flashcard / Dashboard / Profile.
- **CTA Button:** `fillMaxWidth()`, `height(56.dp)`, lime container, black content, pill radius.
- **SwipeyTag:** chip/pill, toggleable; used inside cards and the Echo tag sheet.
- **Selection cards:** glassmorphic per the state rules above (onboarding niche/topic/goal).
- **Idea cards:** clean **solid** surface (`SwipeyCardSurface` #2D353E, rounded 24dp, subtle
  shadow) — **no glassmorphism/gradient**. Kept simple and elegant per product decision.
- **Welcome splash:** intentionally off-theme — pure black (`SwipeyBlack`) canvas with
  violet (`SwipeyViolet` #7D39EB) radial glow blobs, floating tilted idea cards (one
  glassmorphic white, one solid lime), oversized faint serif "Swipey", inline-pill serif
  headline, and a lime CTA pill with a black circular ↗ button. Matches the provided HTML.
- **CircularActionButton:** ✕ / ↑ / ♡ on the Flashcard screen; mirror the swipe gestures.
- **Swipe indicators:** feedback is given by tinting the **whole card** with drag progress —
  green (`SwipeApprove`) for right (like/more) and up (save), red (`SwipeReject`) for left
  (dismiss). No "NOPE/MORE/SAVE" stamp overlays.

## Motion
- `AnimatedVisibility` for card-selection transitions.
- `Modifier.graphicsLayer` for floating/parallax effects (splash blobs, drag rotation).
- Swipe release: past threshold → animate off-screen; below → spring back. Stack shifts
  with spring animations.
- Honor reduced-motion settings in the Phase 9 polish pass.

## Assets
Use **placeholder images** wherever a final asset is missing (per project rules). Registry
tokens from `Design.md`: `LOGO_MAIN`, `ONBOARDING_HERO`, `ICON_CALENDAR`, `ICON_NOTION`,
`TREND_BG_ABSTRACT`. Reference vector assets from `res/drawable`; use solid/gradient
placeholders for raster art until provided.

## Accessibility
Every icon button needs a `contentDescription`. Verify layouts at 130% font scale (Phase 9).
Maintain contrast — the lime-on-black system is high-contrast by design; keep it that way.
