package com.bizilabs.streeek.lib.local.models

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
