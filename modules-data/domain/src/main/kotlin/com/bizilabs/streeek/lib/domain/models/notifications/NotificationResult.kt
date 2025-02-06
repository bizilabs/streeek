package com.bizilabs.streeek.lib.domain.models.notifications

import android.content.Intent
import android.net.Uri
import com.bizilabs.streeek.lib.domain.helpers.JsonSerializer
import com.bizilabs.streeek.lib.domain.helpers.tryOrNull
import kotlinx.serialization.Serializable

fun Intent.asNotificationResult(): NotificationResult? {
    val result = getStringExtra("notification_result") ?: return null
    return result.asNotificationResult()
}

fun String.asNotificationResult(): NotificationResult? {
    return tryOrNull { JsonSerializer.decodeFromString(this) }
}

fun String.extractParam(param: String): String? {
    return Uri.parse(this).getQueryParameter(param)
}

@Serializable
data class NotificationResult(
    val type: String,
    val uri: String,
)
