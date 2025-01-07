package com.bizilabs.streeek.lib.remote.sources.labels

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.GithubLabelDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

interface LabelRemoteSource {
    suspend fun fetchLabels(page: Int): NetworkResult<List<GithubLabelDTO>>
}

class LabelRemoteSourceImpl(
    private val client: HttpClient,
) : LabelRemoteSource {
    override suspend fun fetchLabels(page: Int): NetworkResult<List<GithubLabelDTO>> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.Labels.url)
                parameter("page", page)
            }
        }
}
