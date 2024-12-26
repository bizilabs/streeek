package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.UpdateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import kotlinx.coroutines.flow.firstOrNull

class TeamRepositoryImpl(
    private val remoteSource: TeamRemoteSource,
    private val accountLocalSource: AccountLocalSource
) : TeamRepository {

    private suspend fun getAccountId() = accountLocalSource.account.firstOrNull()?.id

    override suspend fun createTeam(name: String, public: Boolean): DataResult<Long> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        val request = CreateTeamRequestDTO(name = name, public = public, account = account)
        return remoteSource.createTeam(request = request).asDataResult { it }
    }

    override suspend fun updateTeam(
        teamId: Long,
        name: String,
        public: Boolean
    ): DataResult<Boolean> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        val request =
            UpdateTeamRequestDTO(teamId = teamId, name = name, public = public, accountId = account)
        return remoteSource.updateTeam(request = request).asDataResult { it }
    }

    override suspend fun getAccountTeams(): DataResult<List<TeamWithDetailDomain>> {
        val accountId = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.getAccountTeams(accountId = accountId)
            .asDataResult { list -> list.map { it.toDomain() } }
    }

    override suspend fun getTeam(id: Long, page: Int): DataResult<TeamWithMembersDomain> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.fetchTeam(teamId = id, accountId = account, page = page)
            .asDataResult { it.toDomain() }
    }

    override suspend fun joinTeam(code: String): DataResult<JoinTeamInvitationDomain> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.joinTeam(accountId = account, teamId = code.toLong())
            .asDataResult { it.toDomain() }
    }

    override suspend fun leaveTeam(teamId: Long): DataResult<Boolean> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.leaveTeam(accountId = account, teamId = teamId).asDataResult { it }
    }

}
