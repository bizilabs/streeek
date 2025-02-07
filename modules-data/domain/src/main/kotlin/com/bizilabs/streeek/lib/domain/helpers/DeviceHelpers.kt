package com.bizilabs.streeek.lib.domain.helpers

import android.os.Build
import java.util.Locale

fun isDeviceHuawei(): Boolean {
    val deviceInfo =
        listOf(
            Build.MANUFACTURER,
            Build.BRAND,
            Build.MODEL,
            Build.PRODUCT,
            Build.DEVICE,
            System.getProperty("ro.product.vendor.manufacturer") ?: "",
            System.getProperty("ro.build.hw_emui_api_level") ?: "",
        ).joinToString(" ").lowercase(Locale.getDefault())
    val huaweiKeywords =
        listOf("huawei", "honor", "nova", "ascend", "mate", "porsche", "hms", "emui")
    return huaweiKeywords.any { deviceInfo.contains(it) }
}
