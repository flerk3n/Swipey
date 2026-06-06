package com.harsh.swipey.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Hand-built vector icons for the app.
 *
 * We deliberately avoid `material-icons-extended`: its classes are not on the
 * Compose Preview (Layoutlib) classpath, which throws
 * `ClassNotFoundException: ...GridViewKt` when a preview renders. These vectors
 * only reference `compose.ui.graphics`, so they render everywhere — including
 * previews. Built lazily so nothing initializes at class load.
 */
object SwipeyIcons {

    /** 2×2 grid of squares — Dashboard tab. */
    val Grid: ImageVector by lazy {
        ImageVector.Builder(
            name = "SwipeyGrid",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            fun square(left: Float, top: Float) {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(left, top)
                    lineTo(left + 8f, top)
                    lineTo(left + 8f, top + 8f)
                    lineTo(left, top + 8f)
                    close()
                }
            }
            square(3f, 3f)
            square(13f, 3f)
            square(3f, 13f)
            square(13f, 13f)
        }.build()
    }

    /** Two stacked cards — Flashcard / Swipe tab. */
    val Cards: ImageVector by lazy {
        ImageVector.Builder(
            name = "SwipeyCards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            fun card(left: Float, top: Float, right: Float, bottom: Float) {
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineCap = StrokeCap.Round,
                ) {
                    moveTo(left, top)
                    lineTo(right, top)
                    lineTo(right, bottom)
                    lineTo(left, bottom)
                    close()
                }
            }
            // Back card (upper-right), then front card (lower-left) overlapping it.
            card(10f, 4f, 20f, 16f)
            card(4f, 8f, 14f, 20f)
        }.build()
    }

    /** Lightning bolt — used as a small accent on Welcome idea cards. */
    val Bolt: ImageVector by lazy {
        ImageVector.Builder(
            name = "SwipeyBolt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 2f)
                lineTo(5f, 13f)
                lineTo(11f, 13f)
                lineTo(10f, 22f)
                lineTo(19f, 10f)
                lineTo(13f, 10f)
                close()
            }
        }.build()
    }

    /** Flame — streak indicator on the Flashcard top bar. */
    val Flame: ImageVector by lazy {
        ImageVector.Builder(
            name = "SwipeyFlame",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 23f)
                curveTo(7.5f, 23f, 4f, 19.5f, 4f, 14.5f)
                curveTo(4f, 10.5f, 7f, 8f, 8f, 5f)
                curveTo(9f, 8f, 11f, 9f, 12f, 11f)
                curveTo(13f, 8f, 14f, 6f, 15f, 4f)
                curveTo(17f, 8f, 20f, 10.5f, 20f, 14.5f)
                curveTo(20f, 19.5f, 16.5f, 23f, 12f, 23f)
                close()
            }
        }.build()
    }
}
