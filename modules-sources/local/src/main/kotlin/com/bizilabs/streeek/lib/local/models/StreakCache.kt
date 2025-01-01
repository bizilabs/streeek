package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable

@Serializable
data class StreakCache(
    val current: Int,
    val longest: Int,
    val updatedAt: String
)
