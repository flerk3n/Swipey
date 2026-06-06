package com.harsh.swipey.ui.screens.echo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.swipey.data.model.IdeaModel
import com.harsh.swipey.data.model.SampleIdeas
import com.harsh.swipey.ui.components.AnimatedBackground
import com.harsh.swipey.ui.components.PrimaryButton
import com.harsh.swipey.ui.components.SecondaryButton
import com.harsh.swipey.ui.components.SwipeyTag
import com.harsh.swipey.ui.theme.AccentViolet
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyCardSurface
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyOutline
import com.harsh.swipey.ui.theme.SwipeyTheme
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Stateful entry point (ViewModel) — NOT used by previews.
// ---------------------------------------------------------------------------

@Composable
fun ExpandedEchoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EchoViewModel = viewModel(factory = EchoViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ExpandedEchoContent(
        idea = state.idea,
        displayedGenerated = state.displayedGenerated,
        isGenerating = state.isGenerating,
        tags = state.tags,
        onBack = onBack,
        onGenerate = viewModel::onGenerate,
        onTagToggled = viewModel::onTagToggled,
        onTagAdded = viewModel::onTagAdded,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Stateless presentation layer — used directly in previews.
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpandedEchoContent(
    idea: IdeaModel?,
    displayedGenerated: String,
    isGenerating: Boolean,
    tags: List<String>,
    onBack: () -> Unit,
    onGenerate: () -> Unit,
    onTagToggled: (String) -> Unit,
    onTagAdded: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showTagSheet by remember { mutableStateOf(false) }

    AnimatedBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
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
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.ScreenPadding),
            ) {
                if (idea == null) {
                    Text(
                        text = "Idea not found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SwipeyOnSurfaceMuted,
                    )
                    return@Column
                }

                // Category eyebrow
                Text(
                    text = idea.category.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentViolet,
                )
                Spacer(Modifier.height(Spacing.sm))

                // Idea title
                Text(
                    text = idea.title,
                    fontFamily = InstrumentSerif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = SwipeyOnSurface,
                )
                Spacer(Modifier.height(Spacing.xl))

                // Generated content canvas
                GenerationCanvas(
                    displayedText = displayedGenerated,
                    placeholder = idea.description,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.xl))

                // Tags
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.labelLarge,
                    color = SwipeyOnSurfaceMuted,
                )
                Spacer(Modifier.height(Spacing.sm))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    tags.forEach { tag ->
                        SwipeyTag(
                            text = tag,
                            selected = true,
                            onClick = { onTagToggled(tag) },
                        )
                    }
                    // Add tag chip
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SwipeyCardSurface)
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add tag",
                            tint = SwipeyOnSurfaceMuted,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Add tag",
                            style = MaterialTheme.typography.labelLarge,
                            color = SwipeyOnSurfaceMuted,
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.xxl))
            }

            // Bottom action bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenPadding, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                PrimaryButton(
                    text = if (isGenerating) "Generating…" else "Generate Content",
                    onClick = onGenerate,
                    enabled = !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                )
                SecondaryButton(
                    text = "Edit Tags",
                    onClick = { showTagSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Tag bottom sheet
        if (showTagSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTagSheet = false },
                sheetState = sheetState,
                containerColor = SwipeyCardSurface,
            ) {
                TagSheet(
                    tags = tags,
                    onToggle = onTagToggled,
                    onAdd = onTagAdded,
                    onDone = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showTagSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Generation canvas — shows typewriter output or a muted placeholder.
// ---------------------------------------------------------------------------

@Composable
private fun GenerationCanvas(
    displayedText: String,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Spacing.CardRadiusMedium)
    Box(
        modifier = modifier
            .clip(shape)
            .background(SwipeyCardSurface)
            .padding(Spacing.xl),
    ) {
        if (displayedText.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = SwipeyOnSurfaceMuted,
            )
        } else {
            Text(
                text = displayedText,
                style = MaterialTheme.typography.bodyLarge,
                color = SwipeyOnSurface,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Tag bottom sheet body
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSheet(
    tags: List<String>,
    onToggle: (String) -> Unit,
    onAdd: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var newTag by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(horizontal = Spacing.ScreenPadding)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleMedium,
            color = SwipeyOnSurface,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            tags.forEach { tag ->
                SwipeyTag(
                    text = tag,
                    selected = true,
                    onClick = { onToggle(tag) },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OutlinedTextField(
                value = newTag,
                onValueChange = { newTag = it },
                placeholder = {
                    Text(
                        text = "#NewTag",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SwipeyOnSurfaceMuted,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(Spacing.InputRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SwipeyLime,
                    unfocusedBorderColor = SwipeyOutline,
                    focusedTextColor = SwipeyOnSurface,
                    unfocusedTextColor = SwipeyOnSurface,
                    cursorColor = SwipeyLime,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onAdd(newTag)
                    newTag = ""
                }),
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    onAdd(newTag)
                    newTag = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SwipeyLime),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                )
            }
        }
        PrimaryButton(
            text = "Done",
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Echo — idle", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 860)
@Composable
private fun EchoIdlePreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExpandedEchoContent(
                idea = SampleIdeas.hooks,
                displayedGenerated = "",
                isGenerating = false,
                tags = SampleIdeas.hooks.tags,
                onBack = {},
                onGenerate = {},
                onTagToggled = {},
                onTagAdded = {},
            )
        }
    }
}

@Preview(name = "Echo — typewriter", showBackground = true, backgroundColor = 0xFF242B32, heightDp = 860)
@Composable
private fun EchoTypewriterPreview() {
    SwipeyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExpandedEchoContent(
                idea = SampleIdeas.hooks,
                displayedGenerated = "The secret to an unbreakable hook isn't just curiosity—it's establishing an immediate,",
                isGenerating = true,
                tags = SampleIdeas.hooks.tags,
                onBack = {},
                onGenerate = {},
                onTagToggled = {},
                onTagAdded = {},
            )
        }
    }
}
