package com.bizilabs.streeek.lib.domain.models

data class EditIssueDomain(
    val title: String,
    val body: String,
    val labels: List<String>,
    val issue_number: String,
    val repo: String,
    val owner: String,
)
