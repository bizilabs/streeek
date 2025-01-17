package com.bizilabs.streeek.lib.remote.models.supabase.team

import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.models.AccountLightDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamMemberRequestDTO(
    @SerialName("p_team_id") val teamId: Long,
    @SerialName("p_account_id") val accountId: Long,
)

@Serializable
data class TeamMemberGetDTO(
    @SerialName("p_account_id") val accountId: Long,
    @SerialName("p_page") val page: Int,
    @SerialName("p_page_size") val pageSize: Int = PAGE_SIZE,
)

@Serializable
data class ProcessJoinRequestDTO(
    @SerialName("p_request_id") val requestId: Long,
    @SerialName("p_account_id") val accountId: Long,
    @SerialName("p_status") val status: String,
)

@Serializable
data class ProcessSingleRequestDTO(
    @SerialName("p_request_id")
    val requestId: Long,
    @SerialName("p_admin_id")
    val adminId: Long,
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_status")
    val status: String,
)

@Serializable
data class ProcessMultipleRequestDTO(
    @SerialName("p_request_ids")
    val requestIds: List<Long>,
    @SerialName("p_admin_id")
    val adminId: Long,
    @SerialName("p_team_id")
    val teamId: Long,
    @SerialName("p_status")
    val status: String,
)

@Serializable
data class GetTeamRequestDTO(
    @SerialName("p_team_id") val teamId: Long,
    @SerialName("p_page") val page: Int,
    @SerialName("p_page_size") val pageSize: Int = PAGE_SIZE,
)

@Serializable
data class TeamJoinRequestDTO(
    val id: Long,
    val status: String,
    @SerialName("created_at")
    val createdAt: String,
)

@Serializable
data class MemberAccountRequestDTO(
    val request: TeamJoinRequestDTO,
    val team: TeamDTO,
    val members: List<AccountLightDTO>?,
)

@Serializable
data class TeamAccountJoinRequestDTO(
    val request: TeamJoinRequestDTO,
    val account: AccountLightDTO,
)
