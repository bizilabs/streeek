package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface ContributionRepository {
    val contributions: Flow<List<ContributionDomain>>
    val dates: Flow<List<LocalDateTime>>

    val lastSync: Flow<Long?>

    suspend fun updateLastSync(timeInMillis: Long)

    suspend fun getEvent(id: String): DataResult<UserEventDomain>

    suspend fun getEvents(page: Int): DataResult<List<UserEventDomain>>

    suspend fun getContributionWithGithubEventId(githubEventId: String): DataResult<ContributionDomain?>

    suspend fun getContribution(id: Long): DataResult<ContributionDomain>

    suspend fun getContributions(page: Int): DataResult<List<ContributionDomain>>

    suspend fun saveContribution(event: UserEventDomain): DataResult<ContributionDomain>

    suspend fun saveContribution(events: List<UserEventDomain>): DataResult<List<ContributionDomain>>

    fun getLocalContributionsByDate(date: LocalDate): Flow<List<ContributionDomain>>

    suspend fun getContributionsLocally(id: Long): DataResult<ContributionDomain?>

    suspend fun saveContributionLocally(contribution: ContributionDomain): DataResult<Boolean>

    suspend fun saveContributionLocally(contributions: List<ContributionDomain>): DataResult<Boolean>

    suspend fun updateContributionLocally(contribution: ContributionDomain): DataResult<ContributionDomain>

    suspend fun updateContributionLocally(contributions: List<ContributionDomain>): DataResult<Boolean>

    suspend fun deleteContributionLocally(contribution: ContributionDomain): DataResult<Boolean>
}
