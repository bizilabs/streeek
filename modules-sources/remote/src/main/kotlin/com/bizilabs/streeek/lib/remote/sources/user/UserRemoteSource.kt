package com.bizilabs.streeek.lib.remote.sources.user

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.GithubUserDTO
import com.bizilabs.streeek.lib.remote.models.GithubUserEventDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url

interface UserRemoteSource {
    suspend fun getUser(): NetworkResult<GithubUserDTO>
    suspend fun getUserEvents(username: String): NetworkResult<List<GithubUserEventDTO>>
}

class UserRemoteSourceImpl(
    private val client: HttpClient
) : UserRemoteSource {
    override suspend fun getUser(): NetworkResult<GithubUserDTO> = safeApiCall {
        client.get(GithubEndpoint.User.url)
    }

    override suspend fun getUserEvents(username: String): NetworkResult<List<GithubUserEventDTO>> = safeApiCall {
        client.get {
            url(GithubEndpoint.Events(username = username).url)
        }
    }
}
