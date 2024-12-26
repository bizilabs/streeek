package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamInvitationRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamInvitationRemoteSource
import kotlinx.coroutines.flow.firstOrNull

class TeamInvitationRepositoryImpl(
    private val remote: TeamInvitationRemoteSource,
    private val accountLocalSource: AccountLocalSource
) : TeamInvitationRepository {

    private suspend fun getAccountId() = accountLocalSource.account.firstOrNull()?.id

    override suspend fun createInvitation(
        teamId: Long,
        duration: Long
    ): DataResult<CreateTeamInvitationDomain> {
        val accountId = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remote.createInvitation(
            accountId = accountId,
            teamId = teamId,
            duration = duration
        ).asDataResult { it.toDomain() }
    }

    override suspend fun getInvitations(teamId: Long): DataResult<List<TeamInvitationDomain>> {
        val accountId = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remote.getInvitations(teamId = teamId, accountId = accountId)
            .asDataResult { list -> list.map { it.toDomain() } }
    }

    override suspend fun deleteInvitation(id: Long): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error(message = "No account found")
        return remote.deleteInvitation(accountId = accountId, invitationId = id).asDataResult { it }
    }

}
