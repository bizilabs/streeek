package com.bizilabs.streeek.lib.remote.models.supabase

import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.models.AccountLightDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardDTO(
    @SerialName("account_info")
    val current: RankDTO,
    val leaderboard: List<LeaderboardAccountDTO>,
)

@Serializable
data class LeaderboardAccountDTO(
    val account: AccountLightDTO,
    val points: Long,
    val rank: Long,
)

@Serializable
data class LeaderboardRequestDTO(
    @SerialName("p_account_id")
    val accountId: Long,
    @SerialName("p_page")
    val page: Int,
    @SerialName("p_page_size")
    val pageSize: Int = PAGE_SIZE,
)
