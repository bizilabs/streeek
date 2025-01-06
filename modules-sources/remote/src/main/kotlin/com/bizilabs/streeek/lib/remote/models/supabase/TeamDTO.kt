package com.bizilabs.streeek.lib.remote.models.supabase

import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTeamRequestDTO(
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_account_id")
    val accountId: Long,
    @SerialName("p_page")
    val page: Int,
    @SerialName("p_page_size")
    val pageSize: Int = PAGE_SIZE,
)

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
data class UpdateTeamRequestDTO(
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_name")
    val name: String,
    @SerialName("p_account_id")
    val accountId: Long,
    @SerialName("p_is_public")
    val public: Boolean,
)

@Serializable
data class AccountTeamRequestDTO(
    @SerialName("p_account_id")
    val account: Long,
    @SerialName("p_team_id")
    val team: Long,
)

@Serializable
data class TeamWithMembersDTO(
    val members: List<TeamMemberDTO>,
    val team: TeamDTO,
    val current: TeamDetailsDTO,
)

@Serializable
data class TeamDetailsDTO(
    val role: String,
    val rank: Long,
)

@Serializable
data class TeamMemberDTO(
    val account: TeamMemberAccountDTO,
    val level: TeamMemberLevelDTO,
    val points: Long,
    val rank: Long,
)

@Serializable
data class TeamWithDetailDTO(
    val team: TeamDTO,
    val member: TeamDetailsDTO,
)

@Serializable
data class TeamDTO(
    val id: Long,
    val created_at: String,
    val is_public: Boolean,
    val name: String,
    @SerialName("members_count")
    val count: Long,
)

@Serializable
data class TeamMemberAccountDTO(
    val avatar_url: String,
    val created_at: String,
    val email: String,
    val id: Long,
    val role: String,
    val username: String,
)

@Serializable
data class TeamMemberLevelDTO(
    val id: Long,
    val level_name: String,
    val level_number: Long,
    val max_points: Long,
    val min_points: Long,
)
