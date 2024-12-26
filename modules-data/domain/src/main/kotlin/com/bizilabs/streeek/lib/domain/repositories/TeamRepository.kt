package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain

interface TeamRepository {
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

    suspend fun getTeam(id: Long, page: Int) : DataResult<TeamWithMembersDomain>

    suspend fun joinTeam(code: String) : DataResult<JoinTeamInvitationDomain>

    suspend fun leaveTeam(teamId: Long) : DataResult<Boolean>

}
