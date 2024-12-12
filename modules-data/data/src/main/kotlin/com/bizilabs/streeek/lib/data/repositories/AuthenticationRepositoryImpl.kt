package com.bizilabs.streeek.lib.data.repositories

import android.content.Intent
import android.net.Uri
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSource
import kotlinx.coroutines.flow.Flow

class AuthenticationRepositoryImpl(
    private val remote: AuthenticationRemoteSource
) : AuthenticationRepository {
    override val authenticated: Flow<Boolean>
        get() = remote.authenticated
    override suspend fun getAuthenticationIntent(): Intent {
        return remote.getAuthenticationIntent()
    }
    override suspend fun getAuthenticationToken(uri: Uri): DataResult<String> {
        return when(val result = remote.getAuthenticationToken(uri = uri)){
            is NetworkResult.Failure -> {
                val message = result.exception.message ?: "Failed getting authentication token"
                DataResult.Error(message = message)
            }
            is NetworkResult.Success -> {
                val accessToken = result.data.accessToken
                remote.updateAccessToken(token = accessToken)
                DataResult.Success(data = result.data.accessToken)
            }
        }
    }
}
