package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDTO(
    val id: String,
    @SerialName("github_id")
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class AccountCreateRequestDTO(
    @SerialName("github_id")
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)