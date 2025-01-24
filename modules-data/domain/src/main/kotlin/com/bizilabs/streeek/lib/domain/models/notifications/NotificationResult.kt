package com.bizilabs.streeek.lib.domain.models.notifications

import android.content.Intent
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

@Serializable
data class NotificationResult(
    val type: String,
    val uri: String,
)
