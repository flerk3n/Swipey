package com.harsh.swipey.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.harsh.swipey.ui.components.PrimaryButton
import com.harsh.swipey.ui.components.SwipeyToggle
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyTheme
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Stateful entry point
// ---------------------------------------------------------------------------

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        onBack = onBack,
        onPush = viewModel::setPushNotifications,
        onReminder = viewModel::setDailyReminder,
        onReducedMotion = viewModel::setReducedMotion,
        onNotion = viewModel::setNotionConnected,
        onCalendar = viewModel::setCalendarConnected,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Stateless presentation
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onPush: (Boolean) -> Unit,
    onReminder: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onNotion: (Boolean) -> Unit,
    onCalendar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showNotionSheet by remember { mutableStateOf(false) }

    AnimatedBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SwipeyOnSurface,
                    )
                }
                Text(
                    text = "Settings",
                    fontFamily = InstrumentSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = SwipeyOnSurface,
                )
            }
            Spacer(Modifier.height(Spacing.lg))

            SettingsSection(title = "Notifications") {
                ToggleRow(
                    title = "Push notifications",
                    subtitle = "Idea drops and reminders",
                    checked = state.pushNotifications,
                    onToggle = onPush,
                )
                ToggleRow(
                    title = "Daily reminder",
                    subtitle = "A nudge to hit your daily target",
                    checked = state.dailyReminder,
                    onToggle = onReminder,
                )
            }

            SettingsSection(title = "Integrations") {
                IntegrationRow(
                    title = "Notion",
                    connected = state.notionConnected,
                    onToggle = onNotion,
                    onManage = { showNotionSheet = true },
                )
                IntegrationRow(
                    title = "Google Calendar",
                    connected = state.calendarConnected,
                    onToggle = onCalendar,
                    onManage = null,
                )
            }

            SettingsSection(title = "Preferences") {
                ToggleRow(
                    title = "Reduced motion",
                    subtitle = "Minimise animations across the app",
                    checked = state.reducedMotion,
                    onToggle = onReducedMotion,
                )
            }

            SettingsSection(title = "About") {
                StaticRow(title = "Version", value = "1.0")
            }

            Spacer(Modifier.height(Spacing.xxl))
        }

        if (showNotionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showNotionSheet = false },
                sheetState = sheetState,
                containerColor = SwipeyCardSurface,
            ) {
                NotionSheet(
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showNotionSheet = false
                        }
                    },
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Spacing.ScreenPadding)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = SwipeyOnSurfaceMuted,
            modifier = Modifier.padding(start = Spacing.sm, bottom = Spacing.sm),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Spacing.CardRadiusMedium))
                .background(SwipeyCardSurface),
        ) {
            content()
        }
        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        SwipeyToggle(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun IntegrationRow(
    title: String,
    connected: Boolean,
    onToggle: (Boolean) -> Unit,
    onManage: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onManage != null) it.clickable(onClick = onManage) else it }
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = SwipeyOnSurface,
            )
            Text(
                text = if (connected) "Connected" else "Not connected",
                style = MaterialTheme.typography.bodyMedium,
                color = if (connected) SwipeyLime else SwipeyOnSurfaceMuted,
            )
        }
        SwipeyToggle(checked = connected, onCheckedChange = onToggle)
    }
}

@Composable
private fun StaticRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = SwipeyOnSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = SwipeyOnSurfaceMuted,
        )
    }
}

@Composable
private fun NotionSheet(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.ScreenPadding)
            .padding(bottom = Spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = "Connect Notion",
            style = MaterialTheme.typography.titleMedium,
            color = SwipeyOnSurface,
        )
        Text(
            text = "Sync your saved ideas straight into a Notion database. Full OAuth " +
                "connection arrives with the backend — for now this is a placeholder.",
            style = MaterialTheme.typography.bodyLarge,
            color = SwipeyOnSurfaceMuted,
        )
        PrimaryButton(
            text = "Got it",
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Settings", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 900)
@Composable
private fun SettingsPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsContent(
                state = SettingsUiState(notionConnected = true),
                onBack = {},
                onPush = {},
                onReminder = {},
                onReducedMotion = {},
                onNotion = {},
                onCalendar = {},
            )
        }
    }
}
