package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class GithubReviewDTO(
    val id: Long,
    val user: GithubActorDTO,
    val body: String?,
    val state: String,
)
