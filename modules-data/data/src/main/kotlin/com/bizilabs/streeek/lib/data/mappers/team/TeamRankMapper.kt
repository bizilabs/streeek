package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.domain.models.TeamRankDomain
import com.bizilabs.streeek.lib.local.models.TeamRankCache

fun TeamRankDomain.toCache() = TeamRankCache(previous = previous, current = current)
fun TeamRankCache.toDomain() = TeamRankDomain(previous = previous, current = current)
