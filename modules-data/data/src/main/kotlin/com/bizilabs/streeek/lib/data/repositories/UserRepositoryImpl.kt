package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSource

class UserRepositoryImpl(
    private val remote: UserRemoteSource
) : UserRepository {
    override suspend fun getUser(): DataResult<UserDomain> {
        return when (val result = remote.getUser()) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getUserEvents(username: String): DataResult<List<UserEventDomain>> {
        return when(val result = remote.getUserEvents(username = username)) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val data = result.data.map { it.toDomain() }
                DataResult.Success(data = data)
            }
        }
    }
}
