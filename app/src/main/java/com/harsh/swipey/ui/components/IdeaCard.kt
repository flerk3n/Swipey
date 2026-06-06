package com.harsh.swipey.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.model.SampleIdeas
import com.harsh.swipey.ui.theme.AccentViolet
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Full-size idea card — the centerpiece of the Flashcard swipe stack.
 * Clean, solid, elegant surface (no glassmorphism). Stateless: size and gesture handling
 * are supplied by the caller via [modifier].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IdeaCard(
    idea: IdeaModel,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Spacing.CardRadiusLarge),
        color = SwipeyCardSurface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Text(
                text = idea.category.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = AccentViolet,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = idea.title,
                style = MaterialTheme.typography.headlineLarge,
                color = SwipeyOnSurface,
            )
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodyLarge,
                color = SwipeyOnSurfaceMuted,
            )
            Spacer(Modifier.weight(1f))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                idea.tags.forEach { tag -> SwipeyTag(text = tag) }
            }
        }
    }
}

/**
 * Compact idea row — used in saved-idea lists (Dashboard). Solid surface, title with a
 * trailing lime action affordance.
 */
@Composable
fun IdeaCardCompact(
    idea: IdeaModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Spacing.CardRadiusMedium),
        color = SwipeyCardSurface,
        shadowElevation = 1.dp,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = idea.title,
                style = MaterialTheme.typography.bodyLarge,
                color = SwipeyOnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(Spacing.md))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SwipeyLime),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Open idea",
                    tint = SwipeyOnLime,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 640)
@Composable
private fun IdeaCardPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IdeaCard(
                idea = SampleIdeas.hooks,
                modifier = Modifier
                    .padding(Spacing.lg)
                    .fillMaxWidth()
                    .height(560.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun IdeaCardCompactPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                SampleIdeas.all.forEach { idea ->
                    IdeaCardCompact(idea = idea, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
