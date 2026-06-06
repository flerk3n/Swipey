package com.harsh.swipey.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.theme.CardFillSelected
import com.harsh.swipey.ui.theme.CardFillUnselected
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Glassmorphic selection row used in onboarding (niche list, goal cards).
 *
 * Selected: 8% lime fill, 1dp white border, 4dp left-edge lime accent (Design.md §3).
 * Unselected: 5% white fill, 1dp outline border.
 */
@Composable
fun SelectionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: String? = null,
) {
    val shape = RoundedCornerShape(Spacing.CardRadiusMedium)
    val fill by animateColorAsState(
        targetValue = if (selected) CardFillSelected else CardFillUnselected,
        label = "fill",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) SwipeyOnSurface else SwipeyOutline,
        label = "border",
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(BorderStroke(Spacing.BorderWidth, borderColor), shape)
            .clickable(onClick = onClick),
    ) {
        // Left-edge accent on selection.
        if (selected) {
            Box(
                modifier = Modifier
                    .width(Spacing.CardAccentWidth)
                    .fillMaxHeight()
                    .background(SwipeyLime),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (leading != null) {
                Text(text = leading, style = MaterialTheme.typography.titleMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = SwipeyOnSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SwipeyOnSurfaceMuted,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun SelectionCardPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                SelectionCard(
                    title = "Tech & Software",
                    subtitle = "AI, coding, startups",
                    leading = "💻",
                    selected = true,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                SelectionCard(
                    title = "Personal Finance",
                    subtitle = "Investing, budgeting",
                    leading = "💰",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
