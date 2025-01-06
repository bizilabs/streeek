package com.bizilabs.streeek.lib.local.models

import com.bizilabs.streeek.lib.local.entities.NotificationEntity

data class NotificationCache(
    val id: Long,
    val title: String,
    val message: String,
    val payload: String?,
    val createdAt: String,
    val createdAtMillis: Long,
    val readAt: String?,
) {
    internal fun toEntity() =
        NotificationEntity(
            id = id,
            title = title,
            message = message,
            payload = payload,
            createdAt = createdAt,
            createdAtMillis = createdAtMillis,
            readAt = readAt,
        )
}
