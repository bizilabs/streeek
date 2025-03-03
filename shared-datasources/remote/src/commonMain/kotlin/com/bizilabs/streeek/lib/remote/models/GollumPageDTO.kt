package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GollumPageDTO(
    @SerialName("page_name")
    val name: String,
    val title: String,
    val action: String,
)
