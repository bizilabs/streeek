package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.LevelDomain
import com.bizilabs.streeek.lib.local.models.LevelCache
import com.bizilabs.streeek.lib.remote.models.LevelDTO
import kotlinx.datetime.LocalDateTime

fun LevelDTO.toDomain() = LevelDomain(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = LocalDateTime.parse(createdAt)
)

fun LevelDomain.toCache() = LevelCache(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt.toString()
)

fun LevelCache.toDomain() = LevelDomain(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = LocalDateTime.parse(createdAt)
)
