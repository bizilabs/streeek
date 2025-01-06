package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable

@Serializable
data class TeamDetailsCache(
    val team: TeamCache,
    val page: Int,
    val members: List<TeamMemberCache>,
    val rank: TeamRankCache,
    val top: Map<Long, TeamMemberCache>,
)

@Serializable
data class TeamRankCache(
    val previous: Long?,
    val current: Long,
)

@Serializable
data class TeamCache(
    val id: Long,
    val name: String,
    val isPublic: Boolean,
    val count: Long,
    val createdAt: String,
)

@Serializable
data class TeamMemberCache(
    val account: TeamMemberAccountCache,
    val level: TeamMemberLevelCache,
    val points: Long,
    val rank: Long,
)

@Serializable
data class TeamMemberAccountCache(
    val id: Long,
    val role: String,
    val username: String,
    val avatarUrl: String,
    val createdAt: String,
)

@Serializable
data class TeamMemberLevelCache(
    val id: Long,
    val name: String,
    val number: Long,
    val minPoints: Long,
    val maxPoints: Long,
)
