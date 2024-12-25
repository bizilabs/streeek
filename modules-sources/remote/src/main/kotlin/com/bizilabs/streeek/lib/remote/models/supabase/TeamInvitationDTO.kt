package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTeamInvitationsRequestDTO(
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_account_id")
    val accountId: Long
)

@Serializable
data class TeamInvitationDTO(
    val id: Long,
    val token: Long,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("expires_at")
    val expiresAt: String,
    @SerialName("creator_account_id")
    val creatorId: Long
)

@Serializable
data class CreateTeamInvitationRequestDTO(
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_inviter_account_id")
    val accountId: Long,
    @SerialName("p_expires_in")
    val duration: Long
)

@Serializable
data class CreateTeamInvitationDTO(
    val token: Long,
    @SerialName("expires_at")
    val expiresAt: String
)

@Serializable
data class DeleteTeamInvitationRequestDTO(
    @SerialName("p_team_invitation_id")
    val invitationId: Long,
    @SerialName("p_account_id")
    val accountId: Long
)

@Serializable
data class JoinTeamInvitationRequestDTO(
    @SerialName("p_account_id")
    val accountId: Long,
    @SerialName("p_token")
    val token: Long
)

@Serializable
data class JoinTeamInvitationDTO(
    @SerialName("team_id")
    val teamId: Long,
    val role: String
)
