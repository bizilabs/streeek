package com.bizilabs.streeek.lib.domain.models

enum class Leaderboard {
    DAILY, WEEKLY, MONTHLY, ULTIMATE
}

data class LeaderboardAccountDomain(
    val rank: RankDomain,
    val account: AccountLightDomain
)

data class LeaderboardDomain(
    val page: Int,
    val name: String,
    val rank: RankDetailsDomain,
    val list: List<LeaderboardAccountDomain>
) {
    val top: Map<Int, LeaderboardAccountDomain>
        get() {
            val map = mutableMapOf<Int, LeaderboardAccountDomain>()
            list.getOrNull(0)?.let { map[0] = it }
            list.getOrNull(1)?.let { map[1] = it }
            list.getOrNull(2)?.let { map[2] = it }
            return map
        }
}

fun LeaderboardDomain?.updateOrCreate(value: LeaderboardDomain): LeaderboardDomain {
    return when {
        this == null -> value
        else -> copy(
            page = value.page,
            list = value.list,
            rank = rank.copy(previous = rank.current, current = value.rank.current)
        )
    }
}
