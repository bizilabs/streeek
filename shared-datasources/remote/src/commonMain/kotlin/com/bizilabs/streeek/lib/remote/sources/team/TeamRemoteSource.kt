package com.bizilabs.streeek.lib.remote.sources.team

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.AccountTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.GetTeamAndMembersRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.GetTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.JoinTeamInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.JoinTeamInvitationRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamAndMembersDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithDetailDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithMembersDTO
import com.bizilabs.streeek.lib.remote.models.supabase.UpdateTeamRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface TeamRemoteSource {
    suspend fun getAccountTeams(accountId: Long): NetworkResult<List<TeamWithDetailDTO>>

    suspend fun createTeam(request: CreateTeamRequestDTO): NetworkResult<Long>

    suspend fun updateTeam(request: UpdateTeamRequestDTO): NetworkResult<Boolean>

    suspend fun fetchTeam(
        teamId: Long,
        accountId: Long,
        page: Int,
    ): NetworkResult<TeamWithMembersDTO>

    suspend fun fetchTeamAndMembers(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<TeamAndMembersDTO>>

    suspend fun joinTeam(
        accountId: Long,
        token: String,
    ): NetworkResult<JoinTeamInvitationDTO>

    suspend fun leaveTeam(
        accountId: Long,
        teamId: Long,
    ): NetworkResult<Boolean>

    suspend fun deleteTeam(
        accountId: Long,
        teamId: Long,
    ): NetworkResult<Boolean>
}

internal class TeamRemoteSourceImpl(
    private val supabase: SupabaseClient,
) : TeamRemoteSource {
    override suspend fun getAccountTeams(accountId: Long): NetworkResult<List<TeamWithDetailDTO>> =
        safeSupabaseCall {
            val request = mapOf("p_account_id" to accountId)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.GETACCOUNTTEAMS,
                    parameters = request.asJsonObject(),
                )
                .decodeList()
        }

    override suspend fun createTeam(request: CreateTeamRequestDTO): NetworkResult<Long> =
        safeSupabaseCall {
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.CREATE,
                    parameters = request.asJsonObject(),
                )
                .decodeAs()
        }

    override suspend fun updateTeam(request: UpdateTeamRequestDTO): NetworkResult<Boolean> =
        safeSupabaseCall {
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.UPDATE,
                    parameters = request.asJsonObject(),
                )
            true
        }

    override suspend fun fetchTeamAndMembers(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<TeamAndMembersDTO>> =
        safeSupabaseCall {
            val parameters =
                GetTeamAndMembersRequestDTO(accountId = accountId, page = page).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.GETTEAMANDMEMBERS,
                    parameters = parameters,
                )
                .decodeList()
        }

    override suspend fun fetchTeam(
        teamId: Long,
        accountId: Long,
        page: Int,
    ): NetworkResult<TeamWithMembersDTO> =
        safeSupabaseCall {
            val parameters =
                GetTeamRequestDTO(
                    teamId = teamId,
                    accountId = accountId,
                    page = page,
                ).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.GETMEMBERSWITHACCOUNT,
                    parameters = parameters,
                )
                .decodeAs()
        }

    override suspend fun joinTeam(
        accountId: Long,
        token: String,
    ): NetworkResult<JoinTeamInvitationDTO> =
        safeSupabaseCall("Failed to joining team.") {
            val parameters =
                JoinTeamInvitationRequestDTO(accountId = accountId, token = token).asJsonObject()
            supabase.postgrest.rpc(
                function = Supabase.Functions.Teams.JOIN,
                parameters = parameters,
            ).decodeAs()
        }

    override suspend fun leaveTeam(
        accountId: Long,
        teamId: Long,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val parameters = AccountTeamRequestDTO(account = accountId, teamId).asJsonObject()
            supabase.postgrest.rpc(
                function = Supabase.Functions.Teams.LEAVE,
                parameters = parameters,
            )
            true
        }

    override suspend fun deleteTeam(
        accountId: Long,
        teamId: Long,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val parameters = AccountTeamRequestDTO(account = accountId, team = teamId)
            supabase.postgrest.rpc(
                function = Supabase.Functions.Teams.DELETE,
                parameters = parameters.asJsonObject(),
            )
            true
        }
}
