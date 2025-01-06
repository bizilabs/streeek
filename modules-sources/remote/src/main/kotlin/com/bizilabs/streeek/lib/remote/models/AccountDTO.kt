package com.bizilabs.streeek.lib.remote.models

import com.bizilabs.streeek.lib.remote.models.supabase.LevelDTO
import com.bizilabs.streeek.lib.remote.models.supabase.StreakDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDTO(
    val id: Long,
    @SerialName("github_id")
    val githubId: Long,
    val username: String,
    val email: String,
    val bio: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)

@Serializable
data class AccountFullDTO(
    val account: AccountDTO,
    @SerialName("total_points")
    val points: Long? = 0,
    val level: LevelDTO? = null,
    val streak: StreakDTO? = null,
)

@Serializable
data class AccountCreateRequestDTO(
    @SerialName("github_id")
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)
