package com.bizilabs.streeek.lib.remote.models.github

import com.bizilabs.streeek.lib.remote.models.GithubActorDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseDTO(
    val id: Long,
    val name: String,
    val body: String,
    @SerialName("html_url")
    val url: String,
    val author: GithubActorDTO
)
