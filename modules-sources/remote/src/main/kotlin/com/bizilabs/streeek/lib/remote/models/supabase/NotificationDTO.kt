package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO(
    val id: Long,
    @SerialName("account_id")
    val accountId: Long,
    val title: String,
    val message: String,
    val payload: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("read_at")
    val readAt: String?
){
    object Columns {
        const val CreatedAt = "created_at"
    }
}
