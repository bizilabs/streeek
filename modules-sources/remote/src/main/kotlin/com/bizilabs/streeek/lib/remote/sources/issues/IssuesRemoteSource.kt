package com.bizilabs.streeek.lib.remote.sources.issues

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.CreateIssueDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

interface IssuesRemoteSource {
    suspend fun createIssue(createIssueDto: CreateIssueDto): NetworkResult<Boolean>
}

class IssuesRemoteSourceImpl(
    private val client: HttpClient
) : IssuesRemoteSource {
    override suspend fun createIssue(createIssueDto: CreateIssueDto): NetworkResult<Boolean> =
        safeApiCall {
            client.post {
                url(GithubEndpoint.Issues.url)
                setBody(body = createIssueDto)
            }
        }
}
