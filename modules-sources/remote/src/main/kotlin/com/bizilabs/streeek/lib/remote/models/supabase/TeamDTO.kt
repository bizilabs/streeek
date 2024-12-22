package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTeamRequestDTO(
    @SerialName("p_name")
    val name: String,
    @SerialName("p_account_id")
    val account: Long,
    @SerialName("p_is_public")
    val public: Boolean,
)

@Serializable
data class JoinTeamRequestDTO(
    @SerialName("p_account_id")
    val account: Long,
    @SerialName("p_team_id")
    val team: Long,
)
