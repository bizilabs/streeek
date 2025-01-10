package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.RankDetailsDomain
import com.bizilabs.streeek.lib.domain.models.RankDomain
import com.bizilabs.streeek.lib.local.models.RankCache
import com.bizilabs.streeek.lib.local.models.RankDetailsCache
import com.bizilabs.streeek.lib.remote.models.supabase.RankDTO

fun RankDTO.toDomain() = RankDomain(position = rank, points = points)

fun RankDomain.toDTO() = RankDTO(rank = position, points = points)

fun RankDomain.toCache() = RankCache(points = points, rank = position)

fun RankCache.toDomain() = RankDomain(points = points, position = rank)

fun RankDetailsDomain.toCache() = RankDetailsCache(previous = previous?.toCache(), current = current.toCache())

fun RankDetailsCache.toDomain() = RankDetailsDomain(previous = previous?.toDomain(), current = current.toDomain())
