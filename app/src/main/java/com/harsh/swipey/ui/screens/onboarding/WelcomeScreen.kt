package com.harsh.swipey.ui.screens.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.swipey.ui.components.SwipeyIcons
import com.harsh.swipey.ui.theme.InstrumentSerif
import com.harsh.swipey.ui.theme.Spacing
import com.harsh.swipey.ui.theme.SwipeyBlack
import com.harsh.swipey.ui.theme.SwipeyLime
import com.harsh.swipey.ui.theme.SwipeyOnLime
import com.harsh.swipey.ui.theme.SwipeyTheme
import com.harsh.swipey.ui.theme.SwipeyViolet

/**
 * First-run brand splash. Visual-heavy: black canvas, violet glow blobs, floating tilted
 * idea cards, an oversized faint "Swipey", a serif headline with an inline pill, and a lime
 * CTA pill with a black circular arrow. Stateless — navigation hoisted via lambdas.
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit = onGetStarted,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SwipeyBlack),
    ) {
        WelcomeBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.ScreenPadding, vertical = Spacing.xl),
        ) {
            WelcomeHeader(onSignIn = onSignIn)

            // Floating cards zone — clustered around the vertical center.
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                GlassIdeaCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 60.dp, y = (-96).dp)
                        .width(212.dp)
                        .graphicsLayer { rotationZ = 4f },
                )
                LimeIdeaCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-36).dp, y = 84.dp)
                        .width(248.dp)
                        .graphicsLayer { rotationZ = -6f },
                )
                CreateNowBadge(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-104).dp, y = (-44).dp),
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            HeadlineBlock()
            Spacer(Modifier.height(Spacing.xl))
            GetStartedButton(onClick = onGetStarted)
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

/** Soft violet glow blobs on black (radial gradients, no blur — works on all API levels). */
@Composable
private fun WelcomeBlobs() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(520.dp)
                .offset(x = (-160).dp, y = (-140).dp)
                .background(
                    Brush.radialGradient(
                        listOf(SwipeyViolet.copy(alpha = 0.55f), Color.Transparent),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(460.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-120).dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SwipeyViolet.copy(alpha = 0.40f), Color.Transparent),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopEnd)
                .offset(x = 120.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(SwipeyViolet.copy(alpha = 0.25f), Color.Transparent),
                    ),
                ),
        )
    }
}

@Composable
private fun WelcomeHeader(onSignIn: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Swipey",
                fontFamily = InstrumentSerif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White,
            )
            Text(
                text = "SWIPE. SAVE. CREATE.",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onSignIn)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Sign in",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun GlassIdeaCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.20f)), shape)
            .padding(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SwipeyIcons.Bolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "Viral Hook Idea",
            fontFamily = InstrumentSerif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White,
        )
        Text(
            text = "TWITTER THREAD",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = Color.White.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(Spacing.md))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "70% Ready",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun LimeIdeaCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SwipeyLime)
            .padding(Spacing.lg),
    ) {
        Text(
            text = "Focus Block — Campaign Launch",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = SwipeyBlack,
        )
        Spacer(Modifier.height(Spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            MiniTag(text = "MARKETING", bg = SwipeyBlack.copy(alpha = 0.10f), fg = SwipeyBlack)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SwipeyViolet)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = "PRIORITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White,
                )
            }
        }
        Spacer(Modifier.height(Spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Due Nov 15th, 2024",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SwipeyBlack.copy(alpha = 0.7f),
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .drawBehind {
                        val stroke = 4.dp.toPx()
                        drawArc(
                            color = SwipeyBlack.copy(alpha = 0.20f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = stroke),
                        )
                        drawArc(
                            color = SwipeyBlack,
                            startAngle = -90f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    },
            )
        }
    }
}

@Composable
private fun MiniTag(text: String, bg: Color, fg: Color) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = fg,
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun CreateNowBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(90.dp)
            .drawBehind {
                drawCircle(
                    color = SwipeyLime.copy(alpha = 0.5f),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(SwipeyLime.copy(alpha = 0.10f))
                .border(BorderStroke(1.dp, SwipeyLime), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "CREATE\nNOW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                lineHeight = 13.sp,
                color = SwipeyLime,
                modifier = Modifier.padding(Spacing.sm),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeadlineBlock() {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeadlineWord("Take")
        // "control" inside a pill outline.
        Text(
            text = "control",
            fontFamily = InstrumentSerif,
            fontStyle = FontStyle.Italic,
            fontSize = 40.sp,
            color = Color.White,
            modifier = Modifier
                .clip(CircleShape)
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), CircleShape)
                .padding(horizontal = 16.dp, vertical = 2.dp),
        )
        HeadlineWord("of")
        // Overlapping colored dots accent.
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SwipeyLime),
            )
            Box(
                modifier = Modifier
                    .offset(x = 16.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E5FF).copy(alpha = 0.8f)),
            )
        }
        HeadlineWord("your")
        HeadlineWord("creativity")
    }
}

@Composable
private fun HeadlineWord(text: String) {
    Text(
        text = text,
        fontFamily = InstrumentSerif,
        fontStyle = FontStyle.Italic,
        fontSize = 40.sp,
        color = Color.White,
    )
}

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(SwipeyLime)
            .clickable(onClick = onClick)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Get Started",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = SwipeyBlack,
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.lg),
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SwipeyBlack)
                .border(BorderStroke(2.dp, SwipeyLime), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Get started",
                tint = SwipeyLime,
                modifier = Modifier.graphicsLayer { rotationZ = -45f },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, heightDp = 880, widthDp = 420)
@Composable
private fun WelcomeScreenPreview() {
    SwipeyTheme {
        WelcomeScreen(onGetStarted = {})
    }
}
