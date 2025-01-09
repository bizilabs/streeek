package com.bizilabs.streeek.lib.remote.sources.leaderboard

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardDTO
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardRequestDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface LeaderboardRemoteSource {

    suspend fun fetchDailyLeaderboard(accountId: Long, page: Int): NetworkResult<LeaderboardDTO>

}

class LeaderboardRemoteSourceImpl(val supabase: SupabaseClient) : LeaderboardRemoteSource {
    override suspend fun fetchDailyLeaderboard(
        accountId: Long,
        page: Int
    ): NetworkResult<LeaderboardDTO> = safeSupabaseCall {
        val request = LeaderboardRequestDTO(accountId = accountId, page = page)
        supabase.postgrest
            .rpc(
                function = Supabase.Functions.Leaderboard.DAILY,
                parameters = request.asJsonObject(),
            )
            .decodeAs()
    }
}
