package com.harsh.swipey.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Swipey is dark-theme only: no light scheme, no dynamic color.
 * The single base background is [SwipeyBackground] (#252736).
 */
private val SwipeyColorScheme = darkColorScheme(
    primary = SwipeyLime,
    onPrimary = SwipeyOnLime,
    secondary = SwipeyLime,
    onSecondary = SwipeyOnLime,
    tertiary = SwipeyLime,
    onTertiary = SwipeyOnLime,
    background = SwipeyBackground,
    onBackground = SwipeyOnSurface,
    surface = SwipeySurface,
    onSurface = SwipeyOnSurface,
    surfaceVariant = SwipeySurface,
    onSurfaceVariant = SwipeyOnSurfaceMuted,
    outline = SwipeyOutline,
    error = DismissColor,
    onError = SwipeyOnSurface,
)

@Composable
fun SwipeyTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // enableEdgeToEdge() handles transparent bars; here we only ensure
            // light system-bar icons for contrast against the dark background.
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = SwipeyColorScheme,
        typography = SwipeyTypography,
        content = content,
    )
}
