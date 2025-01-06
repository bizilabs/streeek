package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.remote.models.supabase.CreateTeamInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.JoinTeamInvitationDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamInvitationDTO
import kotlinx.datetime.Instant

fun TeamInvitationDTO.toDomain() =
    TeamInvitationDomain(
        id = id,
        code = token,
        createdAt = Instant.parse(createdAt).datetimeUTC,
        expiresAt = Instant.parse(expiresAt).datetimeUTC,
        creatorId = creatorId,
    )

fun CreateTeamInvitationDTO.toDomain() =
    CreateTeamInvitationDomain(
        code = token,
        expiresAt = Instant.parse(expiresAt).datetimeUTC,
    )

fun JoinTeamInvitationDTO.toDomain() =
    JoinTeamInvitationDomain(
        teamId = teamId,
        role = TeamMemberRole.valueOf(role.uppercase()),
    )
