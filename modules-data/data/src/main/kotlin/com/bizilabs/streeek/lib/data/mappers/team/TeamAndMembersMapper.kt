package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain
import com.bizilabs.streeek.lib.remote.models.supabase.TeamAndMembersDTO

fun TeamAndMembersDTO.toDomain() =
    TeamAndMembersDomain(
        team = team.toDomain(),
        members = members?.map { it.toDomain() } ?: emptyList(),
    )
