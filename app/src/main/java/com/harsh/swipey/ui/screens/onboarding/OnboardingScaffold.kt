package com.harsh.swipey.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harsh.swipey.ui.components.AnimatedBackground
import com.harsh.swipey.ui.components.PrimaryButton
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnSurface
import com.harsh.swipey.ui.theme.SwipeyOnSurfaceMuted
import com.harsh.swipey.ui.theme.SwipeyOutline

/**
 * Shared chrome for the three onboarding steps: a back button, a segmented progress
 * indicator, a serif title + subtitle, a scrollable [content] body, and a Continue CTA.
 *
 * The selection bodies differ per step (list / chips / cards), so they are passed in.
 */
@Composable
fun OnboardingScaffold(
    step: Int,
    totalSteps: Int,
    title: String,
    subtitle: String,
    continueEnabled: Boolean,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    continueLabel: String = "Continue",
    content: @Composable () -> Unit,
) {
    AnimatedBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.ScreenPadding),
        ) {
            // Top row: back + progress segments.
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SwipeyOnSurface,
                    )
                }
                Spacer(Modifier.width(Spacing.sm))
                StepProgress(
                    step = step,
                    totalSteps = totalSteps,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = SwipeyOnSurface,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = SwipeyOnSurfaceMuted,
            )
            Spacer(Modifier.height(Spacing.xl))

            // Body fills remaining space (each step supplies its own scrollable content).
            Box(modifier = Modifier.weight(1f)) { content() }

            Spacer(Modifier.height(Spacing.md))
            PrimaryButton(
                text = continueLabel,
                onClick = onContinue,
                enabled = continueEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun StepProgress(
    step: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        for (i in 1..totalSteps) {
            val active = i <= step
            val color by animateColorAsState(
                targetValue = if (active) SwipeyLime else SwipeyOutline,
                label = "segment",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(Spacing.ButtonRadius))
                    .background(color),
            )
        }
    }
}
