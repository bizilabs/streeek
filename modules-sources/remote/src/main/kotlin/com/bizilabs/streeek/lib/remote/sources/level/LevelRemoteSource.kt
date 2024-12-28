package com.bizilabs.streeek.lib.remote.sources.level

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.LevelDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

interface LevelRemoteSource {
    suspend fun getLevels(): NetworkResult<List<LevelDTO>>
}

class LevelRemoteSourceImpl(
    private val supabase: SupabaseClient
) : LevelRemoteSource {

    override suspend fun getLevels(): NetworkResult<List<LevelDTO>> = safeSupabaseCall {
        supabase
            .from(Supabase.Tables.Levels)
            .select()
            .decodeList()
    }

}
