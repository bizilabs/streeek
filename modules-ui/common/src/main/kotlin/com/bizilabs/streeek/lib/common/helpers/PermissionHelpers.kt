package com.bizilabs.streeek.lib.common.helpers

import android.content.Context

fun Context.permissionIsGranted(permission: String): Boolean {
    return checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}
