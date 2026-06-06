package com.harsh.swipey.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Swipey palette (from Design.md). Dark theme only.
// ---------------------------------------------------------------------------

/** Brand accent / active states / key icons / CTA fill. */
val SwipeyLime = Color(0xFFC6FF33)

/** Window + screen background (grey-black). The single base background everywhere. */
val SwipeyBackground = Color(0xFF242B32)

/** Elevated card / sheet surface (glassmorphic base, used with low alpha). */
val SwipeySurface = Color(0xFF1D2111)

/** Solid, elevated surface for idea cards — clean & elegant (no glass/gradient). */
val SwipeyCardSurface = Color(0xFF2D353E)

/** Primary text + headings on dark surfaces. */
val SwipeyOnSurface = Color(0xFFFFFFFF)

/** Borders, dividers, inactive states. */
val SwipeyOutline = Color(0xFF373B2D)

/** Content color that sits on top of the lime CTA fill. */
val SwipeyOnLime = Color(0xFF000000)

// ---------------------------------------------------------------------------
// Semantic aliases
// ---------------------------------------------------------------------------

/** Swipe-right / "MORE" / like action. */
val ApproveColor = SwipeyLime

/** Accent violet — card category eyebrows + the "NOPE" / dismiss label. */
val AccentViolet = Color(0xFF8B5CF6)

/** Brand violet used on the visual-heavy Welcome splash (glow blobs, accents). */
val SwipeyViolet = Color(0xFF7D39EB)

/** Pure black — the Welcome splash background (distinct from the app's #242B32). */
val SwipeyBlack = Color(0xFF000000)

/** Swipe-left / "NOPE" / dismiss action. */
val DismissColor = AccentViolet

/** Swipe-up / "SAVE" action. */
val SaveColor = SwipeyLime

// --- Swipe feedback tints (whole-card color transition) --------------------

/** Right-swipe (like / more) and up-swipe (save) tint the card green. */
val SwipeApprove = Color(0xFF22C55E)

/** Left-swipe (dismiss) tints the card red. */
val SwipeReject = Color(0xFFEF4444)

// ---------------------------------------------------------------------------
// Glassmorphic card states (Design.md §3)
// ---------------------------------------------------------------------------

/** Selected card fill — 8% lime. */
val CardFillSelected = Color(0x14C6FF33)

/** Unselected card fill — 5% white. */
val CardFillUnselected = Color(0x0DFFFFFF)

/** Left-edge accent / glow on a selected card. */
val CardAccentSelected = SwipeyLime

/** Subtle text / muted labels (Label Large role). */
val SwipeyOnSurfaceMuted = Color(0xB3FFFFFF) // 70% white
