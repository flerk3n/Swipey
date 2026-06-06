package com.harsh.swipey.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Whether the user has enabled "Reduced motion" in Settings. Provided at the app root and
 * read by animated composables (e.g. [com.harsh.swipey.ui.components.AnimatedBackground])
 * to skip or shorten non-essential motion.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }
