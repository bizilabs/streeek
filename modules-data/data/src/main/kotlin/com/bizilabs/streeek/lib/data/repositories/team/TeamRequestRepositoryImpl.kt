package com.bizilabs.streeek.lib.data.repositories.team

import com.bizilabs.streeek.lib.data.helper.AccountHelper
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSource

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
}
