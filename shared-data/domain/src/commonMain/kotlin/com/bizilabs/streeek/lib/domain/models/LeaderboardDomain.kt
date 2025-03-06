package com.bizilabs.streeek.lib.domain.models

enum class Leaderboard {
    WEEKLY,
    MONTHLY,
    ULTIMATE,
}

data class LeaderboardAccountDomain(
    val rank: RankDomain,
    val account: AccountLightDomain,
)

data class LeaderboardDomain(
    val page: Int,
    val name: String,
    val rank: RankDetailsDomain,
    val list: List<LeaderboardAccountDomain>,
    val top: Map<Long, LeaderboardAccountDomain>,
) {
    val accounts: List<LeaderboardAccountDomain>
        get() =
            when {
                page == 1 -> list.filterIndexed { index, _ -> index > 2 }
                else -> list
            }
}

fun LeaderboardDomain?.updateOrCreate(value: LeaderboardDomain): LeaderboardDomain {
    return when {
        this == null -> value
        else ->
            copy(
                page = value.page,
                list = value.list,
                rank = rank.copy(previous = rank.current, current = value.rank.current),
                top = if (page == 1) getTopMembersMap() else emptyMap(),
            )
    }
}

fun LeaderboardDomain.getTopMembersMap(): Map<Long, LeaderboardAccountDomain> {
    val map = mutableMapOf<Long, LeaderboardAccountDomain>()
    list.getOrNull(0)?.let { map[0] = it }
    list.getOrNull(1)?.let { map[1] = it }
    list.getOrNull(2)?.let { map[2] = it }
    return map
}
