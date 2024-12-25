package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import kotlinx.coroutines.flow.firstOrNull

class TeamRepositoryImpl(
    private val remoteSource: TeamRemoteSource,
    private val accountLocalSource: AccountLocalSource
) : TeamRepository {
    override suspend fun createTeam(name: String, public: Boolean): DataResult<Long> {
        val account = accountLocalSource.account.firstOrNull()?.id ?: return DataResult.Error(
            message = "No account found"
        )
        val request = CreateTeamRequestDTO(name = name, public = public, account = account)
        return remoteSource.createTeam(request = request).asDataResult { it }
    }

    override suspend fun getTeam(id: Long, page: Int): DataResult<TeamWithMembersDomain> {
        val account = accountLocalSource.account.firstOrNull()?.id ?: return DataResult.Error(
            message = "No account found"
        )
        return remoteSource.fetchTeam(teamId = id, accountId = account, page = page)
            .asDataResult { it.toDomain() }
    }
}
