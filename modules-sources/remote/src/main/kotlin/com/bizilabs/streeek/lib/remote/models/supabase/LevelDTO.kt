package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelDTO(
    val id: Long,
    @SerialName("level_name")
    val name: String,
    @SerialName("level_number")
    val number: Long,
    @SerialName("max_points")
    val maxPoints: Long,
    @SerialName("min_points")
    val minPoints: Long,
    @SerialName("created_at")
    val createdAt: String
)
