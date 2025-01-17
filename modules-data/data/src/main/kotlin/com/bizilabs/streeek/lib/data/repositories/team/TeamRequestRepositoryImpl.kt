package com.bizilabs.streeek.lib.data.repositories.team

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.helper.AccountHelper
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.data.paging.genericPager
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSource
import kotlinx.coroutines.flow.Flow

class TeamRequestRepositoryImpl(
    private val remoteSource: TeamRequestRemoteSource,
    accountLocalSource: AccountLocalSource,
) : TeamRequestRepository, AccountHelper(source = accountLocalSource) {
    override suspend fun requestToJoinTeam(teamId: Long): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")
        return remoteSource
            .requestToJoinTeam(teamId = teamId, accountId = accountId)
            .asDataResult { it }
    }

    override fun getMyRequests(): Flow<PagingData<MemberAccountRequestDomain>> {
        return genericPager(
            getResults = { page ->
                val accountId = getAccountId() ?: 0L
                remoteSource.fetchAccountRequests(accountId = accountId, page = page)
            },
            mapper = { requests ->
                requests.map { it.toDomain() }
            }
        )
    }

    override fun getTeamRequests(teamId: Long): Flow<PagingData<TeamAccountJoinRequestDomain>> {
        return genericPager(
            getResults = { page ->
                remoteSource.fetchTeamRequests(teamId = teamId, page = page)
            },
            mapper = { requests ->
                requests.map { it.toDomain() }
            }
        )
    }

    override suspend fun cancelRequest(id: Long): DataResult<Boolean> {
        return remoteSource.delete(id = id).asDataResult { it }
    }

}
