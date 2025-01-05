package com.bizilabs.streeek.lib.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bizilabs.streeek.lib.local.models.NotificationCache

@Entity("notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val message: String,
    val payload: String?,
    val createdAt: String,
    val createdAtMillis: Long,
    val readAt: String?
){
    internal fun toCache() = NotificationCache(
        id = id,
        title = title,
        message = message,
        payload = payload,
        createdAt = createdAt,
        createdAtMillis = createdAtMillis,
        readAt = readAt
    )
}
