package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.team.AccountTeamInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain
import com.bizilabs.streeek.lib.domain.models.team.DeleteAccountInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.MemberInviteDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamOwnerDomain
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.AccountTeamInvitesDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.AccountsNotInTeamDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.DeleteAccountInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.MemberInviteDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.TeamAccountInvitesDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.invitations.TeamOwnerDTO
import kotlinx.datetime.Instant

fun DeleteAccountInvitationDTO.toDomain() =
    DeleteAccountInvitationDomain(
        inviteId = inviteId,
        message = message,
    )

fun AccountsNotInTeamDTO.toDomain() =
    AccountsNotInTeamDomain(
        accountId = accountId,
        username = username,
        avatarUrl = avatarUrl,
        createdAt =
            createdAt.asLocalDateTime(format = DateFormats.YYYY_MM_DDTHH_MM_SS)
                ?: SystemLocalDateTime,
        isInvited = isInvited,
    )

fun TeamAccountInvitesDTO.toDomain() =
    TeamAccountInvitesDomain(
        inviteId = inviteId,
        inviteeId = inviteeId,
        username = username,
        avatarUrl = avatarUrl,
        invitedOn = Instant.parse(invitedOn).datetimeUTC,
    )

fun AccountTeamInvitesDTO.toDomain() =
    AccountTeamInvitesDomain(
        invite = invite.toDomain(),
        team = team.toDomain(),
        teamOwner = teamOwner.toDomain(),
    )

fun MemberInviteDTO.toDomain() =
    MemberInviteDomain(
        inviteId = inviteId,
        status = status,
        teamId = teamId,
        createdAt = Instant.parse(createdAt).datetimeUTC,
    )

fun TeamOwnerDTO.toDomain() =
    TeamOwnerDomain(
        id = id,
        username = username,
        avatarUrl = avatarUrl,
    )
