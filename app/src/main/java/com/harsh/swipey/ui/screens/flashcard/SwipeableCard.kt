package com.harsh.swipey.ui.screens.flashcard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.harsh.swipey.data.model.SampleIdeas
import com.harsh.swipey.ui.components.IdeaCard
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeApprove
import com.harsh.swipey.ui.theme.SwipeReject
import com.harsh.swipey.ui.theme.SwipeyTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** The three swipe gestures the card supports. */
enum class SwipeDirection { Left, Right, Up }

private const val MAX_ROTATION_DEG = 14f
private const val HORIZONTAL_THRESHOLD_FRACTION = 0.32f
private const val VERTICAL_THRESHOLD_FRACTION = 0.28f
private const val MAX_TINT_ALPHA = 0.6f

/**
 * A single draggable card. Rotates with horizontal drag, animates off-screen when a
 * gesture passes the threshold, or springs back otherwise. Stateless w.r.t. the data —
 * it just reports the resulting [SwipeDirection] via [onSwiped].
 *
 * Feedback is given by tinting the whole card: green for right (like / more) and up
 * (save), red for left (dismiss). The tint strength scales with drag progress.
 *
 * @param shape used to clip the feedback tint to the card's corners.
 * @param enabled only the top card of a stack should be draggable.
 */
@Composable
fun SwipeableCard(
    onSwiped: (SwipeDirection) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Spacing.CardRadiusLarge),
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val thresholdX = size.width * HORIZONTAL_THRESHOLD_FRACTION
    val thresholdY = size.height * VERTICAL_THRESHOLD_FRACTION

    fun dismiss(targetX: Float, targetY: Float, direction: SwipeDirection) {
        scope.launch {
            coroutineScope {
                launch { offsetX.animateTo(targetX, tween(durationMillis = 280)) }
                launch { offsetY.animateTo(targetY, tween(durationMillis = 280)) }
            }
            onSwiped(direction)
        }
    }

    fun springBack() {
        val spec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy)
        scope.launch { offsetX.animateTo(0f, spec) }
        scope.launch { offsetY.animateTo(0f, spec) }
    }

    // Whole-card tint based on the dominant drag axis.
    val (tintColor, tintAlpha) = computeTint(
        offsetX = offsetX.value,
        offsetY = offsetY.value,
        thresholdX = thresholdX,
        thresholdY = thresholdY,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = if (size.width > 0) {
                    (offsetX.value / size.width) * MAX_ROTATION_DEG
                } else {
                    0f
                }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDrag = { change, drag ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + drag.x)
                            offsetY.snapTo(offsetY.value + drag.y)
                        }
                    },
                    onDragEnd = {
                        val x = offsetX.value
                        val y = offsetY.value
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        when {
                            x > thresholdX -> dismiss(w * 1.6f, y, SwipeDirection.Right)
                            x < -thresholdX -> dismiss(-w * 1.6f, y, SwipeDirection.Left)
                            y < -thresholdY -> dismiss(x, -h * 1.6f, SwipeDirection.Up)
                            else -> springBack()
                        }
                    },
                )
            },
    ) {
        content()
        // Tint overlay — clipped to the card shape so corners match.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(tintColor.copy(alpha = tintAlpha)),
        )
    }
}

/** Returns the feedback (color, alpha) for the current drag offset. */
private fun computeTint(
    offsetX: Float,
    offsetY: Float,
    thresholdX: Float,
    thresholdY: Float,
): Pair<Color, Float> {
    fun progress(value: Float, threshold: Float): Float =
        if (threshold <= 0f) 0f else (value / threshold).coerceIn(0f, 1f)

    val horizontalDominant = abs(offsetX) >= abs(offsetY)
    return when {
        horizontalDominant && offsetX > 0f ->
            SwipeApprove to progress(offsetX, thresholdX) * MAX_TINT_ALPHA
        horizontalDominant && offsetX < 0f ->
            SwipeReject to progress(-offsetX, thresholdX) * MAX_TINT_ALPHA
        !horizontalDominant && offsetY < 0f ->
            SwipeApprove to progress(-offsetY, thresholdY) * MAX_TINT_ALPHA
        else -> Color.Transparent to 0f
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 640)
@Composable
private fun SwipeableCardPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SwipeableCard(
                onSwiped = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .padding(Spacing.lg),
            ) {
                IdeaCard(idea = SampleIdeas.hooks, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
