package com.bizilabs.streeek.lib.data.repositories.team

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.helper.AccountHelper
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.data.paging.genericPager
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.AccountTeamInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain
import com.bizilabs.streeek.lib.domain.models.team.DeleteAccountInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain
import com.bizilabs.streeek.lib.domain.repositories.team.TeamMemberInvitationRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamMemberInvitationRemoteSource
import kotlinx.coroutines.flow.Flow

class TeamMemberInvitationRepositoryImpl(
    private val invitationRemoteSource: TeamMemberInvitationRemoteSource,
    accountLocalSource: AccountLocalSource,
) : TeamMemberInvitationRepository, AccountHelper(source = accountLocalSource) {
    override fun getAccountsNotInTeam(teamId: Long): Flow<PagingData<AccountsNotInTeamDomain>> =
        genericPager(
            getResults = { page ->
                invitationRemoteSource.getAccountsNotInTeam(teamId = teamId, page = page)
            },
            mapper = { result ->
                result.map { it.toDomain() }
            },
        )

    override fun searchForAccountNotInTeam(
        searchParam: String,
        teamId: Long,
    ): Flow<PagingData<AccountsNotInTeamDomain>> =
        genericPager(
            getResults = { page ->
                invitationRemoteSource.searchForAccountNotInTeam(
                    searchParam = searchParam,
                    page = page,
                    teamId = teamId,
                )
            },
            mapper = { result ->
                result.map { it.toDomain() }
            },
        )

    override fun getTeamAccountInvites(teamId: Long): Flow<PagingData<TeamAccountInvitesDomain>> =
        genericPager(
            getResults = { page ->
                invitationRemoteSource.getTeamAccountInvites(teamId = teamId, page = page)
            },
            mapper = { result ->
                result.map { it.toDomain() }
            },
        )

    override suspend fun sendAccountInvitation(
        teamId: Long,
        inviteeId: Long,
    ): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")

        return invitationRemoteSource.sendAccountInvitation(
            teamId = teamId,
            adminId = accountId,
            inviteeId = inviteeId,
        ).asDataResult { it }
    }

    override suspend fun sendMultipleAccountInvitation(
        teamId: Long,
        inviteeIds: List<Long>,
    ): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")

        return invitationRemoteSource.sendMultipleAccountInvitation(
            teamId = teamId,
            adminId = accountId,
            inviteeIds = inviteeIds,
        ).asDataResult { it }
    }

    override suspend fun deleteAccountInvitation(inviteId: Long): DataResult<DeleteAccountInvitationDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")

        return invitationRemoteSource.deleteAccountInvitation(
            inviteId = inviteId,
            adminId = accountId,
        ).asDataResult { it.toDomain() }
    }

    override suspend fun processAccountInvite(
        inviteId: Long,
        status: String,
    ): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")

        return invitationRemoteSource.processAccountInvite(
            inviteId = inviteId,
            inviteeId = accountId,
            status = status,
        ).asDataResult { it }
    }

    override suspend fun processAccountMultipleInvite(
        inviteIds: List<Long>,
        status: String,
    ): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("couldn't find account id")

        return invitationRemoteSource.processAccountMultipleInvite(
            inviteIds = inviteIds,
            inviteeId = accountId,
            status = status,
        ).asDataResult { it }
    }

    override fun getAllAccountInvites(): Flow<PagingData<AccountTeamInvitesDomain>> =
        genericPager(
            getResults = { page ->
                val accountId = getAccountId() ?: throw Exception("couldn't find account id")
                invitationRemoteSource.getAllAccountInvites(accountId = accountId, page = page)
            },
            mapper = { result ->
                result.map { it.toDomain() }
            },
        )
}
