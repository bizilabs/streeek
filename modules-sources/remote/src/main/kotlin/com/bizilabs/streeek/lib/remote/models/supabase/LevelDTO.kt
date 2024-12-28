package com.bizilabs.streeek.lib.remote.models.supabase

import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
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

@Serializable
data class GetLevelRequestDTO(
    @SerialName("p_page")
    val page: Int,
    @SerialName("p_page_size")
    val size: Int = PAGE_SIZE
)
