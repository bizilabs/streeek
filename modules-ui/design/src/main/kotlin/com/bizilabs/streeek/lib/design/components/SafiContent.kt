package com.bizilabs.streeek.lib.design.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.SafiBarColors
import com.bizilabs.streeek.lib.design.helpers.SetupNavigationBarColor
import com.bizilabs.streeek.lib.design.helpers.SetupStatusBarColor
import com.bizilabs.streeek.lib.design.helpers.isSystemNetworkConnected
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SafiContent(
    isNetworkConnected: Boolean = isSystemNetworkConnected(),
    barColors: SafiBarColors =
        SafiBarColors(
            top = MaterialTheme.colorScheme.background,
            bottom = MaterialTheme.colorScheme.background,
        ),
    content: @Composable () -> Unit,
) {
    SetupStatusBarColor(color = barColors.top)
    SetupNavigationBarColor(color = barColors.bottom)
    Scaffold(
        snackbarHost = {
            AnimatedVisibility(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 96.dp),
                visible = !isNetworkConnected,
            ) {
                SafiNetworkComponent(
                    modifier = Modifier.fillMaxWidth(),
                    isNetworkConnected = isNetworkConnected,
                )
            }
        },
    ) { innerPadding ->
        content()
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiContentHasNetworkPreview() {
    SafiTheme {
        SafiContent(isNetworkConnected = true) {
            Scaffold { padding ->
                Text(modifier = Modifier.padding(padding), text = "streeek")
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiContentHasNoNetworkPreview() {
    SafiTheme {
        SafiContent(isNetworkConnected = false) {
            Scaffold { padding ->
                Text(modifier = Modifier.padding(padding), text = "streeek")
            }
        }
    }
}
