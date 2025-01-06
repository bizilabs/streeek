package com.bizilabs.streeek.lib.design.helpers

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
fun isSystemNetworkConnected(): Boolean = _isSystemNetworkConnected()

@Composable
@ReadOnlyComposable
internal fun _isSystemNetworkConnected(): Boolean {
    val activity = (LocalContext.current as Activity)
    val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}
