package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain

interface TeamInvitationRepository {

    suspend fun createInvitation(
        teamId: Long,
        duration: Long
    ): DataResult<CreateTeamInvitationDomain>

    suspend fun getInvitations(
        teamId: Long,
    ): DataResult<List<TeamInvitationDomain>>

    suspend fun deleteInvitation(id: Long) : DataResult<Boolean>

}
