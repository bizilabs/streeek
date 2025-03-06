package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class CommentDomain(
    val id: Long,
    val body: String,
    val user: ActorDomain,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
