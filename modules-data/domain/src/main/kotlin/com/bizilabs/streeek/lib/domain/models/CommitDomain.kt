package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class CommitDomain(
    val sha: String,
    val url: String,
    val author: ActorMinimalDomain,
)

data class CommitCommentDomain(
    val id: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val body: String,
    val user: ActorDomain
)
