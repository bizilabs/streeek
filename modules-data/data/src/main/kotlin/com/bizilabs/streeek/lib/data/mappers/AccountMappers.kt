package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun AccountDTO.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC),
    updatedAt = updatedAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC)
)

fun AccountDomain.toCache(): AccountCache = AccountCache(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asString() ?: "",
    updatedAt = updatedAt.asString() ?: ""
)

fun AccountCache.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC),
    updatedAt = updatedAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC)
)
