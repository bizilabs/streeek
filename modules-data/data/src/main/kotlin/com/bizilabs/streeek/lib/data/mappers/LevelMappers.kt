package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.LevelDomain
import com.bizilabs.streeek.lib.local.models.LevelCache
import com.bizilabs.streeek.lib.remote.models.supabase.LevelDTO

fun LevelDTO.toDomain() = LevelDomain(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt.asDate(format = DateFormats.ISO_8601)?.datetimeSystem ?: SystemLocalDateTime,
)

fun LevelDomain.toCache() = LevelCache(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt.asString(format = DateFormats.ISO_8601_Z) ?: "",
)

fun LevelCache.toDomain() = LevelDomain(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt.asDate(format = DateFormats.ISO_8601_Z)?.datetimeSystem ?: SystemLocalDateTime,
)
