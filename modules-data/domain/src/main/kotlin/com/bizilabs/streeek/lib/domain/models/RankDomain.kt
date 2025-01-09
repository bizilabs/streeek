package com.bizilabs.streeek.lib.domain.models

data class RankDetailsDomain(
    val previous: RankDomain?,
    val current: RankDomain,
)

data class RankDomain(
    val position: Long,
    val points: Long,
)
