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
import kotlin.math.sqrt

data class SafiBarColors(
    val top: Color,
    val bottom: Color,
)

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

val Color.isDark: Boolean
    get() {
        val red = this.red
        val green = this.green
        val blue = this.blue

        // Calculate relative brightness using a weighted formula
        val brightness = sqrt(0.299 * red * red + 0.587 * green * green + 0.114 * blue * blue)

        // Use a threshold to determine if the color is dark
        return brightness < 0.5
    }

@Composable
fun SetupStatusBarColor(color: Color?) {
    val view = LocalView.current
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = (color ?: colorScheme.background).toArgb()
    WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightStatusBars = color?.isDark == false
    }
}

@Composable
fun SetupNavigationBarColor(color: Color?) {
    val view = LocalView.current
    val window = (LocalView.current.context as Activity).window
    window.navigationBarColor = (color ?: colorScheme.background).toArgb()
    WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightNavigationBars = color?.isDark == false
    }
}
