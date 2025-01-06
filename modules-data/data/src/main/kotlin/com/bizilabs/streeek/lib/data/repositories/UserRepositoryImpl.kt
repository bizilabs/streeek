package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSource
import kotlinx.coroutines.flow.first

class UserRepositoryImpl(
    private val remote: UserRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : UserRepository {
    override suspend fun getUser(): DataResult<UserDomain> {
        return when (val result = remote.getUser()) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getUserEvents(): DataResult<List<UserEventDomain>> {
        val username = accountLocalSource.account.first()?.username ?: ""
        return when (val result = remote.getUserEvents(username = username)) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val data = result.data.map { it.toDomain() }
                DataResult.Success(data = data)
            }
        }
    }
}
