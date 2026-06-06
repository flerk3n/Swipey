package com.harsh.swipey.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.theme.LocalReducedMotion
import com.harsh.swipey.ui.theme.SwipeyBackground
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Ambient screen background: soft glow blobs that slowly drift. The glow [accent]
 * can be animated by the Flashcard screen to react to swipe direction
 * (lime = like/save, violet = dismiss). Renders its [content] on top.
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accent: Color = SwipeyLime,
    content: @Composable () -> Unit = {},
) {
    val reducedMotion = LocalReducedMotion.current
    val transition = rememberInfiniteTransition(label = "bgFloat")
    val animatedDrift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    // Hold the blobs at a fixed position when reduced motion is enabled.
    val drift = if (reducedMotion) 0.5f else animatedDrift
    val animatedAccent by animateColorAsState(targetValue = accent, label = "bgAccent")

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(SwipeyBackground),
    ) {
        Blob(
            color = animatedAccent.copy(alpha = 0.16f),
            sizeDp = 420,
            translationXFactor = -120f + drift * 60f,
            translationYFactor = -160f + drift * 80f,
        )
        Blob(
            color = animatedAccent.copy(alpha = 0.10f),
            sizeDp = 360,
            translationXFactor = 180f - drift * 90f,
            translationYFactor = 360f + drift * 70f,
        )
        content()
    }
}

@Composable
private fun Blob(
    color: Color,
    sizeDp: Int,
    translationXFactor: Float,
    translationYFactor: Float,
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .graphicsLayer {
                translationX = translationXFactor
                translationY = translationYFactor
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                ),
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 480)
@Composable
private fun AnimatedBackgroundPreview() {
    SwipeyTheme {
        AnimatedBackground(modifier = Modifier.fillMaxSize())
    }
}
