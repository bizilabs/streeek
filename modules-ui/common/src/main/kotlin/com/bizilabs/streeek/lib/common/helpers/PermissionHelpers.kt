package com.bizilabs.streeek.lib.common.helpers

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

fun ComponentActivity.requestSinglePermission(permission: String) {
    permissionLauncher.launch(permission)
}
