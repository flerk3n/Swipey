# Tech Stack & Build

## Platform
- **Language:** Kotlin `2.0.21`
- **UI:** Jetpack Compose + Material 3 (`androidx.compose.material3`)
- **Build:** Gradle (Kotlin DSL) with the **version catalog** at `gradle/libs.versions.toml`
- **AGP:** `8.12.3`
- **compileSdk / targetSdk:** `36` · **minSdk:** `24`
- **Java / JVM target:** `11`
- **Compose BOM:** `2024.09.00`
- **App namespace / applicationId:** `com.harsh.swipey`

## Dependency rules
- **Always add dependencies through the version catalog** (`libs.versions.toml`), then
  reference them as `libs.*` in `app/build.gradle.kts`. Do not hardcode versioned
  coordinate strings directly in the build file — match the existing pattern.
- Pin versions in `[versions]`; declare libraries in `[libraries]`; declare plugins in
  `[plugins]`.
- Prefer stable Material 3 APIs. Avoid deprecated or unstable experimental APIs that cause
  Gradle sync or compile failures. When an API requires opt-in (e.g.
  `@OptIn(ExperimentalMaterial3Api::class)` for `ModalBottomSheet`, `TopAppBar`), add the
  opt-in explicitly rather than switching to a deprecated alternative.

## Dependencies to add (per build-roadmap Phase 0)
Not yet in the project — add when the relevant phase starts:
- `androidx.navigation:navigation-compose` (~`2.7.7`) — navigation
- `androidx.lifecycle:lifecycle-viewmodel-compose` (~`2.7.0`) — ViewModel in Compose
- `androidx.lifecycle:lifecycle-runtime-compose` (~`2.7.0`) — `collectAsStateWithLifecycle`
- `androidx.compose.ui:ui-text-google-fonts` — Instrument Serif / Sans via GoogleFont
- `androidx.compose.ui:ui-tooling-preview` + `debugImplementation ui-tooling` — already present, keep for `@Preview`

Already present: `core-ktx`, `lifecycle-runtime-ktx`, `activity-compose`, Compose BOM,
`ui`, `ui-graphics`, `ui-tooling-preview`, `material3`, test/androidTest deps.

## Deferred stack (NOT for the build phases)
`masterdoc.md` specifies Supabase (auth-kt, postgrest-kt, realtime-kt), Ktor, and Google
Play Services Auth for the eventual backend. **Do not add these during Phases 0–9.** The
build phases are local-only with hardcoded/in-memory data. Introduce networking as a
separate, later milestone and revisit `minSdk`/secrets handling (`BuildConfig.SUPABASE_*`)
at that point.

## Build & test commands
Run from the project root. The Gradle wrapper is committed.

```bash
./gradlew assembleDebug        # compile the debug APK (use to verify a change builds)
./gradlew installDebug         # build + install on a connected device/emulator
./gradlew testDebugUnitTest    # JVM unit tests (app/src/test)
./gradlew connectedDebugAndroidTest   # instrumented tests (needs device/emulator)
./gradlew lint                 # Android lint
./gradlew clean                # clean build outputs
```

- Do **not** start long-running watch/daemon commands in the agent shell. After a code
  change, verify with `./gradlew assembleDebug`.
- Compose previews are the primary visual verification tool — every component must ship a
  `@Preview` (see `code-standards.md`).

## Secrets
- `local.properties` is machine-local and git-ignored — never commit it.
- When backend work begins, keep `SUPABASE_URL` / `SUPABASE_ANON_KEY` out of source;
  inject via `local.properties` → `BuildConfig`. Do not paste keys into tracked files.
