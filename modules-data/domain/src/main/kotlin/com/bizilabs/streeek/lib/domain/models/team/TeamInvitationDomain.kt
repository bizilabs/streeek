package com.bizilabs.streeek.lib.domain.models.team

import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import kotlinx.datetime.LocalDateTime

data class TeamInvitationDomain(
    val id: Long,
    val code: Long,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val creatorId: Long
)

data class CreateTeamInvitationDomain(
    val code: Long,
    val expiresAt: LocalDateTime
)

data class JoinTeamInvitationDomain(
    val teamId: Long,
    val role: TeamMemberRole
)
