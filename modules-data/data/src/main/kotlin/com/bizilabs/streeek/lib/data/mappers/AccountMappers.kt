package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import java.util.Date

fun AccountDTO.toDomain(): AccountDomain = AccountDomain(
    id = id,
    githubId = githubId,
    username = username,
    email = email,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate()?.time ?: Date(),
    updatedAt = updatedAt.asDate()?.time ?: Date()
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
    createdAt = createdAt.asDate()?.time ?: Date(),
    updatedAt = createdAt.asDate()?.time ?: Date()
)
