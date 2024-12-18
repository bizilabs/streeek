package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import com.bizilabs.streeek.lib.remote.models.AccountFullDTO

fun AccountDTO.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = updatedAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
    points = 0,
    level = null
)

fun AccountDomain.toCache(): AccountCache = AccountCache(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asString(format = DateFormats.ISO_8601_Z) ?: "",
    updatedAt = updatedAt.asString(format = DateFormats.ISO_8601_Z) ?: "",
    points = points,
    level = level?.toCache()
)

fun AccountCache.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate(format = DateFormats.ISO_8601_Z)?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = updatedAt.asDate(format = DateFormats.ISO_8601_Z)?.datetimeSystem ?: SystemLocalDateTime,
    points = points,
    level = level?.toDomain()
)

fun AccountFullDTO.toDomain() = AccountDomain(
    id = account.id,
    githubId = account.githubId,
    username = account.username,
    email = account.email,
    bio = account.bio,
    avatarUrl = account.avatarUrl,
    createdAt = account.createdAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = account.updatedAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
    points = points ?: 0,
    level = level?.toDomain()
)
