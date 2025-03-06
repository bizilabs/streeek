package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class StreakDomain(
    val current: Int,
    val longest: Int,
    val updatedAt: LocalDateTime,
)
