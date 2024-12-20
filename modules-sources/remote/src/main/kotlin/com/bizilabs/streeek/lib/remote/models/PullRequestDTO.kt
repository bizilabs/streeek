package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestDTO(
    val id: Long,
    val url: String,
    val number: Long,
    val title: String,
    val body: String,
    val user: GithubActorDTO,
    val labels: List<GithubLabelDTO>,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val comments: Long,
    @SerialName("review_comments")
    val reviewComments: Long,
    val commits: Long,
    val additions: Long,
    val deletions: Long
)

@Serializable
data class MinPullRequestDTO(
    val id: Long,
    val url: String,
    val number: Long,
    val title: String,
    val body: String?,
    val user: GithubActorDTO,
    val labels: List<GithubLabelDTO>,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)
