package com.bizilabs.streeek.lib.domain.repositories.team

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import kotlinx.coroutines.flow.Flow

interface TeamRequestRepository {
    suspend fun requestToJoinTeam(
        teamId: Long,
    ): DataResult<Boolean>

    fun getMyRequests(): Flow<PagingData<MemberAccountRequestDomain>>
}
