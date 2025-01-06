package com.bizilabs.streeek.lib.design.atoms

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal val SafiDarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        onPrimary = OnPrimaryDark,
        secondary = SecondaryDark,
        onSecondary = OnSecondaryDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        error = ErrorDark,
        onError = OnErrorDark,
    )

internal val SafiLightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        onPrimary = OnPrimaryLight,
        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        error = ErrorLight,
        onError = OnErrorLight,
    )

internal fun getColorScheme(darkTheme: Boolean) = if (darkTheme) SafiDarkColorScheme else SafiLightColorScheme
