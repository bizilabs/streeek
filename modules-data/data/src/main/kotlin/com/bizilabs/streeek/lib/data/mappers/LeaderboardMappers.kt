package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.models.RankDetailsDomain
import com.bizilabs.streeek.lib.domain.models.RankDomain
import com.bizilabs.streeek.lib.local.models.LeaderboardAccountCache
import com.bizilabs.streeek.lib.local.models.LeaderboardCache
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardAccountDTO
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardDTO

fun LeaderboardAccountDTO.toDomain() =
    LeaderboardAccountDomain(
        rank = RankDomain(position = rank, points = points),
        account = account.toDomain(),
    )

fun LeaderboardAccountDomain.toCache() =
    LeaderboardAccountCache(
        rank = rank.toCache(),
        account = account.toCache(),
    )

fun LeaderboardAccountCache.toDomain() =
    LeaderboardAccountDomain(
        rank = rank.toDomain(),
        account = account.toDomain(),
    )

fun LeaderboardDTO.toDomain(
    name: String,
    page: Int,
) = LeaderboardDomain(
    page = page,
    name = name,
    rank =
        RankDetailsDomain(
            current = RankDomain(position = current.rank, points = current.points),
            previous = null,
        ),
    list = leaderboard.map { it.toDomain() },
    top = if (page == 1) getTopMembersMap() else emptyMap()
)

private fun LeaderboardDTO.getTopMembersMap(): Map<Long, LeaderboardAccountDomain> {
    val map = mutableMapOf<Long, LeaderboardAccountDomain>()
    leaderboard.getOrNull(0)?.let { map[0] = it.toDomain() }
    leaderboard.getOrNull(1)?.let { map[1] = it.toDomain() }
    leaderboard.getOrNull(2)?.let { map[2] = it.toDomain() }
    return map
}

fun LeaderboardDomain.toCache() =
    LeaderboardCache(
        page = page,
        name = name,
        rank = rank.toCache(),
        list = list.map { it.toCache() },
        top = top.mapValues { it.value.toCache() }
    )

fun LeaderboardCache.toDomain() =
    LeaderboardDomain(
        page = page,
        name = name,
        rank = rank.toDomain(),
        list = list.map { it.toDomain() },
        top = top.mapValues { it.value.toDomain() }
    )
