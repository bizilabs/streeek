package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable

@Serializable
data class RankDetailsCache(
    val previous: RankCache?,
    val current: RankCache,
)

@Serializable
data class RankCache(
    val points: Long,
    val rank: Long,
)
