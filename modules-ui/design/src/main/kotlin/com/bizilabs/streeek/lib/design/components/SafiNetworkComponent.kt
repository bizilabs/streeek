package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SignalWifi4Bar
import androidx.compose.material.icons.rounded.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels

private data class SafiNetworkData(
    val icon: ImageVector,
    val label: Int,
    val backgroundColor: Color,
    val onBackgroundColor: Color,
)

@Composable
private fun getNetworkData(isNetworkConnected: Boolean) =
    if (isNetworkConnected) {
        SafiNetworkData(
            icon = Icons.Rounded.SignalWifi4Bar,
            label = SafiStringLabels.NetworkConnectionYes,
            backgroundColor = MaterialTheme.colorScheme.success,
            onBackgroundColor = MaterialTheme.colorScheme.onSuccess,
        )
    } else {
        SafiNetworkData(
            icon = Icons.Rounded.SignalWifiConnectedNoInternet4,
            label = SafiStringLabels.NetworkConnectionNo,
            backgroundColor = MaterialTheme.colorScheme.error,
            onBackgroundColor = MaterialTheme.colorScheme.onError,
        )
    }

@Composable
internal fun SafiNetworkComponent(isNetworkConnected: Boolean) {
    val data = getNetworkData(isNetworkConnected = isNetworkConnected)
    SafiCenteredRow(modifier = Modifier.fillMaxWidth().background(data.backgroundColor)) {
        SafiCenteredRow(modifier = Modifier.padding(8.dp)) {
            Icon(
                imageVector = data.icon,
                contentDescription = stringResource(data.label),
                tint = data.onBackgroundColor,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(data.label),
                color = data.onBackgroundColor,
            )
        }
    }
}

@Preview
@Composable
private fun SafiNetworkComponentLightNoInternetPreview() {
    SafiTheme {
        SafiNetworkComponent(isNetworkConnected = true)
    }
}

@Preview
@Composable
private fun SafiNetworkComponentLightHasInternetPreview() {
    SafiTheme {
        SafiNetworkComponent(isNetworkConnected = false)
    }
}

@Preview
@Composable
private fun SafiNetworkComponentDarkNoInternetPreview() {
    SafiTheme(isDarkThemeEnabled = true) {
        SafiNetworkComponent(isNetworkConnected = true)
    }
}

@Preview
@Composable
private fun SafiNetworkComponentDarkHasInternetPreview() {
    SafiTheme(isDarkThemeEnabled = true) {
        SafiNetworkComponent(isNetworkConnected = false)
    }
}
