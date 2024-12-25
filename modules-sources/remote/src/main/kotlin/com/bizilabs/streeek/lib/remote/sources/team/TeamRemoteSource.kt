package com.bizilabs.streeek.lib.remote.sources.team

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.GetTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.JoinTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithMembersDTO
import com.bizilabs.streeek.lib.remote.models.supabase.UpdateTeamRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface TeamRemoteSource {
    suspend fun createTeam(request: CreateTeamRequestDTO): NetworkResult<Long>
    suspend fun updateTeam(request: UpdateTeamRequestDTO): NetworkResult<Boolean>
    suspend fun fetchTeam(
        teamId: Long,
        accountId: Long,
        page: Int
    ): NetworkResult<TeamWithMembersDTO>

    suspend fun joinTeam(accountId: Long, teamId: Long): NetworkResult<Unit>
}

internal class TeamRemoteSourceImpl(
    private val supabase: SupabaseClient
) : TeamRemoteSource {

    override suspend fun createTeam(request: CreateTeamRequestDTO): NetworkResult<Long> =
        safeSupabaseCall {
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.Create,
                    parameters = request.asJsonObject()
                )
                .decodeAs()
        }

    override suspend fun updateTeam(request: UpdateTeamRequestDTO): NetworkResult<Boolean> =
        safeSupabaseCall {
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.Update,
                    parameters = request.asJsonObject()
                )
            true
        }

    override suspend fun fetchTeam(
        teamId: Long,
        accountId: Long,
        page: Int
    ): NetworkResult<TeamWithMembersDTO> = safeSupabaseCall {
        val parameters =
            GetTeamRequestDTO(teamId = teamId, accountId = accountId, page = page).asJsonObject()
        supabase.postgrest
            .rpc(
                function = Supabase.Functions.Teams.GetMembersWithAccount,
                parameters = parameters
            )
            .decodeAs()
    }

    override suspend fun joinTeam(accountId: Long, teamId: Long): NetworkResult<Unit> =
        safeSupabaseCall {
            val parameters = JoinTeamRequestDTO(account = accountId, teamId).asJsonObject()
            supabase.postgrest.rpc(
                function = Supabase.Functions.Teams.Join,
                parameters = parameters
            )
        }

}
