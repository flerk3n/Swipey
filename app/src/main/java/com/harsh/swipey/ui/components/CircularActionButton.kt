package com.harsh.swipey.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.theme.ApproveColor
import com.harsh.swipey.ui.theme.DismissColor
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeySurface
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Round icon button used for the Flashcard actions (✕ dismiss, ↑ save, ♡ like).
 * Mirrors the swipe gestures. Fully stateless.
 *
 * @param borderColor pass [Color.Unspecified] for no border (filled variants).
 */
@Composable
fun CircularActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = Spacing.ActionButtonSize,
    containerColor: Color = SwipeySurface,
    contentColor: Color = SwipeyOnSurface,
    borderColor: Color = Color.Unspecified,
) {
    val base = Modifier
        .size(size)
        .background(containerColor, CircleShape)
    val withBorder =
        if (borderColor != Color.Unspecified) {
            base.border(BorderStroke(Spacing.BorderWidth, borderColor), CircleShape)
        } else {
            base
        }

    IconButton(
        onClick = onClick,
        modifier = modifier.then(withBorder),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(size * 0.42f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun CircularActionButtonPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(Spacing.xl),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                // Dismiss — outlined
                CircularActionButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    onClick = {},
                    contentColor = DismissColor,
                    borderColor = DismissColor,
                )
                // Save — smaller, outlined
                CircularActionButton(
                    icon = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Save",
                    onClick = {},
                    size = 48.dp,
                    borderColor = SwipeyOnSurface,
                )
                // Like — filled lime
                CircularActionButton(
                    icon = Icons.Filled.Favorite,
                    contentDescription = "Like",
                    onClick = {},
                    containerColor = ApproveColor,
                    contentColor = SwipeyOnLime,
                )
            }
        }
    }
}
