package com.bizilabs.streeek.lib.remote.sources.team.requests

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamMemberRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface TeamRequestRemoteSource {
    suspend fun requestToJoinTeam(
        teamId: Long,
        accountId: Long,
    ): NetworkResult<Boolean>
}

class TeamRequestRemoteSourceImpl(
    private val supabase: SupabaseClient,
) : TeamRequestRemoteSource {
    override suspend fun requestToJoinTeam(
        teamId: Long,
        accountId: Long,
    ): NetworkResult<Boolean> = safeSupabaseCall {
        val request = TeamMemberRequestDTO(teamId = teamId, accountId = accountId).asJsonObject()
        supabase.postgrest
            .rpc(
                function = Supabase.Functions.Teams.MemberRequests.REQUESTTOJOINTEAM,
                parameters = request,
            )
        true
    }
}

