package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitDTO(
    val sha: String,
    val url: String,
    val author: GithubActorMinimalDTO,
)

@Serializable
data class CommitStatsDTO(
    val additions: Long,
    val deletions: Long,
    val total: Long
)

@Serializable
data class CommitCommentDTO(
    val id: Long,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val body: String,
    val user: GithubActorDTO
)
