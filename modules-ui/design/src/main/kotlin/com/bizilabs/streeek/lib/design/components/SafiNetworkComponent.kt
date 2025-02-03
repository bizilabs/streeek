package com.bizilabs.streeek.lib.design.components

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.SignalWifi4Bar
import androidx.compose.material.icons.rounded.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

fun Context.openNetworkSettings() {
    val intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            // Fallback for older Android versions
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        }
    if (this is AppCompatActivity) {
        startActivityForResult(intent, 100)
    } else {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

@Composable
internal fun SafiNetworkComponent(
    isNetworkConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val data = getNetworkData(isNetworkConnected = isNetworkConnected)
    SafiBottomInfoComponent(
        modifier = modifier,
        containerColor = data.backgroundColor,
        contentColor = data.onBackgroundColor,
        title = {
            SafiCenteredRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = data.icon,
                    contentDescription = stringResource(data.label),
                    tint = data.onBackgroundColor,
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = stringResource(data.label),
                    color = data.onBackgroundColor,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        message = {},
    ) {
        IconButton(onClick = { context.openNetworkSettings() }) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.ArrowOutward,
                contentDescription = stringResource(data.label),
                tint = data.onBackgroundColor,
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
