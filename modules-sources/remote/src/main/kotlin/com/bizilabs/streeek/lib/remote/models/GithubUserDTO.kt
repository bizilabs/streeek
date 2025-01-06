package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubUserDTO(
    val id: Int,
    @SerialName("login")
    val name: String,
    val email: String? = null,
    val bio: String,
    @SerialName("avatar_url")
    val url: String,
)

@Serializable
data class GithubActorDTO(
    val id: Long,
    @SerialName("login")
    val name: String,
    @SerialName("avatar_url")
    val url: String,
)

@Serializable
data class GithubActorMinimalDTO(
    val name: String,
)
