package com.harsh.swipey.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.components.BottomNavPill
import com.harsh.swipey.ui.components.SwipeyTab
import com.harsh.swipey.ui.screens.dashboard.DashboardContent
import com.harsh.swipey.ui.screens.dashboard.DashboardScreen
import com.harsh.swipey.ui.screens.dashboard.DashboardUiState
import com.harsh.swipey.ui.screens.flashcard.FlashcardScreen
import com.harsh.swipey.ui.screens.profile.ProfileScreen
import com.harsh.swipey.ui.theme.LocalReducedMotion
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Main app shell: persistent [BottomNavPill] + crossfaded tab switcher.
 * [onOpenEcho] navigates to the Expanded Echo screen; [onOpenSettings] to Settings.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onOpenEcho: (ideaId: String) -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    var tab by remember { mutableStateOf(SwipeyTab.Flashcard) }
    val reducedMotion = LocalReducedMotion.current

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = tab,
            animationSpec = if (reducedMotion) snap() else tween(250),
            label = "tabSwitch",
        ) { current ->
            when (current) {
                SwipeyTab.Flashcard -> FlashcardScreen(
                    modifier = Modifier.fillMaxSize(),
                    onLike = { ideaId -> onOpenEcho(ideaId) },
                )
                SwipeyTab.Dashboard -> DashboardScreen(
                    modifier = Modifier.fillMaxSize(),
                    onKeepSwiping = { tab = SwipeyTab.Flashcard },
                    onIdeaClick = { idea -> onOpenEcho(idea.id) },
                )
                SwipeyTab.Profile -> ProfileScreen(
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        BottomNavPill(
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl),
        )
    }
}

// ---------------------------------------------------------------------------
// Preview — uses static Content composables, no ViewModel or Store.
// ---------------------------------------------------------------------------

@Preview(name = "Main shell", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 860)
@Composable
private fun MainShellPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            var tab by remember { mutableStateOf(SwipeyTab.Dashboard) }
            Box(modifier = Modifier.fillMaxSize()) {
                DashboardContent(
                    state = DashboardUiState(
                        greeting = "Good morning,",
                        userName = "Editor",
                        savedIdeas = SeedIdeas.deck.take(3),
                        streak = 3,
                    ),
                    onKeepSwiping = {},
                    onIdeaClick = {},
                    modifier = Modifier.fillMaxSize(),
                )
                BottomNavPill(
                    selected = tab,
                    onSelect = { tab = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Spacing.xl),
                )
            }
        }
    }
}
