package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

enum class TeamMemberRole {
    OWNER, ADMIN, MEMBER;

    val isAdmin: Boolean
        get() = this != MEMBER

    val label: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

data class TeamDetailsDomain(
    val team: TeamDomain,
    val page: Int,
    val members: List<TeamMemberDomain>,
    val rank: TeamRankDomain,
    val top: Map<Long, TeamMemberDomain>
)

data class TeamRankDomain(
    val previous: Long?,
    val current: Long
)

data class TeamWithMembersDomain(
    val members: List<TeamMemberDomain>,
    val team: TeamDomain,
    val details: TeamMemberDetailsDomain
)

data class TeamMemberDetailsDomain(
    val role: TeamMemberRole,
    val rank: Long
)

data class TeamWithDetailDomain(
    val team: TeamDomain,
    val member: TeamMemberDetailsDomain
)

data class TeamDomain(
    val id: Long,
    val name: String,
    val public: Boolean,
    val createdAt: LocalDateTime,
    val count: Long
)

data class TeamMemberDomain(
    val account: TeamMemberAccountDomain,
    val level: TeamMemberLevelDomain,
    val points: Long,
    val rank: Long
)

data class TeamMemberAccountDomain(
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val id: Long,
    val role: TeamMemberRole,
    val username: String
)

data class TeamMemberLevelDomain(
    val id: Long,
    val name: String,
    val number: Long,
    val maxPoints: Long,
    val minPoints: Long
)
