package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateIssueDTO(
    val title: String,
    val body: String,
    val labels: List<String>,
)
