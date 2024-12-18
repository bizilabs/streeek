package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.UTCLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import timber.log.Timber

fun AccountDTO.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = createdAt.asDate(format = DateFormats.YYYY_MM_dd_T_HH_mm_ss)?.datetimeSystem ?: SystemLocalDateTime,
)

fun AccountDomain.toCache(): AccountCache = AccountCache(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asString(format = DateFormats.ISO_8601) ?: "",
    updatedAt = createdAt.asString(format = DateFormats.ISO_8601) ?: "",
)

fun AccountCache.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate(format = DateFormats.ISO_8601)?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = createdAt.asDate(format = DateFormats.ISO_8601)?.datetimeSystem ?: SystemLocalDateTime,
)
