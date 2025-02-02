package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val account: Flow<AccountDomain?>
    val isSyncingAccount: Flow<Boolean>

    suspend fun getAccountWithGithubId(id: Int): DataResult<AccountDomain?>

    suspend fun createAccount(
        githubId: Int,
        username: String,
        email: String,
        bio: String,
        avatarUrl: String,
    ): DataResult<AccountDomain>

    suspend fun getAccount(id: Long): DataResult<AccountDomain>

    suspend fun syncAccount(): DataResult<Boolean>

    suspend fun updateIsSyncingAccount(value: Boolean)

    suspend fun logout()

    suspend fun saveFCMToken(token: String): DataResult<Boolean>
}
