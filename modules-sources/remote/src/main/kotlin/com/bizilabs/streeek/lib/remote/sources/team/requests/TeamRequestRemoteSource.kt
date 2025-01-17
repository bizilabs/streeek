package com.bizilabs.streeek.lib.remote.sources.team.requests

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.team.GetTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.MemberAccountRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.ProcessJoinRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.ProcessMultipleRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.ProcessSingleRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamAccountJoinRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamMemberGetDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamMemberRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

interface TeamRequestRemoteSource {
    suspend fun requestToJoinTeam(
        teamId: Long,
        accountId: Long,
    ): NetworkResult<Boolean>

    suspend fun fetchAccountRequests(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<MemberAccountRequestDTO>>

    suspend fun fetchTeamRequests(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<TeamAccountJoinRequestDTO>>

    suspend fun processAccountRequest(
        requestId: Long,
        accountId: Long,
        status: String,
    ): NetworkResult<Boolean>

    suspend fun processSingleRequestAsTeamAdmin(
        requestId: Long,
        adminId: Long,
        teamId: Long,
        status: String,
    ): NetworkResult<Boolean>

    suspend fun processMultipleRequestAsTeamAdmin(
        requestIds: List<Long>,
        adminId: Long,
        teamId: Long,
        status: String,
    ): NetworkResult<Boolean>

    suspend fun delete(id: Long): NetworkResult<Boolean>
}

class TeamRequestRemoteSourceImpl(
    private val supabase: SupabaseClient,
) : TeamRequestRemoteSource {
    override suspend fun requestToJoinTeam(
        teamId: Long,
        accountId: Long,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val request = TeamMemberRequestDTO(teamId = teamId, accountId = accountId).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.REQUESTTOJOINTEAM,
                    parameters = request,
                )
            true
        }

    override suspend fun fetchAccountRequests(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<MemberAccountRequestDTO>> =
        safeSupabaseCall {
            val request =
                TeamMemberGetDTO(accountId = accountId, page = page).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.GETMYREQUESTS,
                    parameters = request,
                )
                .decodeList()
        }

    override suspend fun fetchTeamRequests(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<TeamAccountJoinRequestDTO>> =
        safeSupabaseCall {
            val request = GetTeamRequestDTO(teamId = teamId, page = page).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.GETTEAMREQUESTS,
                    parameters = request,
                )
                .decodeList()
        }

    override suspend fun processAccountRequest(
        requestId: Long,
        accountId: Long,
        status: String,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val request =
                ProcessJoinRequestDTO(
                    requestId = requestId,
                    accountId = accountId,
                    status = status,
                ).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.PROCESSMYREQUEST,
                    parameters = request,
                )
            true
        }

    override suspend fun processSingleRequestAsTeamAdmin(
        requestId: Long,
        adminId: Long,
        teamId: Long,
        status: String,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val request =
                ProcessSingleRequestDTO(
                    requestId = requestId,
                    adminId = adminId,
                    teamId = teamId,
                    status = status,
                ).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.PROCESSSINGLEREQUEST,
                    parameters = request,
                )
            true
        }

    override suspend fun processMultipleRequestAsTeamAdmin(
        requestIds: List<Long>,
        adminId: Long,
        teamId: Long,
        status: String,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val request =
                ProcessMultipleRequestDTO(
                    requestIds = requestIds,
                    adminId = adminId,
                    teamId = teamId,
                    status = status,
                ).asJsonObject()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberRequests.PROCESSMULTIPLTREQUEST,
                    parameters = request,
                )
            true
        }

    override suspend fun delete(id: Long): NetworkResult<Boolean> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.TEAM_REQUESTS)
                .delete {
                    filter { eq("id", id) }
                }
            true
        }
}
