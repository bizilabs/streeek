package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toDTO
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.asJson
import com.bizilabs.streeek.lib.remote.models.CreateContributionDTO
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSource
import kotlinx.coroutines.flow.first

class ContributionRepositoryImpl(
    private val remote: ContributionsRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : ContributionRepository {

    private suspend fun getAccount() = accountLocalSource.account.first()

    private suspend fun getAccountId() = getAccount()?.id?.toLong() ?: 0L

    private suspend fun getUsername() = getAccount()?.username ?: ""

    override suspend fun getEvent(id: String): DataResult<UserEventDomain> {
        return when (val result = remote.fetchEvent(username = getUsername(), id = id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getEvents(page: Int): DataResult<List<UserEventDomain>> {
        return when (val result = remote.fetchEvents(username = getUsername(), page = page)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    override suspend fun getContributionWithGithubEventId(githubEventId: String): DataResult<ContributionDomain?> {
        return when (val result =
            remote.fetchContributionWithGithubEventId(githubEventId = githubEventId)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data?.toDomain())
        }
    }

    override suspend fun getContribution(id: Long): DataResult<ContributionDomain> {
        return when (val result = remote.fetchContribution(id = id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getContributions(page: Int): DataResult<List<ContributionDomain>> {
        return when (val result = remote.fetchContributions(page = page)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    override suspend fun saveContribution(event: UserEventDomain): DataResult<ContributionDomain> {
        val request = CreateContributionDTO(
            accountId = getAccountId(),
            githubEventId = event.id,
            githubEventType = event.type,
            githubEventDate = event.createdAt.asString(DateFormats.ISO_8601) ?: "",
            githubEventRepo = event.repo.toDTO().asJson(),
            githubEventActor = event.actor.toDTO().asJson(),
            githubEventPayload = event.payload.toDTO().asJson(),
            points = event.payload.points
        )
        return when (val result = remote.saveContribution(request = request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }
}