package com.bizilabs.streeek.lib.domain.models.notifications

import android.content.Intent

actual fun getNotificationResult(value: Any?): NotificationResult? {
    val intent = (value as? Intent) ?: return null
    val result = intent.getStringExtra("notification_result") ?: return null
    return result.asNotificationResult()
}
