package com.bizilabs.streeek.lib.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.design.atoms.getColorScheme
import com.bizilabs.streeek.lib.design.atoms.getTypography
import com.bizilabs.streeek.lib.design.helpers.LocalSafiColorScheme
import com.bizilabs.streeek.lib.design.helpers.StatusAndNavigationBar
import com.bizilabs.streeek.lib.design.helpers.getSafiColorScheme

@Composable
fun SafiTheme(
    typography: SafiTypography = SafiTypography.MONO,
    isDarkThemeEnabled: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(darkTheme = isDarkThemeEnabled)
    val safiColorScheme = getSafiColorScheme(isDarkThemeEnabled = isDarkThemeEnabled)
    val typography = getTypography(typography = typography)

    StatusAndNavigationBar(colorScheme = colorScheme, isDarkThemeEnabled = isDarkThemeEnabled)

    CompositionLocalProvider(
        LocalSafiColorScheme provides safiColorScheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content,
        )
    }

    Card { }
}
