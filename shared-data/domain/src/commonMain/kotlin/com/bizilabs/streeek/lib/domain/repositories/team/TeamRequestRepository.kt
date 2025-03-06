package com.bizilabs.streeek.lib.domain.repositories.team

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import kotlinx.coroutines.flow.Flow

interface TeamRequestRepository {
    suspend fun requestToJoinTeam(teamId: Long): DataResult<Boolean>

    fun getMyRequests(): Flow<PagingData<MemberAccountRequestDomain>>

    fun getTeamRequests(teamId: Long): Flow<PagingData<TeamAccountJoinRequestDomain>>

    suspend fun cancelRequest(id: Long): DataResult<Boolean>

    suspend fun processSingleRequest(
        teamId: Long,
        requestId: Long,
        status: String,
    ): DataResult<Boolean>

    suspend fun processMultipleRequest(
        teamId: Long,
        requestIds: List<Long>,
        status: String,
    ): DataResult<Boolean>
}
