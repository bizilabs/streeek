package com.bizilabs.streeek.lib.remote.models

data class CreateIssueDto(
    val title: String,
    val body: String,
    val label: String // drop down for feature, bug , enhancement
)