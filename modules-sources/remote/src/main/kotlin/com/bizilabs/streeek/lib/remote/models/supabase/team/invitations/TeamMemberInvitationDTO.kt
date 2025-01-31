package com.bizilabs.streeek.lib.remote.models.supabase.team.invitations

import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountsNotInTeamDTO(
    @SerialName("id")
    val accountId: Long,
    @SerialName("username")
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("is_invited")
    val isInvited: Boolean,
)

@Serializable
data class DeleteAccountInvitationDTO(
    @SerialName("team_invitation_id")
    val inviteId: Long,
    val message: String,
)

@Serializable
data class MemberInviteDTO(
    @SerialName("invite_id")
    val inviteId: Long,
    val status: String,
    @SerialName("team_id")
    val teamId: Long,
    @SerialName("created_at")
    val createdAt: String,
)

@Serializable
data class TeamOwnerDTO(
    val id: Long,
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
)

@Serializable
data class AccountTeamInvitesDTO(
    val invite: MemberInviteDTO,
    val team: TeamDTO,
    @SerialName("team_owner")
    val teamOwner: TeamOwnerDTO,
)

@Serializable
data class TeamAccountInvitesDTO(
    @SerialName("invite_id")
    val inviteId: Long,
    @SerialName("invitee_id")
    val inviteeId: Long,
    @SerialName("username")
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("invited_on")
    val invitedOn: String,
)

@Serializable
data class GetAccountsDTO(
    @SerialName("team_id_param") val teamId: Long,
    @SerialName("page_param") val page: Int,
    @SerialName("page_size_param") val pageSize: Int = PAGE_SIZE,
)

@Serializable
data class SearchAccountNotInTeamDTO(
    @SerialName("search_param") val searchParam: String,
    @SerialName("team_id_param") val teamId: Long,
    @SerialName("page_param") val page: Int,
    @SerialName("page_size_param") val pageSize: Int = PAGE_SIZE,
)

@Serializable
data class SendAccountInvitationDTO(
    @SerialName("team_id_param") val teamId: Long,
    @SerialName("admin_id_param") val adminId: Long,
    @SerialName("invitee_id_param") val inviteeId: Long,
)

@Serializable
data class SendMultipleAccountInvitationDTO(
    @SerialName("team_id_param") val teamId: Long,
    @SerialName("admin_id_param") val adminId: Long,
    @SerialName("invitee_ids_param") val inviteeIds: List<Long>,
)

@Serializable
data class DeleteAccountInvitationRequestDTO(
    @SerialName("invite_id_param") val inviteId: Long,
    @SerialName("admin_id_param") val adminId: Long,
)

@Serializable
data class ProcessAccountInviteDTO(
    @SerialName("invite_id_param") val inviteId: Long,
    @SerialName("invitee_id_param") val inviteeId: Long,
    @SerialName("status_param") val status: String,
)

@Serializable
data class ProcessAccountMultipleInvitesDTO(
    @SerialName("invite_ids_param") val inviteIds: List<Long>,
    @SerialName("invitee_id_param") val inviteeId: Long,
    @SerialName("status_param") val status: String,
)

@Serializable
data class GetAllAccountInvitesDTO(
    @SerialName("account_id_param") val accountId: Long,
    @SerialName("page_param") val page: Int,
    @SerialName("page_size_param") val pageSize: Int = PAGE_SIZE,
)
