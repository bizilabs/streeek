package com.bizilabs.streeek.lib.remote.models.supabase

import kotlinx.serialization.Serializable

@Serializable
data class RankDTO(
    val points: Long,
    val rank: Long,
)
