package com.bizilabs.streeek.lib.domain.models

import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import kotlinx.datetime.LocalDateTime

data class NotificationDomain(
    val id: Long,
    val title: String,
    val message: String,
    val payload: String?,
    val createdAt: LocalDateTime,
    val readAt: LocalDateTime?
){
    val createdAtMillis: Long
        get() = createdAt.datetimeUTC.toEpochMilliseconds()
}
