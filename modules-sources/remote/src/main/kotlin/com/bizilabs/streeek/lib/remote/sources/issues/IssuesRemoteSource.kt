package com.bizilabs.streeek.lib.remote.sources.issues

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.CreateIssueDTO
import com.bizilabs.streeek.lib.remote.models.GithubIssueDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

interface IssuesRemoteSource {
    suspend fun createIssue(createIssueDto: CreateIssueDTO): NetworkResult<GithubIssueDTO>

    suspend fun getIssues(username: String): NetworkResult<List<GithubIssueDTO>>
}

class IssuesRemoteSourceImpl(
    private val client: HttpClient,
) : IssuesRemoteSource {
    override suspend fun createIssue(createIssueDto: CreateIssueDTO): NetworkResult<GithubIssueDTO> =
        safeApiCall {
            client.post {
                url(GithubEndpoint.Issues.url)
                setBody(body = createIssueDto)
            }
        }

    override suspend fun getIssues(username: String): NetworkResult<List<GithubIssueDTO>> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Issues.url)
                parameter("creator", username)
            }
        }
}
