package com.bizilabs.streeek.lib.domain.repositories

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    val isSyncing: Flow<Boolean>

    val teamId: Flow<Long?>

    val teams: Flow<Map<Long, TeamDetailsDomain>>

    fun getPagedData(team: TeamDetailsDomain): Flow<PagingData<TeamMemberDomain>>

    suspend fun updateIsSyncing(isSyncing: Boolean)

    suspend fun createTeam(
        name: String,
        public: Boolean,
    ): DataResult<Long>

    suspend fun updateTeam(
        teamId: Long,
        name: String,
        public: Boolean,
    ): DataResult<Boolean>

    suspend fun getAccountTeams(): DataResult<List<TeamWithDetailDomain>>

    fun getTeamsAndMembers(): Flow<PagingData<TeamAndMembersDomain>>

    suspend fun getTeam(
        id: Long,
        page: Int,
    ): DataResult<TeamWithMembersDomain>

    suspend fun joinTeam(token: String): DataResult<JoinTeamInvitationDomain>

    suspend fun leaveTeam(teamId: Long): DataResult<Boolean>

    suspend fun setSelectedTeam(team: TeamDetailsDomain)

    suspend fun getTeamLocally(id: Long): DataResult<TeamDetailsDomain>

    suspend fun addTeamLocally(team: TeamDetailsDomain)

    suspend fun updateTeamLocally(team: TeamDetailsDomain)

    suspend fun deleteTeamLocally(team: TeamDetailsDomain)

    suspend fun deleteTeam(teamId: Long): DataResult<Boolean>
}
