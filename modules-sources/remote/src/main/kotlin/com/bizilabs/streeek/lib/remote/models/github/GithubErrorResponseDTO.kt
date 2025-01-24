package com.bizilabs.streeek.lib.remote.models.github

import kotlinx.serialization.Serializable

@Serializable
data class GithubErrorResponseDTO(
    val message: String,
    val status: String,
)
