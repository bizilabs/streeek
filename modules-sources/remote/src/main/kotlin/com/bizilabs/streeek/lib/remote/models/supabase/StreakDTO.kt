package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreakDTO(
    @SerialName("account_id")
    val accountId: Long,
    @SerialName("current_streak")
    val currentStreak: Int,
    @SerialName("longest_streak")
    val longestStreak: Int,
    @SerialName("last_updated")
    val updatedAt: String
)
