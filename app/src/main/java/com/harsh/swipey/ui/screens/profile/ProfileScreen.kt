package com.harsh.swipey.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.swipey.ui.components.AnimatedBackground
import com.harsh.swipey.ui.components.SwipeyTag
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyTheme

private const val PROFILE_NAME = "Editor"

data class ProfileUiState(
    val name: String = PROFILE_NAME,
    val nicheName: String?,
    val goalTitle: String?,
    val savedCount: Int,
)

// ---------------------------------------------------------------------------
// Stateful entry point
// ---------------------------------------------------------------------------

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileContent(state = state, onOpenSettings = onOpenSettings, modifier = modifier)
}

// ---------------------------------------------------------------------------
// Stateless presentation
// ---------------------------------------------------------------------------

@Composable
fun ProfileContent(
    state: ProfileUiState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = Spacing.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.xxl))

            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SwipeyLime),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = SwipeyOnLime,
                    modifier = Modifier.size(52.dp),
                )
            }
            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = state.name,
                fontFamily = InstrumentSerif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = SwipeyOnSurface,
            )
            if (state.nicheName != null) {
                Spacer(Modifier.height(Spacing.sm))
                SwipeyTag(text = state.nicheName)
            }

            Spacer(Modifier.height(Spacing.xl))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                StatCard(
                    value = state.savedCount.toString(),
                    label = "Saved ideas",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = state.goalTitle ?: "—",
                    label = "Daily goal",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.xl))

            // Settings entry
            NavRow(
                icon = Icons.Filled.Settings,
                title = "Settings",
                onClick = onOpenSettings,
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.CardRadiusMedium))
            .background(SwipeyCardSurface)
            .padding(Spacing.lg),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = SwipeyLime,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SwipeyOnSurfaceMuted,
        )
    }
}

@Composable
private fun NavRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.CardRadiusMedium))
            .background(SwipeyCardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SwipeyOnSurface)
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SwipeyOnSurface,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = SwipeyOnSurfaceMuted,
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Profile", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun ProfilePreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileContent(
                state = ProfileUiState(
                    nicheName = "Tech & Software",
                    goalTitle = "Consistent",
                    savedCount = 7,
                ),
                onOpenSettings = {},
            )
        }
    }
}
