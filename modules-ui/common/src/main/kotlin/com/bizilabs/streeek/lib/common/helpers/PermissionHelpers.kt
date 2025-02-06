package com.bizilabs.streeek.lib.common.helpers

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

fun Context.permissionIsGranted(permission: String): Boolean {
    return checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private val mutableLauncherState = MutableStateFlow<Boolean?>(null)
val launcherState = mutableLauncherState.asStateFlow()

private lateinit var permissionLauncher: ActivityResultLauncher<String>

fun ComponentActivity.registerLaunchers() {
    permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            mutableLauncherState.update { result }
        }
}

/**
 * @param permission the permission you'd like to request e.g [android.Manifest.permission.INTERNET]
 * @param fallback a block of code to run if the permission is not granted e.g opening the network settings
 */
fun ComponentActivity.requestSinglePermission(
    permission: String,
    fallback: Activity.() -> Unit = {},
) {
    val hasDeniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    if (!hasDeniedPermission) permissionLauncher.launch(permission) else fallback()
}
