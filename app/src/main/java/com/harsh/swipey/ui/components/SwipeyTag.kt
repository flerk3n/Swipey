package com.harsh.swipey.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.theme.CardFillSelected
import com.harsh.swipey.ui.theme.CardFillUnselected
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Hashtag-style pill used on idea cards and in the tag bottom sheet.
 *
 * @param text full label including any leading "#", e.g. "#Strategy".
 * @param selected toggled state — brighter fill + white border.
 * @param onClick optional; when null the tag is non-interactive (display only).
 */
@Composable
fun SwipeyTag(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(Spacing.ButtonRadius)
    val fill = if (selected) CardFillSelected else CardFillUnselected
    val borderColor = if (selected) SwipeyOnSurface else SwipeyOutline
    val contentColor = SwipeyLime

    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(BorderStroke(Spacing.BorderWidth, borderColor), shape)
            .then(clickModifier)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun SwipeyTagPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                SwipeyTag(text = "#Strategy")
                SwipeyTag(text = "#Writing", onClick = {})
                SwipeyTag(text = "#Selected", selected = true, onClick = {})
            }
        }
    }
}
