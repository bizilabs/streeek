package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock
import timber.log.Timber

class AccountRepositoryImpl(
    private val remote: AccountRemoteSource,
    private val local: AccountLocalSource,
    private val contributionsLocalSource: ContributionsLocalSource
) : AccountRepository {

    override val account: Flow<AccountDomain?>
        get() = local.account.mapLatest { it?.toDomain() }

    override suspend fun getAccountWithGithubId(id: Int): DataResult<AccountDomain?> {
        return when (val result = remote.fetchAccountWithGithubId(id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val account = result.data?.toDomain()
                account?.let { local.updateAccount(account = it.toCache()) }
                if (account != null)
                    getAccount(id = account.id)
                else
                    DataResult.Success(result.data?.toDomain())
            }
        }
    }

    override suspend fun getAccount(id: Long): DataResult<AccountDomain> {
        return when (val result = remote.getAccount(id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(result.data.toDomain())
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
            createdAt = Clock.System.now().asString(DateFormats.ISO_8601_Z) ?: "",
            updatedAt = Clock.System.now().asString(DateFormats.ISO_8601_Z) ?: ""
        )
        return when (val result = remote.createAccount(request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val account = result.data.toDomain()
                local.updateAccount(account = account.toCache())
                DataResult.Success(result.data.toDomain())
            }
        }
    }

    override suspend fun syncAccount(): DataResult<Boolean> {
        val id = account.first()?.id ?: return DataResult.Error("Account not found")
        return when (val result = remote.getAccount(id = id)) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val account = result.data.toDomain()
                local.updateAccount(account = account.toCache())
                DataResult.Success(true)
            }
        }
    }

    override suspend fun logout() {
        remote.logout()
        local.logout()
        contributionsLocalSource.deleteAll()
    }

}