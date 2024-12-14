package com.bizilabs.streeek.lib.remote.sources.account

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

interface AccountRemoteSource {
    suspend fun fetchAccountWithGithubId(id: Int): NetworkResult<AccountDTO?>
    suspend fun createAccount(request: AccountCreateRequestDTO): NetworkResult<AccountDTO>
}

class AccountRemoteSourceImpl(
    private val supabase: SupabaseClient
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
                .insert(request) {
                    select()
                }
                .decodeSingle()
        }
}