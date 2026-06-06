package com.harsh.swipey.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.components.GhostButton
import com.harsh.swipey.ui.components.PrimaryButton
import com.harsh.swipey.ui.components.SwipeyIcons
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyBackground
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeyTheme
import com.harsh.swipey.ui.theme.buildMixedHeadline
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Dashboard screen — wired to [DashboardViewModel] (reads the shared saved-ideas store).
 * Stateless variant [DashboardContent] is used for previews.
 */
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onKeepSwiping: () -> Unit = {},
    onIdeaClick: (IdeaModel) -> Unit = {},
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        state = state,
        onKeepSwiping = onKeepSwiping,
        onIdeaClick = onIdeaClick,
        modifier = modifier,
    )
}

@Composable
fun DashboardContent(
    state: DashboardUiState,
    onKeepSwiping: () -> Unit,
    onIdeaClick: (IdeaModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val savedIdeas = state.savedIdeas
    val progress = state.progress

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SwipeyBackground),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 120.dp),
        ) {
            // Top bar
            item { DashboardTopBar(streak = state.streak) }

            // Greeting
            item {
                Text(
                    text = buildMixedHeadline(state.greeting, state.userName),
                    style = MaterialTheme.typography.headlineLarge,
                    color = SwipeyOnSurface,
                    modifier = Modifier.padding(
                        horizontal = Spacing.ScreenPadding,
                        vertical = Spacing.lg,
                    ),
                )
            }

            // Goal card
            item {
                GoalCard(
                    saved = state.savedCount,
                    target = state.dailyTarget,
                    progress = progress,
                    onKeepSwiping = onKeepSwiping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.ScreenPadding)
                        .padding(bottom = Spacing.xl),
                )
            }

            // Loved Ideas header
            item {
                Text(
                    text = "Loved Ideas",
                    fontFamily = InstrumentSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    color = SwipeyLime,
                    modifier = Modifier.padding(
                        horizontal = Spacing.ScreenPadding,
                        vertical = Spacing.sm,
                    ),
                )
            }

            // Loved Ideas list
            if (savedIdeas.isEmpty()) {
                item {
                    Text(
                        text = "No saved ideas yet — swipe ♡ on the card screen to add some.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SwipeyOnSurfaceMuted,
                        modifier = Modifier.padding(
                            horizontal = Spacing.ScreenPadding,
                            vertical = Spacing.md,
                        ),
                    )
                }
            } else {
                items(savedIdeas.take(5), key = { it.id }) { idea ->
                    LovedIdeaRow(
                        idea = idea,
                        onClick = { onIdeaClick(idea) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.ScreenPadding, vertical = 6.dp),
                    )
                }
            }

            // Trends header
            item {
                Spacer(Modifier.height(Spacing.xl))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.ScreenPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Trends",
                        style = MaterialTheme.typography.titleMedium,
                        color = SwipeyOnSurface,
                    )
                    GhostButton(text = "View All", onClick = {})
                }
            }

            // Trends horizontal scroll
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Spacing.ScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.padding(vertical = Spacing.md),
                ) {
                    items(TrendItems, key = { it.label }) { trend ->
                        TrendCard(trend = trend, modifier = Modifier.size(width = 200.dp, height = 280.dp))
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun DashboardTopBar(streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.ScreenPadding, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = SwipeyIcons.Flame,
                contentDescription = "Streak",
                tint = SwipeyLime,
                modifier = Modifier.size(26.dp),
            )
            if (streak > 0) {
                Text(
                    text = streak.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = SwipeyLime,
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = "Swipey",
                fontFamily = InstrumentSerif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = SwipeyLime,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SwipeyCardSurface)
                .border(BorderStroke(1.dp, SwipeyOutline), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = SwipeyOnSurface,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun GoalCard(
    saved: Int,
    target: Int,
    progress: Float,
    onKeepSwiping: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(modifier = modifier) {
        // Lime glow behind the card.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        listOf(SwipeyLime.copy(alpha = 0.15f), Color.Transparent),
                    ),
                    shape = shape,
                ),
        )
        Column(
            modifier = Modifier
                .clip(shape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), shape)
                .padding(Spacing.xl),
        ) {
            // Progress bar row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(SwipeyLime),
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = SwipeyLime,
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "DAILY TARGET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SwipeyOnSurfaceMuted,
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$saved",
                            fontFamily = InstrumentSerif,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwipeyLime,
                            lineHeight = 48.sp,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "/ $target",
                            style = MaterialTheme.typography.titleMedium,
                            color = SwipeyOnSurfaceMuted,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
                PrimaryButton(
                    text = "Keep Swiping",
                    onClick = onKeepSwiping,
                )
            }
        }
    }
}

@Composable
private fun LovedIdeaRow(
    idea: IdeaModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = idea.title,
            style = MaterialTheme.typography.titleSmall,
            color = SwipeyOnSurface,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Spacing.md))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SwipeyLime),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Open",
                tint = SwipeyOnLime,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Trends data & card
// ---------------------------------------------------------------------------

private data class TrendItem(val label: String, val tag: String, val accent: Color)

private val TrendItems = listOf(
    TrendItem("Digital Abstracts", "Trending", SwipeyLime),
    TrendItem("Neon Streets", "Style", Color(0xFF00E5FF)),
    TrendItem("Lo-fi Aesthetic", "Popular", Color(0xFFD3BBFF)),
    TrendItem("Retro Gradients", "Rising", SwipeyLime),
)

@Composable
private fun TrendCard(trend: TrendItem, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(SwipeyCardSurface)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), shape),
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, SwipeyBackground.copy(alpha = 0.8f)),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Bookmark chip top-right
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SwipeyBackground.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = SwipeyIcons.Flame,
                        contentDescription = null,
                        tint = SwipeyLime,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Column {
                Text(
                    text = trend.tag.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = trend.accent,
                )
                Text(
                    text = trend.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = SwipeyOnSurface,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Dashboard — with ideas", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 900)
@Composable
private fun DashboardWithIdeasPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = DashboardUiState(
                    greeting = "Good morning,",
                    userName = "Editor",
                    savedIdeas = SeedIdeas.deck.take(3),
                    streak = 3,
                ),
                onKeepSwiping = {},
                onIdeaClick = {},
            )
        }
    }
}

@Preview(name = "Dashboard — empty", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 900)
@Composable
private fun DashboardEmptyPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = DashboardUiState(
                    greeting = "Good evening,",
                    userName = "Editor",
                    savedIdeas = emptyList(),
                    streak = 0,
                ),
                onKeepSwiping = {},
                onIdeaClick = {},
            )
        }
    }
}

@Preview(
    name = "Dashboard — 130% font",
    showBackground = true,
    backgroundColor = 0xFF242B32,
    heightDp = 1000,
    fontScale = 1.3f,
)
@Composable
private fun DashboardLargeFontPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(
                state = DashboardUiState(
                    greeting = "Good morning,",
                    userName = "Editor",
                    savedIdeas = SeedIdeas.deck.take(3),
                    streak = 3,
                ),
                onKeepSwiping = {},
                onIdeaClick = {},
            )
        }
    }
}
