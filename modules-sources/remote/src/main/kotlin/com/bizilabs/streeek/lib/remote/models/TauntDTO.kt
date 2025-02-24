package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class TauntDTO(
    val success: Boolean,
    val message: String
)
