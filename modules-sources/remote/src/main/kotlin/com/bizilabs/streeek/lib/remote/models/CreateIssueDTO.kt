package com.bizilabs.streeek.lib.remote.models

data class CreateIssueDTO(
    val title: String,
    val body: String,
    val label: String,
)
