package com.harsh.swipey.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized spacing, shape-radius, and sizing tokens (Design.md §1).
 * Reference these everywhere — never hardcode raw dp values in screens.
 */
object Spacing {

    // --- Generic scale -----------------------------------------------------
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    // --- Layout ------------------------------------------------------------
    /** Baseline screen padding used across all screens. */
    val ScreenPadding = 20.dp

    /** Design.md layout margins (use when a screen needs the larger values). */
    val HorizontalMargin = 24.dp
    val VerticalMargin = 32.dp

    /** Vertical gap between major sections. */
    val SectionSpacing = 24.dp

    // --- Shape radii -------------------------------------------------------
    val CardRadiusLarge = 24.dp
    val CardRadiusMedium = 16.dp

    /** Full-round / pill radius for buttons and nav. */
    val ButtonRadius = 100.dp

    /** Input field corner radius. */
    val InputRadius = 12.dp

    // --- Fixed component sizes (only where strictly required) --------------
    /** Primary CTA button height. */
    val CtaHeight = 56.dp

    /** Circular action button (✕ ↑ ♡) diameter on the Flashcard screen. */
    val ActionButtonSize = 56.dp

    /** Default stroke width for outlines / borders. */
    val BorderWidth = 1.dp

    /** Left-edge accent width on a selected glassmorphic card. */
    val CardAccentWidth = 4.dp

    /** Distance the floating bottom nav pill sits above the bottom edge. */
    val BottomNavFloat = 8.dp
}
