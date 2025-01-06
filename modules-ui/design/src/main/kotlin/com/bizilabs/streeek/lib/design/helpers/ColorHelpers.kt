package com.bizilabs.streeek.lib.design.helpers

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.bizilabs.streeek.lib.design.atoms.OnSuccessDark
import com.bizilabs.streeek.lib.design.atoms.OnSuccessLight
import com.bizilabs.streeek.lib.design.atoms.SuccessDark
import com.bizilabs.streeek.lib.design.atoms.SuccessLight

@ConsistentCopyVisibility
@Immutable
data class SafiColorScheme internal constructor(
    val success: Color = SuccessLight,
    val onSuccess: Color = OnSuccessLight,
)

internal val SafiLightColorScheme =
    SafiColorScheme(
        success = SuccessLight,
        onSuccess = OnSuccessLight,
    )

internal val SafiDarkColorScheme =
    SafiColorScheme(
        success = SuccessDark,
        onSuccess = OnSuccessDark,
    )

fun getSafiColorScheme(isDarkThemeEnabled: Boolean) = if (isDarkThemeEnabled) SafiDarkColorScheme else SafiLightColorScheme

internal val LocalSafiColorScheme = staticCompositionLocalOf { SafiColorScheme() }

val ColorScheme.success: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalSafiColorScheme.current.success

val ColorScheme.onSuccess: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalSafiColorScheme.current.onSuccess
