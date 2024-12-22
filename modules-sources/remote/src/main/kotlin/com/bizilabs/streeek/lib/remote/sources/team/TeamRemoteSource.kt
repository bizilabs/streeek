package com.bizilabs.streeek.lib.remote.sources.team

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface TeamRemoteSource {
    suspend fun createTeam(request: CreateTeamRequestDTO): NetworkResult<Long>
    suspend fun fetchTeam(id: Long): NetworkResult<String>
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

    override suspend fun fetchTeam(id: Long): NetworkResult<String> = safeSupabaseCall {
        val parameters = mapOf("p_team_id" to id).asJsonObject()
        supabase.postgrest
            .rpc(
                function = Supabase.Functions.Teams.Get,
                parameters = parameters
            )
            .data
    }

    override suspend fun joinTeam(accountId: Long, teamId: Long): NetworkResult<Unit> =
        safeSupabaseCall {
            val parameters = mapOf(
                "p_team_id" to teamId,
                "p_account_id" to accountId
            ).asJsonObject()
            supabase.postgrest.rpc(
                function = Supabase.Functions.Teams.Join,
                parameters = parameters
            )
        }

}
