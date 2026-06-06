package com.harsh.swipey.ui.screens.flashcard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.components.AnimatedBackground
import com.harsh.swipey.ui.components.CircularActionButton
import com.harsh.swipey.ui.components.IdeaCard
import com.harsh.swipey.ui.components.PrimaryButton
import com.harsh.swipey.ui.components.SaveToast
import com.harsh.swipey.ui.components.SwipeyIcons
import com.harsh.swipey.ui.theme.ApproveColor
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeReject
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyTheme
import kotlinx.coroutines.delay

/** Space reserved at the bottom so action buttons clear the floating nav pill. */
private val BottomReserved = 96.dp

// ---------------------------------------------------------------------------
// Stateful entry point (ViewModel + Store) — NOT used by previews.
// ---------------------------------------------------------------------------

@Composable
fun FlashcardScreen(
    modifier: Modifier = Modifier,
    onLike: (ideaId: String) -> Unit = {},
    viewModel: FlashcardViewModel = viewModel(factory = FlashcardViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val saved by SavedIdeasStore.saved.collectAsStateWithLifecycle()

    var toastVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.toastMessages.collect { message ->
            toastMessage = message
            toastVisible = true
            delay(2500)
            toastVisible = false
        }
    }

    FlashcardContent(
        uiState = uiState,
        streak = saved.size,
        toastVisible = toastVisible,
        toastMessage = toastMessage,
        onSwiped = { idea, direction ->
            viewModel.onSwiped(idea, direction)
            if (direction == com.harsh.swipey.ui.screens.flashcard.SwipeDirection.Right) {
                onLike(idea.id)
            }
        },
        onDismiss = viewModel::dismissTop,
        onSave = viewModel::saveTop,
        onLike = {
            viewModel.likeTop()
            (viewModel.uiState.value as? FlashcardUiState.Content)
                ?.cards?.firstOrNull()?.id?.let(onLike)
        },
        onReset = viewModel::reset,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Stateless presentation layer — used directly in previews.
// ---------------------------------------------------------------------------

@Composable
fun FlashcardContent(
    uiState: FlashcardUiState,
    streak: Int,
    toastVisible: Boolean,
    toastMessage: String,
    onSwiped: (IdeaModel, SwipeDirection) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onLike: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                FlashcardTopBar(streak = streak)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.ScreenPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    when (val state = uiState) {
                        FlashcardUiState.Loading -> LoadingSkeleton()
                        FlashcardUiState.Empty -> EmptyState(onReset = onReset)
                        is FlashcardUiState.Error -> ErrorState(
                            message = state.message,
                            onRetry = onReset,
                        )
                        is FlashcardUiState.Content -> CardStack(
                            cards = state.cards,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = Spacing.lg),
                            onSwiped = onSwiped,
                        ) { idea ->
                            IdeaCard(idea = idea, modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                ActionButtons(
                    enabled = uiState is FlashcardUiState.Content,
                    onDismiss = onDismiss,
                    onSave = onSave,
                    onLike = onLike,
                )
                Spacer(Modifier.height(BottomReserved))
            }

            SaveToast(
                visible = toastVisible,
                message = toastMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BottomReserved + Spacing.xl),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun FlashcardTopBar(streak: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.ScreenPadding, vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = SwipeyIcons.Flame,
                contentDescription = "Saved ideas",
                tint = SwipeyLime,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = streak.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = SwipeyOnSurface,
            )
        }
        Text(
            text = "Swipey",
            fontFamily = InstrumentSerif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = SwipeyOnSurface,
        )
    }
}

@Composable
private fun ActionButtons(
    enabled: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onLike: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularActionButton(
            icon = Icons.Filled.Close,
            contentDescription = "Dismiss",
            onClick = { if (enabled) onDismiss() },
            contentColor = SwipeReject,
            borderColor = SwipeReject,
        )
        Spacer(Modifier.size(Spacing.xl))
        CircularActionButton(
            icon = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Save",
            onClick = { if (enabled) onSave() },
            size = 48.dp,
            borderColor = SwipeyOnSurface,
        )
        Spacer(Modifier.size(Spacing.xl))
        CircularActionButton(
            icon = Icons.Filled.Favorite,
            contentDescription = "Like",
            onClick = { if (enabled) onLike() },
            containerColor = ApproveColor,
            contentColor = SwipeyOnLime,
        )
    }
}

@Composable
private fun LoadingSkeleton() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Spacing.lg)
            .clip(RoundedCornerShape(Spacing.CardRadiusLarge))
            .alpha(pulse)
            .background(SwipeyCardSurface),
    )
}

@Composable
private fun EmptyState(onReset: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "ALL CAUGHT UP",
            style = MaterialTheme.typography.headlineLarge,
            color = SwipeyOnSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "You've swiped through every idea for now. Check your saved ideas, or " +
                "start the deck over.",
            style = MaterialTheme.typography.bodyLarge,
            color = SwipeyOnSurfaceMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(text = "Start over", onClick = onReset)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "CAN'T LOAD IDEAS",
            style = MaterialTheme.typography.headlineLarge,
            color = SwipeyOnSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SwipeyOnSurfaceMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(text = "Retry", onClick = onRetry)
    }
}

// ---------------------------------------------------------------------------
// Previews — all use FlashcardContent directly (no ViewModel required).
// ---------------------------------------------------------------------------

@Preview(name = "Content state", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun FlashcardContentPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashcardContent(
                uiState = FlashcardUiState.Content(SeedIdeas.deck),
                streak = 3,
                toastVisible = false,
                toastMessage = "",
                onSwiped = { _, _ -> },
                onDismiss = {},
                onSave = {},
                onLike = {},
                onReset = {},
            )
        }
    }
}

@Preview(name = "Loading state", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun FlashcardLoadingPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashcardContent(
                uiState = FlashcardUiState.Loading,
                streak = 0,
                toastVisible = false,
                toastMessage = "",
                onSwiped = { _, _ -> },
                onDismiss = {},
                onSave = {},
                onLike = {},
                onReset = {},
            )
        }
    }
}

@Preview(name = "Empty state", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun FlashcardEmptyPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashcardContent(
                uiState = FlashcardUiState.Empty,
                streak = 5,
                toastVisible = false,
                toastMessage = "",
                onSwiped = { _, _ -> },
                onDismiss = {},
                onSave = {},
                onLike = {},
                onReset = {},
            )
        }
    }
}

@Preview(name = "Toast visible", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun FlashcardToastPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashcardContent(
                uiState = FlashcardUiState.Content(SeedIdeas.deck),
                streak = 4,
                toastVisible = true,
                toastMessage = "Saved to your ideas",
                onSwiped = { _, _ -> },
                onDismiss = {},
                onSave = {},
                onLike = {},
                onReset = {},
            )
        }
    }
}

@Preview(name = "Error state", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun FlashcardErrorPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashcardContent(
                uiState = FlashcardUiState.Error(
                    "Couldn't reach the server. Check that the backend is running.",
                ),
                streak = 0,
                toastVisible = false,
                toastMessage = "",
                onSwiped = { _, _ -> },
                onDismiss = {},
                onSave = {},
                onLike = {},
                onReset = {},
            )
        }
    }
}
