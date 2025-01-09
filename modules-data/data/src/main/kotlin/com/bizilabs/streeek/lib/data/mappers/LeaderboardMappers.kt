package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.models.RankDetailsDomain
import com.bizilabs.streeek.lib.domain.models.RankDomain
import com.bizilabs.streeek.lib.local.models.LeaderboardAccountCache
import com.bizilabs.streeek.lib.local.models.LeaderboardCache
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardAccountDTO
import com.bizilabs.streeek.lib.remote.models.supabase.LeaderboardDTO

fun LeaderboardAccountDTO.toDomain() = LeaderboardAccountDomain(
    rank = RankDomain(position = rank, points = points),
    account = account.toDomain()
)

fun LeaderboardAccountDomain.toCache() = LeaderboardAccountCache(
    rank = rank.toCache(),
    account = account.toCache()
)

fun LeaderboardAccountCache.toDomain() = LeaderboardAccountDomain(
    rank = rank.toDomain(),
    account = account.toDomain()
)

fun LeaderboardDTO.toDomain(name: String, page: Int) = LeaderboardDomain(
    page = page,
    name = name,
    rank = RankDetailsDomain(
        current = RankDomain(position = current.rank, points = current.points),
        previous = null
    ),
    list = leaderboard.map { it.toDomain() }
)

fun LeaderboardDomain.toCache() = LeaderboardCache(
    page = page,
    name = name,
    rank = rank.toCache(),
    list = list.map { it.toCache() }
)

fun LeaderboardCache.toDomain() = LeaderboardDomain(
    page = page,
    name = name,
    rank = rank.toDomain(),
    list = list.map { it.toDomain() }
)
