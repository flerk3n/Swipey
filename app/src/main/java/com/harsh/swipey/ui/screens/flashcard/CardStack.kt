package com.harsh.swipey.ui.screens.flashcard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.model.SampleIdeas
import com.harsh.swipey.ui.components.IdeaCard
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyTheme

private const val VISIBLE_COUNT = 3
private const val DEPTH_SCALE_STEP = 0.04f
private val DEPTH_PEEK = 14.dp

/**
 * Renders up to [VISIBLE_COUNT] cards from [cards] as a stack. Only the top card is
 * draggable; cards behind are scaled down and offset, and animate forward when the top
 * card is dismissed. Reports each swipe via [onSwiped].
 *
 * The deck is a plain list owned by the caller (the FlashcardViewModel) for UDF.
 *
 * @param cardContent renders a single card's visuals; should fill the available space.
 */
@Composable
fun CardStack(
    cards: List<IdeaModel>,
    modifier: Modifier = Modifier,
    onSwiped: (IdeaModel, SwipeDirection) -> Unit = { _, _ -> },
    cardContent: @Composable (IdeaModel) -> Unit,
) {
    val peekPx = with(LocalDensity.current) { DEPTH_PEEK.toPx() }
    val visible = cards.take(VISIBLE_COUNT)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Draw back-to-front so the top card (depth 0) renders last / on top.
        visible.asReversed().forEach { idea ->
            key(idea.id) {
                val depth = visible.indexOf(idea)
                val scale by animateFloatAsState(
                    targetValue = 1f - depth * DEPTH_SCALE_STEP,
                    label = "cardScale",
                )
                val translateY by animateFloatAsState(
                    targetValue = depth * peekPx,
                    label = "cardTranslateY",
                )

                if (depth == 0) {
                    SwipeableCard(
                        onSwiped = { direction -> onSwiped(idea, direction) },
                        enabled = true,
                    ) {
                        cardContent(idea)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationY = translateY
                            },
                    ) {
                        cardContent(idea)
                    }
                }
            }
        }
    }
}

// --- Phase 3 checkpoint preview: a deck of 5 hardcoded cards -----------------

private val previewDeck: List<IdeaModel> = buildList {
    addAll(SampleIdeas.all)
    add(SampleIdeas.hooks.copy(id = "idea_4", title = "5 thumbnail styles that get clicks"))
    add(SampleIdeas.aiTweet.copy(id = "idea_5", title = "Why your CTA is killing reach"))
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 720)
@Composable
private fun CardStackPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val state = rememberCardStackState(previewDeck)
            CardStack(
                cards = state.cards,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(620.dp)
                    .padding(Spacing.lg),
                onSwiped = { _, direction -> state.dismiss(direction) },
            ) { idea ->
                IdeaCard(idea = idea, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
