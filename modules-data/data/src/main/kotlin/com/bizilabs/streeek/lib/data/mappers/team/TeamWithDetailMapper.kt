package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithDetailDTO

fun TeamWithDetailDTO.toDomain() =
    TeamWithDetailDomain(
        team = team.toDomain(),
        member = member.toDomain(),
    )
