package com.bizilabs.streeek.lib.local.models

import com.bizilabs.streeek.lib.local.entities.LevelEntity
import kotlinx.serialization.Serializable

@Serializable
data class LevelCache(
    val id: Long,
    val name: String,
    val number: Long,
    val maxPoints: Long,
    val minPoints: Long,
    val createdAt: String
)

fun LevelCache.toEntity() = LevelEntity(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt
)

fun LevelEntity.toCache() = LevelCache(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints,
    createdAt = createdAt
)
