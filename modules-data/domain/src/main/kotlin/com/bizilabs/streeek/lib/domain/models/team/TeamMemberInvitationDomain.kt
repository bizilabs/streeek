package com.bizilabs.streeek.lib.domain.models.team

import com.bizilabs.streeek.lib.domain.models.TeamDomain
import kotlinx.datetime.LocalDateTime

data class AccountsNotInTeamDomain(
    val accountId: Long,
    val username: String,
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val isInvited: Boolean,
)

data class TeamAccountInvitesDomain(
    val inviteId: Long,
    val inviteeId: Long,
    val username: String,
    val avatarUrl: String,
    val invitedOn: LocalDateTime,
)

data class DeleteAccountInvitationDomain(
    val inviteId: Long,
    val message: String,
)

data class AccountTeamInvitesDomain(
    val invite: MemberInviteDomain,
    val team: TeamDomain,
    val teamOwner: TeamOwnerDomain,
)

data class MemberInviteDomain(
    val inviteId: Long,
    val status: String,
    val teamId: Long,
    val createdAt: LocalDateTime,
)

data class TeamOwnerDomain(
    val id: Long,
    val username: String,
    val avatarUrl: String,
)
