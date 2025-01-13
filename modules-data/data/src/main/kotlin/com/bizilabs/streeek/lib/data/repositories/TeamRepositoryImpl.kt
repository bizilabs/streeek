package com.bizilabs.streeek.lib.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.PagingHelpers
import com.bizilabs.streeek.lib.data.paging.TeamsPagingSource
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.team.TeamLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.UpdateTeamRequestDTO
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest

class TeamRepositoryImpl(
    private val remoteSource: TeamRemoteSource,
    private val localSource: TeamLocalSource,
    private val accountLocalSource: AccountLocalSource,
) : TeamRepository {
    override val isSyncing: Flow<Boolean>
        get() = localSource.isSyncing

    override val teamId: Flow<Long?>
        get() = localSource.teamId

    override val teams: Flow<Map<Long, TeamDetailsDomain>>
        get() =
            localSource.teams.mapLatest { map ->
                map.mapValues { it.value.toDomain() }
            }

    override fun getPagedData(team: TeamDetailsDomain): Flow<PagingData<TeamMemberDomain>> {
        return Pager(
            config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                TeamsPagingSource(
                    team = team,
                    accountLocalSource = accountLocalSource,
                    teamLocalSource = localSource,
                    teamRemoteSource = remoteSource,
                )
            },
        ).flow
    }

    override suspend fun updateIsSyncing(isSyncing: Boolean) {
        localSource.updateIsSyncing(isSyncing = isSyncing)
    }

    private suspend fun getAccountId() = accountLocalSource.account.firstOrNull()?.id

    override suspend fun createTeam(
        name: String,
        public: Boolean,
    ): DataResult<Long> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        val request = CreateTeamRequestDTO(name = name, public = public, account = account)
        return remoteSource.createTeam(request = request).asDataResult { it }
    }

    override suspend fun updateTeam(
        teamId: Long,
        name: String,
        public: Boolean,
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

    override suspend fun getTeam(
        id: Long,
        page: Int,
    ): DataResult<TeamWithMembersDomain> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.fetchTeam(teamId = id, accountId = account, page = page)
            .asDataResult { it.toDomain() }
    }

    override suspend fun joinTeam(token: String): DataResult<JoinTeamInvitationDomain> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.joinTeam(accountId = account, token = token)
            .asDataResult { it.toDomain() }
    }

    override suspend fun leaveTeam(teamId: Long): DataResult<Boolean> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remoteSource.leaveTeam(accountId = account, teamId = teamId).asDataResult { it }
    }

    override suspend fun setSelectedTeam(team: TeamDetailsDomain) {
        localSource.setSelected(team.toCache())
    }

    override suspend fun getTeamLocally(id: Long): DataResult<TeamDetailsDomain> {
        return when (val result = localSource.get(id = id)) {
            is LocalResult.Error -> DataResult.Error(result.message)
            is LocalResult.Success -> DataResult.Success(result.data.toDomain())
        }
    }

    override suspend fun addTeamLocally(team: TeamDetailsDomain) {
        localSource.add(team.toCache())
    }

    override suspend fun updateTeamLocally(team: TeamDetailsDomain) {
        localSource.update(team.toCache())
    }

    override suspend fun deleteTeamLocally(team: TeamDetailsDomain) {
        localSource.delete(team.toCache())
    }

    override suspend fun deleteTeam(teamId: Long): DataResult<Boolean> {
        val account = getAccountId() ?: return DataResult.Error(message = "No account found")
        return when (val result = remoteSource.deleteTeam(accountId = account, teamId = teamId)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                localSource.delete(teamId)
                DataResult.Success(result.data)
            }
        }
    }
}
