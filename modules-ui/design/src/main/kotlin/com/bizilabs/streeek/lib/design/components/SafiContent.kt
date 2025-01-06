package com.bizilabs.streeek.lib.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bizilabs.streeek.lib.design.helpers.isSystemNetworkConnected
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiContent(
    isNetworkConnected: Boolean = isSystemNetworkConnected(),
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedVisibility(visible = !isNetworkConnected) {
            SafiNetworkComponent(isNetworkConnected = isNetworkConnected)
        }
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) { content() }
    }
}

@Preview
@Composable
private fun SafiContentHasNetworkPreview() {
    SafiTheme(isDarkThemeEnabled = false) {
        Scaffold { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxWidth()) {
                SafiContent(isNetworkConnected = true) {
                    Text(text = "dcke")
                }
            }
        }
    }
}

@Preview
@Composable
private fun SafiContentHasNoNetworkPreview() {
    SafiTheme(isDarkThemeEnabled = false) {
        SafiContent(isNetworkConnected = false) {
            Scaffold { padding ->
                Text(modifier = Modifier.padding(padding), text = "dcke")
            }
        }
    }
}

@Preview
@Composable
private fun SafiContentHasNetworkDarkPreview() {
    SafiTheme(isDarkThemeEnabled = true) {
        SafiContent(isNetworkConnected = true) {
            Scaffold { padding ->
                Text(modifier = Modifier.padding(padding), text = "dcke")
            }
        }
    }
}

@Preview
@Composable
private fun SafiContentHasNoNetworkDarkPreview() {
    SafiTheme(isDarkThemeEnabled = true) {
        SafiContent(isNetworkConnected = false) {
            Scaffold { padding ->
                Text(modifier = Modifier.padding(padding), text = "dcke")
            }
        }
    }
}
