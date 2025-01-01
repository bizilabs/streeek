package com.bizilabs.streeek.lib.data.repositories

import androidx.work.Data
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDTO
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.asJson
import com.bizilabs.streeek.lib.remote.models.CreateContributionDTO
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

class ContributionRepositoryImpl(
    private val remote: ContributionsRemoteSource,
    private val local: ContributionsLocalSource,
    private val accountLocalSource: AccountLocalSource,
) : ContributionRepository {

    override val contributions: Flow<List<ContributionDomain>>
        get() = local.contributions.mapLatest { list -> list.map { it.toDomain() } }

    override val dates: Flow<List<LocalDateTime>>
        get() = local.dates.map { list -> list.map { it.toDomain().githubEventDate } }

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
        return when (val result =
            remote.fetchContributions(accountId = getAccountId(), page = page)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    private fun UserEventDomain.toCreateContribution(account: AccountDomain) =
        CreateContributionDTO(
            accountId = account.id,
            githubEventId = id,
            githubEventType = type,
            githubEventDate = createdAt.asString(DateFormats.ISO_8601_Z) ?: "",
            githubEventRepo = repo.toDTO().asJson(),
            githubEventActor = actor.toDTO().asJson(),
            githubEventPayload = payload.toDTO().asJson(),
            points = payload.getPoints(account = account)
        )

    override fun getLocalContributionsByDate(date: LocalDate): Flow<List<ContributionDomain>> {
        Timber.d("Dately -> $date")
        return local.getByDate(date = date.toString())
            .mapLatest { list -> list.map { it.toDomain() } }
    }

    override suspend fun getContributionsLocally(id: Long): DataResult<ContributionDomain?> {
        return when (val result = local.get(id = id)) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data?.toDomain())
        }
    }

    override suspend fun saveContribution(event: UserEventDomain): DataResult<ContributionDomain> {
        val account =
            getAccount()?.toDomain() ?: return DataResult.Error("Couldn't get logged in account")
        val request = event.toCreateContribution(account = account)
        return when (val result = remote.saveContribution(request = request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun saveContribution(events: List<UserEventDomain>): DataResult<List<ContributionDomain>> {
        val account =
            getAccount()?.toDomain() ?: return DataResult.Error("Couldn't get logged in account")
        val requests = events.map { it.toCreateContribution(account = account) }
        return when (val result = remote.saveContribution(requests = requests)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    override suspend fun saveContributionLocally(contribution: ContributionDomain): DataResult<Boolean> {
        val result = local.create(contribution = contribution.toCache())
        return when (result) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data)
        }
    }

    override suspend fun saveContributionLocally(contributions: List<ContributionDomain>): DataResult<Boolean> {
        return when (val result =
            local.create(contributions = contributions.map { it.toCache() })) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data)
        }
    }

    override suspend fun deleteContributionLocally(contribution: ContributionDomain): DataResult<Boolean> {
        val result = local.delete(id = contribution.id)
        return when (result) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data)
        }
    }

    override suspend fun updateContributionLocally(contribution: ContributionDomain): DataResult<ContributionDomain> {
        val result = local.update(contribution = contribution.toCache())
        return when (result) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun updateContributionLocally(contributions: List<ContributionDomain>): DataResult<Boolean> {
        return when (val result =
            local.update(contributions = contributions.map { it.toCache() })) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(data = result.data)
        }
    }

}
