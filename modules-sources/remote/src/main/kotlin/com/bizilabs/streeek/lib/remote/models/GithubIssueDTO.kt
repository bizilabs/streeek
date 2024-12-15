package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubIssueDTO(
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
    @SerialName("closed_at")
    val closedAt: String?
)
