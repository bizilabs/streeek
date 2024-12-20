package com.bizilabs.streeek.lib.domain.models

import kotlinx.serialization.Serializable

data class GollumPageDomain(
    val name: String,
    val title: String,
    val action: String,
)
