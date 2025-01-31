package com.bizilabs.streeek.lib.remote.sources.team.invitations

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.AccountTeamInvitesDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.AccountsNotInTeamDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.DeleteAccountInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.DeleteAccountInvitationRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.GetAccountsDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.GetAllAccountInvitesDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.ProcessAccountInviteDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.ProcessAccountMultipleInvitesDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.SearchAccountNotInTeamDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.SendAccountInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.SendMultipleAccountInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.TeamAccountInvitesDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface TeamMemberInvitationRemoteSource {
    suspend fun getAccountsNotInTeam(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<AccountsNotInTeamDTO>>

    suspend fun searchForAccountNotInTeam(
        searchParam: String,
        teamId: Long,
        page: Int,
    ): NetworkResult<List<AccountsNotInTeamDTO>>

    suspend fun getTeamAccountInvites(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<TeamAccountInvitesDTO>>

    suspend fun sendAccountInvitation(
        teamId: Long,
        adminId: Long,
        inviteeId: Long,
    ): NetworkResult<Boolean>

    suspend fun sendMultipleAccountInvitation(
        teamId: Long,
        adminId: Long,
        inviteeIds: List<Long>,
    ): NetworkResult<Boolean>

    suspend fun deleteAccountInvitation(
        inviteId: Long,
        adminId: Long,
    ): NetworkResult<DeleteAccountInvitationDTO>

    suspend fun processAccountInvite(
        inviteId: Long,
        inviteeId: Long,
        status: String,
    ): NetworkResult<Boolean>

    suspend fun processAccountMultipleInvite(
        inviteIds: List<Long>,
        inviteeId: Long,
        status: String,
    ): NetworkResult<Boolean>

    suspend fun getAllAccountInvites(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<AccountTeamInvitesDTO>>
}

class TeamMemberInvitationRemoteSourceImpl(
    private val supabase: SupabaseClient,
) : TeamMemberInvitationRemoteSource {
    override suspend fun getAccountsNotInTeam(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<AccountsNotInTeamDTO>> =
        safeSupabaseCall {
            val params = GetAccountsDTO(teamId = teamId, page = page)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.GETACCOUNTSNOTINTEAM,
                    parameters = params.asJsonObject(),
                )
                .decodeList()
        }

    override suspend fun searchForAccountNotInTeam(
        searchParam: String,
        teamId: Long,
        page: Int,
    ): NetworkResult<List<AccountsNotInTeamDTO>> =
        safeSupabaseCall {
            val params =
                SearchAccountNotInTeamDTO(searchParam = searchParam, teamId = teamId, page = page)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.SEARCHACCOUNTSNOTINTEAM,
                    parameters = params.asJsonObject(),
                )
                .decodeList()
        }

    override suspend fun getTeamAccountInvites(
        teamId: Long,
        page: Int,
    ): NetworkResult<List<TeamAccountInvitesDTO>> =
        safeSupabaseCall {
            val params = GetAccountsDTO(teamId = teamId, page = page)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.GETTEAMACCOUNTINVITES,
                    parameters = params.asJsonObject(),
                )
                .decodeList()
        }

    override suspend fun sendAccountInvitation(
        teamId: Long,
        adminId: Long,
        inviteeId: Long,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val params =
                SendAccountInvitationDTO(
                    teamId = teamId,
                    adminId = adminId,
                    inviteeId = inviteeId,
                )
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.SENDMEMBERINVITE,
                    parameters = params.asJsonObject(),
                )

            true
        }

    override suspend fun sendMultipleAccountInvitation(
        teamId: Long,
        adminId: Long,
        inviteeIds: List<Long>,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val params =
                SendMultipleAccountInvitationDTO(
                    teamId = teamId,
                    adminId = adminId,
                    inviteeIds = inviteeIds,
                )
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.SENDMULTIPLEMEMBERINVITE,
                    parameters = params.asJsonObject(),
                )

            true
        }

    override suspend fun deleteAccountInvitation(
        inviteId: Long,
        adminId: Long,
    ): NetworkResult<DeleteAccountInvitationDTO> =
        safeSupabaseCall {
            val params = DeleteAccountInvitationRequestDTO(inviteId = inviteId, adminId = adminId)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.DELETEINVITE,
                    parameters = params.asJsonObject(),
                )
                .decodeAs()
        }

    override suspend fun processAccountInvite(
        inviteId: Long,
        inviteeId: Long,
        status: String,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val params =
                ProcessAccountInviteDTO(
                    inviteId = inviteId,
                    inviteeId = inviteeId,
                    status = status,
                )
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.UPDATEINVITESTATUS,
                    parameters = params.asJsonObject(),
                )

            true
        }

    override suspend fun processAccountMultipleInvite(
        inviteIds: List<Long>,
        inviteeId: Long,
        status: String,
    ): NetworkResult<Boolean> =
        safeSupabaseCall {
            val params =
                ProcessAccountMultipleInvitesDTO(
                    inviteIds = inviteIds,
                    inviteeId = inviteeId,
                    status = status,
                )
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.UPDATEMULTIPLEINVITESTATUS,
                    parameters = params.asJsonObject(),
                )

            true
        }

    override suspend fun getAllAccountInvites(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<AccountTeamInvitesDTO>> =
        safeSupabaseCall {
            val params = GetAllAccountInvitesDTO(accountId = accountId, page = page)
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.MemberInvitations.GETACCOUNTINVITES,
                    parameters = params.asJsonObject(),
                )
                .decodeList()
        }
}
