package com.bizilabs.streeek.lib.remote.sources.issues

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.CommentDTO
import com.bizilabs.streeek.lib.remote.models.CreateIssueDTO
import com.bizilabs.streeek.lib.remote.models.EditIssueDTO
import com.bizilabs.streeek.lib.remote.models.GithubIssueDTO
import com.bizilabs.streeek.lib.remote.models.SearchGithubIssuesDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

interface IssuesRemoteSource {
    suspend fun createIssue(request: CreateIssueDTO): NetworkResult<GithubIssueDTO>

    suspend fun editIssue(request: EditIssueDTO): NetworkResult<GithubIssueDTO>

    suspend fun fetchUserIssues(
        username: String,
        page: Int,
    ): NetworkResult<List<GithubIssueDTO>>

    suspend fun fetchIssues(page: Int): NetworkResult<List<GithubIssueDTO>>

    suspend fun searchIssues(
        searchQuery: String,
        page: Int,
    ): NetworkResult<SearchGithubIssuesDTO>

    suspend fun fetchIssue(number: Long): NetworkResult<GithubIssueDTO>

    suspend fun fetchIssueComments(
        number: Long,
        page: Int,
    ): NetworkResult<List<CommentDTO>>
}

class IssuesRemoteSourceImpl(
    private val client: HttpClient,
) : IssuesRemoteSource {
    override suspend fun createIssue(request: CreateIssueDTO): NetworkResult<GithubIssueDTO> =
        safeApiCall {
            client.post {
                url(GithubEndpoint.Repository.Issues.url)
                setBody(body = request)
            }
        }

    override suspend fun editIssue(request: EditIssueDTO): NetworkResult<GithubIssueDTO> =
        safeApiCall {
            client.patch {
                url(GithubEndpoint.Repository.Issues(id = request.issue_number.toLong()).url)
                setBody(body = request)
            }
        }

    override suspend fun fetchUserIssues(
        username: String,
        page: Int,
    ): NetworkResult<List<GithubIssueDTO>> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.Issues.url)
                parameter("creator", username)
                parameter("page", page)
            }
        }

    override suspend fun fetchIssues(page: Int): NetworkResult<List<GithubIssueDTO>> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.Issues.url)
                parameter("page", page)
            }
        }

    override suspend fun searchIssues(
        searchQuery: String,
        page: Int,
    ): NetworkResult<SearchGithubIssuesDTO> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.SearchIssues.url)
                parameter(
                    "q",
                    "repo:${GithubEndpoint.Repository.SearchIssues.repoRoute} $searchQuery in:title OR $searchQuery in:body",
                ) // in:title OR CTA in:body if you need to search in Both
            }
        }

    override suspend fun fetchIssue(number: Long): NetworkResult<GithubIssueDTO> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.Issues(id = number).url)
            }
        }

    override suspend fun fetchIssueComments(
        number: Long,
        page: Int,
    ): NetworkResult<List<CommentDTO>> =
        safeApiCall {
            client.get {
                url(GithubEndpoint.Repository.Issues(id = number).Comments().url)
                parameter("page", page)
            }
        }
}
