package com.bizilabs.streeek.lib.remote.sources.user

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.GithubUserDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get

interface UserRemoteSource {
    suspend fun getUser(): NetworkResult<GithubUserDTO>
}

class UserRemoteSourceImpl(
    private val client: HttpClient
) : UserRemoteSource {
    override suspend fun getUser(): NetworkResult<GithubUserDTO> = safeApiCall {
        client.get(GithubEndpoint.User.url)
    }
}
