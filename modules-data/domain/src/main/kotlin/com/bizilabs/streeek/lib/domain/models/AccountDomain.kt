package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class AccountDomain(
    val id: Long,
    val githubId: Long,
    val username: String,
    val email: String,
    val bio: String,
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val points: Long,
    val level: LevelDomain?,
    val streak: StreakDomain?,
)

data class AccountLightDomain(
    val id: Long,
    val username: String,
    val avatarUrl: String,
    val createdAt: LocalDateTime,
)
