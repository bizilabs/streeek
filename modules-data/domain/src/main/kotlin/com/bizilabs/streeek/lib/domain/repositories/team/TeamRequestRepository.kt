package com.bizilabs.streeek.lib.domain.repositories.team

import com.bizilabs.streeek.lib.domain.helpers.DataResult

interface TeamRequestRepository {
    suspend fun requestToJoinTeam(
        teamId: Long,
    ): DataResult<Boolean>
}
