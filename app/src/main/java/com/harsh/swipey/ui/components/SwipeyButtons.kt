package com.harsh.swipey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeyTheme

private val pillShape = RoundedCornerShape(Spacing.ButtonRadius)

/**
 * Primary CTA — solid lime fill, black text, full-width pill. (Design.md §3 + §5)
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(Spacing.CtaHeight),
        enabled = enabled,
        shape = pillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SwipeyLime,
            contentColor = SwipeyOnLime,
            disabledContainerColor = SwipeyLime.copy(alpha = 0.35f),
            disabledContentColor = SwipeyOnLime.copy(alpha = 0.5f),
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Secondary action — transparent fill, outline border, white text. Full-width pill.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(Spacing.CtaHeight),
        enabled = enabled,
        shape = pillShape,
        border = BorderStroke(Spacing.BorderWidth, SwipeyOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = SwipeyOnSurface,
            disabledContentColor = SwipeyOnSurface.copy(alpha = 0.4f),
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Ghost / text action — no fill, no border, lime text. Wraps content.
 */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = pillShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = SwipeyLime,
            disabledContentColor = SwipeyLime.copy(alpha = 0.4f),
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun SwipeyButtonsPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .padding(Spacing.lg)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                PrimaryButton(text = "Keep Swiping", onClick = {}, modifier = Modifier.fillMaxWidth())
                SecondaryButton(text = "Maybe later", onClick = {}, modifier = Modifier.fillMaxWidth())
                GhostButton(text = "View All", onClick = {})
                PrimaryButton(text = "Disabled", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
