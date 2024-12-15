package com.bizilabs.streeek.lib.domain.models

import java.util.Date

data class CommitDomain(
    val sha: String,
    val url: String,
    val author: ActorMinimalDomain,
)

data class CommitCommentDomain(
    val id: Long,
    val createdAt: Date,
    val updatedAt: Date,
    val body: String,
    val user: ActorDomain
)
