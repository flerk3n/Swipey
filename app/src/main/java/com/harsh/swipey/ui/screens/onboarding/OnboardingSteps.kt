package com.harsh.swipey.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.harsh.swipey.data.model.Goal
import com.harsh.swipey.data.model.Niche
import com.harsh.swipey.data.model.OnboardingData
import com.harsh.swipey.ui.components.SelectionCard
import com.harsh.swipey.ui.components.SwipeyTag
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyTheme

private const val TOTAL_STEPS = 3

/** Step 1 — pick one broad niche (list-based). */
@Composable
fun NicheStepScreen(
    selectedNicheId: String?,
    onNicheSelected: (String) -> Unit,
    canContinue: Boolean,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        step = 1,
        totalSteps = TOTAL_STEPS,
        title = "What do you create?",
        subtitle = "Pick the niche that fits you best.",
        continueEnabled = canContinue,
        onContinue = onContinue,
        onBack = onBack,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            items(OnboardingData.niches, key = { it.id }) { niche ->
                SelectionCard(
                    title = niche.name,
                    subtitle = niche.topics.take(3).joinToString(" · "),
                    leading = niche.emoji,
                    selected = niche.id == selectedNicheId,
                    onClick = { onNicheSelected(niche.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Step 2 — pick sub-topics within the chosen niche (chip cloud, multi-select). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicStepScreen(
    niche: Niche?,
    selectedTopics: Set<String>,
    onTopicToggled: (String) -> Unit,
    canContinue: Boolean,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        step = 2,
        totalSteps = TOTAL_STEPS,
        title = "Narrow it down",
        subtitle = "Choose the topics you want ideas for.",
        continueEnabled = canContinue,
        onContinue = onContinue,
        onBack = onBack,
        modifier = modifier,
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            niche?.topics?.forEach { topic ->
                SwipeyTag(
                    text = topic,
                    selected = topic in selectedTopics,
                    onClick = { onTopicToggled(topic) },
                )
            }
        }
    }
}

/** Step 3 — pick a posting-volume goal (high-intent card selection). */
@Composable
fun GoalStepScreen(
    selectedGoalId: String?,
    onGoalSelected: (String) -> Unit,
    canContinue: Boolean,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        step = 3,
        totalSteps = TOTAL_STEPS,
        title = "Set your pace",
        subtitle = "How many ideas do you want to commit to?",
        continueEnabled = canContinue,
        onContinue = onFinish,
        onBack = onBack,
        continueLabel = "Start swiping",
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            items(OnboardingData.goals, key = { it.id }) { goal: Goal ->
                SelectionCard(
                    title = goal.title,
                    subtitle = goal.subtitle,
                    selected = goal.id == selectedGoalId,
                    onClick = { onGoalSelected(goal.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun NicheStepPreview() {
    SwipeyTheme {
        NicheStepScreen(
            selectedNicheId = "tech",
            onNicheSelected = {},
            canContinue = true,
            onContinue = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun TopicStepPreview() {
    SwipeyTheme {
        TopicStepScreen(
            niche = OnboardingData.niches.first(),
            selectedTopics = setOf("AI & Machine Learning", "Startups & SaaS"),
            onTopicToggled = {},
            canContinue = true,
            onContinue = {},
            onBack = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF242B32, heightDp = 820)
@Composable
private fun GoalStepPreview() {
    SwipeyTheme {
        GoalStepScreen(
            selectedGoalId = "consistent",
            onGoalSelected = {},
            canContinue = true,
            onFinish = {},
            onBack = {},
        )
    }
}
