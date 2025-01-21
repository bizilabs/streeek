package com.bizilabs.streeek.feature.push.data

data class NotificationData(
    val eventType: String?,
    val userId: String? = null,
    val issueId: String? = null,
    val title: String? = null,
    val body: String? = null,
)
