package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubUserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val bio: String,
    @SerialName("avatar_url")
    val url: String
)