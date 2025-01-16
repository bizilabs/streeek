package com.bizilabs.streeek.lib.remote.models.supabase.team

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamMemberRequestDTO(
    @SerialName("p_team_id") val teamId: Long,
    @SerialName("p_account_id") val accountId: Long,
)
