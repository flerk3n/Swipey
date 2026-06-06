package com.harsh.swipey.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeySurface
import com.harsh.swipey.ui.theme.SwipeyTheme

/** The three persistent destinations behind the bottom nav pill. */
enum class SwipeyTab(val label: String) {
    Dashboard("Dashboard"),
    Flashcard("Swipe"),
    Profile("Profile");

    /**
     * Resolved lazily (not in the enum's static initializer). Uses our own
     * [SwipeyIcons] vectors for the grid/cards glyphs so previews render — the
     * `material-icons-extended` classes are not on the Layoutlib classpath.
     */
    val icon: ImageVector
        get() = when (this) {
            Dashboard -> SwipeyIcons.Grid
            Flashcard -> SwipeyIcons.Cards
            Profile -> Icons.Filled.Person
        }
}

/**
 * Floating, pill-shaped bottom navigation across Dashboard / Flashcard / Profile.
 * Stateless: current selection in, selection events out.
 */
@Composable
fun BottomNavPill(
    selected: SwipeyTab,
    onSelect: (SwipeyTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Spacing.ButtonRadius),
        color = SwipeySurface,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            SwipeyTab.entries.forEach { tab ->
                val tint by animateColorAsState(
                    targetValue = if (tab == selected) SwipeyLime else SwipeyOnSurfaceMuted,
                    label = "navTint",
                )
                IconButton(onClick = { onSelect(tab) }) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = tint,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun BottomNavPillPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.padding(Spacing.xl)) {
                BottomNavPill(
                    selected = SwipeyTab.Flashcard,
                    onSelect = {},
                    modifier = Modifier
                        .clip(RoundedCornerShape(Spacing.ButtonRadius)),
                )
            }
        }
    }
}
