package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

enum class TeamMemberRole {
    OWNER,
    ADMIN,
    MEMBER,
    ;

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
    val top: Map<Long, TeamMemberDomain>,
)

fun TeamDetailsDomain?.updateOrCreate(
    page: Int,
    team: TeamWithMembersDomain,
): TeamDetailsDomain {
    return when {
        this == null -> team.asTeamDetails(page = page)
        else ->
            copy(
                page = page,
                team = team.team,
                members = team.members,
                rank =
                    TeamRankDomain(
                        previous = rank.current,
                        current = team.details.rank,
                    ),
                top = if (page == 1) team.getTopMembersMap() else top,
            )
    }
}

fun TeamWithMembersDomain.asTeamDetails(page: Int): TeamDetailsDomain =
    TeamDetailsDomain(
        page = page,
        team = team,
        members = members,
        rank =
            TeamRankDomain(
                previous = null,
                current = details.rank,
            ),
        top = if (page == 1) getTopMembersMap() else emptyMap(),
    )

private fun TeamWithMembersDomain.getTopMembersMap(): Map<Long, TeamMemberDomain> {
    val map = mutableMapOf<Long, TeamMemberDomain>()
    members.getOrNull(0)?.let { map[0] = it }
    members.getOrNull(1)?.let { map[1] = it }
    members.getOrNull(2)?.let { map[2] = it }
    return map
}

data class TeamRankDomain(
    val previous: Long?,
    val current: Long,
)

data class TeamWithMembersDomain(
    val members: List<TeamMemberDomain>,
    val team: TeamDomain,
    val details: TeamMemberDetailsDomain,
)

data class TeamMemberDetailsDomain(
    val role: TeamMemberRole,
    val rank: Long,
)

data class TeamWithDetailDomain(
    val team: TeamDomain,
    val member: TeamMemberDetailsDomain,
)

data class TeamDomain(
    val id: Long,
    val name: String,
    val public: Boolean,
    val createdAt: LocalDateTime,
    val count: Long,
)

data class TeamMemberDomain(
    val account: TeamMemberAccountDomain,
    val level: TeamMemberLevelDomain,
    val points: Long,
    val rank: Long,
)

data class TeamMemberAccountDomain(
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val id: Long,
    val role: TeamMemberRole,
    val username: String,
)

data class TeamMemberLevelDomain(
    val id: Long,
    val name: String,
    val number: Long,
    val maxPoints: Long,
    val minPoints: Long,
)
