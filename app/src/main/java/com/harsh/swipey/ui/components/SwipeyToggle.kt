package com.harsh.swipey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeySurface
import com.harsh.swipey.ui.theme.SwipeyTheme

/**
 * Lime-themed switch used in Settings rows. Stateless — checked in, change events out.
 */
@Composable
fun SwipeyToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = SwipeyOnLime,
            checkedTrackColor = SwipeyLime,
            checkedBorderColor = SwipeyLime,
            uncheckedThumbColor = SwipeyOnSurfaceMuted,
            uncheckedTrackColor = SwipeySurface,
            uncheckedBorderColor = SwipeyOutline,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32)
@Composable
private fun SwipeyTogglePreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                SwipeyToggle(checked = true, onCheckedChange = {})
                SwipeyToggle(checked = false, onCheckedChange = {})
                SwipeyToggle(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }
}
