package com.bizilabs.streeek.lib.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bizilabs.streeek.lib.design.atoms.getColorScheme
import com.bizilabs.streeek.lib.design.helpers.LocalSafiColorScheme
import com.bizilabs.streeek.lib.design.helpers.StatusAndNavigationBar
import com.bizilabs.streeek.lib.design.helpers.getSafiColorScheme

@Composable
fun SafiTheme(
    isDarkThemeEnabled: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(darkTheme = isDarkThemeEnabled)
    val safiColorScheme = getSafiColorScheme(isDarkThemeEnabled = isDarkThemeEnabled)
    StatusAndNavigationBar(colorScheme = colorScheme, isDarkThemeEnabled = isDarkThemeEnabled)
    CompositionLocalProvider(
        LocalSafiColorScheme provides safiColorScheme,
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
