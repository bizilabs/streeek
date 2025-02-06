package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import com.bizilabs.streeek.lib.local.models.ReminderCache
import kotlinx.datetime.DayOfWeek

fun ReminderDomain.toCache() =
    ReminderCache(
        label = label,
        repeat = repeat.map { it.value },
        enabled = enabled,
        hour = hour,
        minute = minute,
    )

fun ReminderCache.toDomain() =
    ReminderDomain(
        label = label,
        repeat = repeat.map { DayOfWeek.of(it) },
        enabled = enabled,
        hour = hour,
        minute = minute,
    )
