package com.bizilabs.streeek.lib.design.atoms

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.bizilabs.streeek.lib.resources.fonts.SafiFonts

@Composable
private fun getTypography(family: FontFamily): Typography {
    return Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = family),
        displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = family),
        displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = family),
        titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = family),
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = family),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = family),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = family),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = family),
        bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = family),
        labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = family),
        labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = family),
        labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = family),
    )
}

enum class SafiTypography {
    SANS,
    MONO,
    NOTO, ;

    val label: String
        get() =
            when (this) {
                SafiTypography.SANS -> "Sans Serif"
                SafiTypography.MONO -> "Monospace"
                SafiTypography.NOTO -> "Noto"
            }
}

@Composable
fun getTypography(typography: SafiTypography): Typography {
    return when (typography) {
        SafiTypography.SANS -> SansTypography
        SafiTypography.MONO -> MonoTypography
        SafiTypography.NOTO -> NotoTypography
    }
}

val SansTypography: Typography
    @Composable
    get() {
        val family =
            FontFamily(
                Font(SafiFonts.Sans.bold, FontWeight.Bold),
                Font(SafiFonts.Sans.boldItalic, FontWeight.Bold, FontStyle.Italic),
                Font(SafiFonts.Sans.semiBold, FontWeight.SemiBold),
                Font(SafiFonts.Sans.semiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
                Font(SafiFonts.Sans.medium, FontWeight.Medium),
                Font(SafiFonts.Sans.mediumItalic, FontWeight.Medium, FontStyle.Italic),
                Font(SafiFonts.Sans.regular, FontWeight.Normal),
                Font(SafiFonts.Sans.regularItalic, FontWeight.Normal, FontStyle.Italic),
                Font(SafiFonts.Sans.light, FontWeight.Light),
                Font(SafiFonts.Sans.lightItalic, FontWeight.Light, FontStyle.Italic),
                Font(SafiFonts.Sans.thin, FontWeight.Thin),
                Font(SafiFonts.Sans.thinItalic, FontWeight.Thin, FontStyle.Italic),
            )
        return getTypography(family = family)
    }

val MonoTypography: Typography
    @Composable
    get() {
        val family =
            FontFamily(
                Font(SafiFonts.Mono.bold, FontWeight.Bold),
                Font(SafiFonts.Mono.boldItalic, FontWeight.Bold, FontStyle.Italic),
                Font(SafiFonts.Mono.semiBold, FontWeight.SemiBold),
                Font(SafiFonts.Mono.semiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
                Font(SafiFonts.Mono.medium, FontWeight.Medium),
                Font(SafiFonts.Mono.mediumItalic, FontWeight.Medium, FontStyle.Italic),
                Font(SafiFonts.Mono.regular, FontWeight.Normal),
                Font(SafiFonts.Mono.regularItalic, FontWeight.Normal, FontStyle.Italic),
                Font(SafiFonts.Mono.light, FontWeight.Light),
                Font(SafiFonts.Mono.lightItalic, FontWeight.Light, FontStyle.Italic),
                Font(SafiFonts.Mono.thin, FontWeight.Thin),
                Font(SafiFonts.Mono.thinItalic, FontWeight.Thin, FontStyle.Italic),
            )
        return getTypography(family = family)
    }

val NotoTypography: Typography
    @Composable
    get() {
        val family =
            FontFamily(
                Font(SafiFonts.Noto.bold, FontWeight.Bold),
                Font(SafiFonts.Noto.boldItalic, FontWeight.Bold, FontStyle.Italic),
                Font(SafiFonts.Noto.semiBold, FontWeight.SemiBold),
                Font(SafiFonts.Noto.semiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
                Font(SafiFonts.Noto.medium, FontWeight.Medium),
                Font(SafiFonts.Noto.mediumItalic, FontWeight.Medium, FontStyle.Italic),
                Font(SafiFonts.Noto.regular, FontWeight.Normal),
                Font(SafiFonts.Noto.regularItalic, FontWeight.Normal, FontStyle.Italic),
                Font(SafiFonts.Noto.light, FontWeight.Light),
                Font(SafiFonts.Noto.lightItalic, FontWeight.Light, FontStyle.Italic),
                Font(SafiFonts.Noto.thin, FontWeight.Thin),
                Font(SafiFonts.Noto.thinItalic, FontWeight.Thin, FontStyle.Italic),
            )
        return getTypography(family = family)
    }
