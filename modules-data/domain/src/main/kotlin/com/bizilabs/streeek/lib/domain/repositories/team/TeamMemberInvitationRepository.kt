package com.bizilabs.streeek.lib.domain.repositories.team

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.team.AccountTeamInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain
import com.bizilabs.streeek.lib.domain.models.team.DeleteAccountInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain
import kotlinx.coroutines.flow.Flow

interface TeamMemberInvitationRepository {
    fun getAccountsNotInTeam(teamId: Long): Flow<PagingData<AccountsNotInTeamDomain>>

    fun searchForAccountNotInTeam(
        searchParam: String,
        teamId: Long,
    ): Flow<PagingData<AccountsNotInTeamDomain>>

    fun getTeamAccountInvites(teamId: Long): Flow<PagingData<TeamAccountInvitesDomain>>

    suspend fun sendAccountInvitation(
        teamId: Long,
        inviteeId: Long,
    ): DataResult<Boolean>

    suspend fun sendMultipleAccountInvitation(
        teamId: Long,
        inviteeIds: List<Long>,
    ): DataResult<Boolean>

    suspend fun deleteAccountInvitation(inviteId: Long): DataResult<DeleteAccountInvitationDomain>

    suspend fun processAccountInvite(
        inviteId: Long,
        status: String,
    ): DataResult<Boolean>

    suspend fun processAccountMultipleInvite(
        inviteIds: List<Long>,
        status: String,
    ): DataResult<Boolean>

    fun getAllAccountInvites(): Flow<PagingData<AccountTeamInvitesDomain>>
}
