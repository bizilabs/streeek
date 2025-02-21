package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class EditIssueDTO(
    val title: String,
    val body: String,
    val labels: List<String>,
    val assignees: List<String>?,
    val milestone: Int?,
    val state: String?,
    val issue_number: String,
    val repo: String,
    val owner: String,
)
