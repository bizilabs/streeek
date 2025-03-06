package com.bizilabs.streeek.lib.domain.models.team

import com.bizilabs.streeek.lib.domain.models.AccountLightDomain
import com.bizilabs.streeek.lib.domain.models.TeamDomain

data class TeamJoinRequestDomain(
    val id: Long,
    val status: String,
    val createdAt: String,
)

data class MemberAccountRequestDomain(
    val request: TeamJoinRequestDomain,
    val team: TeamDomain,
    val members: List<AccountLightDomain>,
)

data class TeamAccountJoinRequestDomain(
    val request: TeamJoinRequestDomain,
    val account: AccountLightDomain,
)
