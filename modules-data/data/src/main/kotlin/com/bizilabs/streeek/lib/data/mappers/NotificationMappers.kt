package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.UTCLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.local.models.NotificationCache
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationDTO
import kotlinx.datetime.Instant

fun NotificationDTO.toDomain() =
    NotificationDomain(
        id = id,
        title = title,
        message = message,
        payload = payload,
        createdAt = Instant.parse(createdAt).datetimeUTC,
        readAt = readAt?.let { Instant.parse(it).datetimeUTC },
    )

fun NotificationDomain.toDTO(accountId: Long) =
    NotificationDTO(
        id = id,
        accountId = accountId,
        title = title,
        message = message,
        payload = payload,
        createdAt = createdAt.toString(),
        readAt = readAt?.toString(),
    )

fun NotificationDomain.toCache() =
    NotificationCache(
        id = id,
        title = title,
        message = message,
        payload = payload,
        createdAt = createdAt.toString(),
        createdAtMillis = createdAtMillis,
        readAt = readAt?.toString(),
    )

fun NotificationCache.toDomain() =
    NotificationDomain(
        id = id,
        title = title,
        message = message,
        payload = payload,
        createdAt = createdAt.asDate(DateFormats.YYYY_MM_dd_T_HH_mm_ss_SSSSSS)?.datetimeUTC ?: UTCLocalDateTime,
        readAt = readAt?.asDate(DateFormats.YYYY_MM_dd_T_HH_mm_ss_SSSSSS)?.datetimeUTC,
    )
