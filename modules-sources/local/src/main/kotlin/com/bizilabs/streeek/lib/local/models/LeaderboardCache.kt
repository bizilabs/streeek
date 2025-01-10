package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardCache(
    val page: Int,
    val name: String,
    val rank: RankDetailsCache,
    val list: List<LeaderboardAccountCache>,
    val top: Map<Long, LeaderboardAccountCache>

)

@Serializable
data class LeaderboardAccountCache(
    val rank: RankCache,
    val account: AccountLightCache,
)
