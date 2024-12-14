package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain

interface AccountRepository {
    suspend fun getAccountWithGithubId(id: Int): DataResult<AccountDomain?>
    suspend fun createAccount(
        githubId: Int,
        username: String,
        email: String,
        bio: String,
        avatarUrl: String
    ): DataResult<AccountDomain>
}
