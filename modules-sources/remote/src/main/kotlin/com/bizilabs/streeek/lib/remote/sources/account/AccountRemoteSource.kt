package com.bizilabs.streeek.lib.remote.sources.account

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import com.bizilabs.streeek.lib.remote.models.AccountFullDTO
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

interface AccountRemoteSource {
    suspend fun fetchAccountWithGithubId(id: Int): NetworkResult<AccountDTO?>
    suspend fun createAccount(request: AccountCreateRequestDTO): NetworkResult<AccountDTO>
    suspend fun getAccount(id: Long): NetworkResult<AccountFullDTO>
    suspend fun logout()
}

class AccountRemoteSourceImpl(
    private val supabase: SupabaseClient,
    private val remotePreferencesSource: RemotePreferencesSource
) : AccountRemoteSource {
    override suspend fun fetchAccountWithGithubId(id: Int): NetworkResult<AccountDTO?> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Accounts)
                .select {
                    filter {
                        AccountDTO::githubId eq id
                    }
                }
                .decodeSingleOrNull<AccountDTO>()
        }

    override suspend fun createAccount(request: AccountCreateRequestDTO): NetworkResult<AccountDTO> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Accounts)
                .insert(request) { select() }
                .decodeSingle()
        }

    override suspend fun getAccount(id: Long): NetworkResult<AccountFullDTO> =
        safeSupabaseCall {
            val body = Json.encodeToJsonElement(mapOf("value_account_id" to id))
            supabase.postgrest
                .rpc(Supabase.Functions.GetAccountWithPoints, body.jsonObject) {}
                .decodeAs()
        }

    override suspend fun logout() {
        remotePreferencesSource.clear()
    }

}
