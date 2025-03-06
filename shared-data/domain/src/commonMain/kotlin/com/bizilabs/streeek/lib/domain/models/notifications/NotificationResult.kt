package com.bizilabs.streeek.lib.domain.models.notifications

import com.bizilabs.streeek.lib.domain.helpers.JsonSerializer
import com.bizilabs.streeek.lib.domain.helpers.tryOrNull
import com.eygraber.uri.Uri
import kotlinx.serialization.Serializable

expect fun getNotificationResult(value: Any?): NotificationResult?

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
