# Coding Standards (strict)

Act as an expert senior Android developer. Code must be production-ready, compile-ready,
and pixel-perfect on the first pass. These rules are non-negotiable.

## Zero stubs, zero bugs
- **Never** leave `// TODO`, placeholders, truncated classes, or incomplete functions.
- Every referenced composable (buttons, chips, lists, layouts, screens) must be **fully
  implemented** — no "left as an exercise" gaps.
- Types, names, and parameter lists must match **exactly** across ViewModel ↔ Screen ↔
  NavHost so the project always compiles. After a change, verify with `./gradlew assembleDebug`.
- If a value isn't available yet, use realistic hardcoded/seed data — not a stub.

## Compose & state (UDF)
- Composables are **stateless**: hoist state, pass events down as lambdas. Only the
  top-level route composable touches a `ViewModel`; inner composables take plain params.
- ViewModels expose immutable `StateFlow`; mutate a private `MutableStateFlow`. Collect with
  `collectAsStateWithLifecycle()`.
- Use `remember` / `rememberSaveable` correctly; key `LaunchedEffect` / `derivedStateOf`
  deliberately. Don't read mutable state outside composition.
- Prefer stable, non-deprecated Material 3 APIs. Add explicit `@OptIn` for required
  experimental APIs rather than reaching for deprecated ones.

## Layout & responsiveness
- Translate design tokens via `Modifier`; pull values from `Color.kt` / `Type.kt` /
  `Spacing.kt`, never inline hex or magic dp.
- No hardcoded width/height unless strictly required — use `fillMaxWidth()` + `weight()`.
- Edge-to-edge aware: apply `statusBarsPadding()` / `navigationBarsPadding()` (or
  `WindowInsets`) so content never sits under system bars.

## Previews are mandatory
- Every component and screen ships a `@Preview` (named `<Name>Preview`) wrapped in
  `SwipeyTheme`, using hardcoded preview data.
- Build the visual with hardcoded data **first**, get it pixel-perfect in Preview, then add
  logic/callbacks. (Phase 2 rule — applies throughout.)
- **Never use `material-icons-extended`.** Its classes are not on the Compose Preview
  (Layoutlib) classpath, so any extended icon throws
  `ClassNotFoundException: ...Kt` when a preview renders (and lazy `get()` resolution does
  not help — the class is simply absent). Use only `material-icons-core` icons (Close,
  Favorite, KeyboardArrowUp, Check, PlayArrow, Person, etc.) or hand-built vectors in
  `ui/components/SwipeyIcons.kt`. Also never store `ImageVector`s as eager `val`s in an
  enum constructor / `object` — resolve them lazily (`by lazy`) or in a `get()`.

## Packaging & scope
- Single Activity. One centralized `NavHost`. Strongly-typed sealed-class routes. Screens
  decoupled from navigation via lambda params. (See `structure.md`.)
- Base package is `com.harsh.swipey` — match the current directory layout, don't hardcode
  `com.swipey`.
- No networking/APIs during the build phases — hardcoded / in-memory data only.

## Working discipline
- **One task per change.** Build the named composable/ViewModel/screen requested — don't
  pre-build the rest of a screen. Follow the depth-first order in `build-roadmap.md`.
- Read existing theme/components before writing new ones; reuse atoms instead of
  re-implementing. Match established patterns and naming.
- Add `contentDescription` to every icon button as you build it (don't defer all a11y to
  Phase 9).
