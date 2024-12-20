package com.bizilabs.streeek.lib.remote.sources.contributions

import com.bizilabs.streeek.lib.remote.helpers.GithubEndpoint
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.ContributionDTO
import com.bizilabs.streeek.lib.remote.models.CreateContributionDTO
import com.bizilabs.streeek.lib.remote.models.GithubUserEventDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.append
import io.ktor.http.parameters
import io.ktor.http.plus

interface ContributionsRemoteSource {
    suspend fun fetchEvent(username: String, id: String): NetworkResult<GithubUserEventDTO>
    suspend fun fetchEvents(username: String, page: Int): NetworkResult<List<GithubUserEventDTO>>

    suspend fun fetchContribution(id: Long): NetworkResult<ContributionDTO>
    suspend fun fetchContributions(accountId: Long, page: Int): NetworkResult<List<ContributionDTO>>

    suspend fun fetchContributionWithGithubEventId(githubEventId: String): NetworkResult<ContributionDTO?>
    suspend fun saveContribution(request: CreateContributionDTO): NetworkResult<ContributionDTO>
    suspend fun saveContribution(requests: List<CreateContributionDTO>): NetworkResult<List<ContributionDTO>>
}

class ContributionsRemoteSourceImpl(
    private val supabase: SupabaseClient,
    private val client: HttpClient
) : ContributionsRemoteSource {
    override suspend fun fetchEvent(
        username: String,
        id: String
    ): NetworkResult<GithubUserEventDTO> = safeApiCall {
        client.get {
            url(GithubEndpoint.Event(username = username, id = id).url)
        }
    }

    override suspend fun fetchEvents(
        username: String,
        page: Int
    ): NetworkResult<List<GithubUserEventDTO>> = safeApiCall {
        client.get(GithubEndpoint.Events(username = username).url) {
            url {
                parameters.append("page", "$page")
            }
        }
    }

    override suspend fun fetchContribution(id: Long): NetworkResult<ContributionDTO> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Contributions)
                .select { filter { ContributionDTO::id eq id } }
                .decodeSingle()
        }

    override suspend fun fetchContributions(
        accountId: Long,
        page: Int
    ): NetworkResult<List<ContributionDTO>> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Contributions)
                .select {
                    filter {
                        ContributionDTO::accountId eq accountId
                    }
                    order(ContributionDTO.Columns.AccountId, order = Order.DESCENDING)
                }
                .decodeList()
        }

    override suspend fun fetchContributionWithGithubEventId(githubEventId: String): NetworkResult<ContributionDTO?> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Contributions)
                .select { filter { ContributionDTO::githubEventId eq githubEventId } }
                .decodeSingleOrNull()
        }

    override suspend fun saveContribution(request: CreateContributionDTO): NetworkResult<ContributionDTO> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Contributions)
                .insert(request) { select() }
                .decodeSingle()
        }

    override suspend fun saveContribution(requests: List<CreateContributionDTO>): NetworkResult<List<ContributionDTO>> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Contributions)
                .insert(requests) { select() }
                .decodeList()
        }

}
