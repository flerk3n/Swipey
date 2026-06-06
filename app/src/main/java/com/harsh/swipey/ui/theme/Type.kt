package com.harsh.swipey.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.harsh.swipey.R

// ---------------------------------------------------------------------------
// Font families (bundled local TTFs in res/font).
//
// NOTE: Instrument Serif ships only Regular + Italic (it is a single-weight
// display serif). Requests for heavier weights resolve to the closest file and
// may be synthetically emboldened — this is expected for this typeface.
// ---------------------------------------------------------------------------

val InstrumentSerif = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans_regular, FontWeight.Normal),
    Font(R.font.instrument_sans_medium, FontWeight.Medium),
    Font(R.font.instrument_sans_semibold, FontWeight.SemiBold),
    Font(R.font.instrument_sans_bold, FontWeight.Bold),
)

// ---------------------------------------------------------------------------
// Type scale (Design.md §1). Mapped onto Material 3 slots so components inherit.
// ---------------------------------------------------------------------------

val SwipeyTypography = Typography().run {
    copy(
        // Headline Large — Instrument Serif 32sp Bold Italic
        headlineLarge = headlineLarge.copy(
            fontFamily = InstrumentSerif,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 32.sp,
            lineHeight = 38.sp,
        ),
        // Headline Medium — Instrument Serif 28sp Italic
        headlineMedium = headlineMedium.copy(
            fontFamily = InstrumentSerif,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic,
            fontSize = 28.sp,
            lineHeight = 34.sp,
        ),
        // Title Large — Instrument Serif 26sp SemiBold
        titleLarge = titleLarge.copy(
            fontFamily = InstrumentSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            lineHeight = 32.sp,
        ),
        // Title Medium — Instrument Serif 22sp SemiBold
        titleMedium = titleMedium.copy(
            fontFamily = InstrumentSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        // Title Small — Instrument Sans 18sp Medium
        titleSmall = titleSmall.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        // Body Large — Instrument Sans 16sp Regular
        bodyLarge = bodyLarge.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        // Body Medium — Instrument Sans 14sp Regular
        bodyMedium = bodyMedium.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Body Small — Instrument Sans 12sp Regular
        bodySmall = bodySmall.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Label Large — Instrument Sans 14sp Medium (muted via color at call site)
        labelLarge = labelLarge.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        // Label Medium — Instrument Sans 12sp Medium
        labelMedium = labelMedium.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        // Label Small — Instrument Sans 11sp Medium
        labelSmall = labelSmall.copy(
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        ),
    )
}

// ---------------------------------------------------------------------------
// Mixed-type headline helper (Design.md / Phase 7.3 Dashboard greeting).
//
// Renders [plain] in normal serif and [emphasis] in italic lime, e.g.
//   buildMixedHeadline("Good morning,", "Harsh")
// ---------------------------------------------------------------------------

fun buildMixedHeadline(
    plain: String,
    emphasis: String,
): AnnotatedString = buildAnnotatedString {
    append(plain)
    if (plain.isNotEmpty() && emphasis.isNotEmpty()) append(" ")
    withStyle(
        SpanStyle(
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = SwipeyLime,
        )
    ) {
        append(emphasis)
    }
}
