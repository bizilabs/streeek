package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class GithubLabelDTO(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
)
