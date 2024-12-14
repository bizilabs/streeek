package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import java.util.Date

class AccountRepositoryImpl(
    private val remote: AccountRemoteSource
) : AccountRepository {
    override suspend fun getAccountWithGithubId(id: Int): DataResult<AccountDomain?> {
        return when (val result = remote.fetchAccountWithGithubId(id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(result.data?.toDomain())
        }
    }

    override suspend fun createAccount(
        githubId: Int,
        username: String,
        email: String,
        bio: String,
        avatarUrl: String
    ): DataResult<AccountDomain> {
        val request = AccountCreateRequestDTO(
            githubId = githubId,
            username = username,
            email = email,
            bio = bio,
            avatarUrl = avatarUrl,
            createdAt = Date().asString(DateFormats.ISO_8601) ?: "",
            updatedAt = Date().asString(DateFormats.ISO_8601) ?: ""
        )
        return when (val result = remote.createAccount(request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(result.data.toDomain())
        }
    }

}