package com.bizilabs.streeek.lib.design.helpers

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
internal fun StatusAndNavigationBar(
    colorScheme: ColorScheme,
    isDarkThemeEnabled: Boolean,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkThemeEnabled
                isAppearanceLightNavigationBars = !isDarkThemeEnabled
            }
        }
    }
}

@Composable
fun SetupStatusBarColor(color: Color?) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = (color ?: colorScheme.background).toArgb()
}

@Composable
fun SetupNavigationBarColor(color: Color?) {
    val window = (LocalView.current.context as Activity).window
    window.navigationBarColor = (color ?: colorScheme.background).toArgb()
}
