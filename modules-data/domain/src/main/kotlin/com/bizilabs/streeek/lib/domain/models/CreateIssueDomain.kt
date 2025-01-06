package com.bizilabs.streeek.lib.domain.models

data class CreateIssueDomain(
    val title: String,
    val body: String,
    val label: String
)