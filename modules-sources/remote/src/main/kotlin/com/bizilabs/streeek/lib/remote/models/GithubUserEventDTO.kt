package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubEventRepositoryDTO(
    val id: String,
    val name: String,
    val url: String,
)

@Serializable
data class GithubUserEventDTO(
    val id: String,
    val type: String,
    @SerialName("created_at")
    val createdAt: String,
    val actor: GithubActorDTO,
    val repo: GithubEventRepositoryDTO,
    val payload: EventPayloadDTO,
)
