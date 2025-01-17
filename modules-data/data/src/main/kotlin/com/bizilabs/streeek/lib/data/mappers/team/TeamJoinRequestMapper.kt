package com.bizilabs.streeek.lib.data.mappers.team

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamJoinRequestDomain
import com.bizilabs.streeek.lib.remote.models.supabase.team.MemberAccountRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamAccountJoinRequestDTO
import com.bizilabs.streeek.lib.remote.models.supabase.team.TeamJoinRequestDTO

fun TeamJoinRequestDTO.toDomain() =
    TeamJoinRequestDomain(
        id = id,
        status = status,
        createdAt = createdAt,
    )

fun TeamJoinRequestDomain.toDTO() =
    TeamJoinRequestDTO(
        id = id,
        status = status,
        createdAt = createdAt,
    )

fun MemberAccountRequestDTO.toDomain() =
    MemberAccountRequestDomain(
        request = request.toDomain(),
        team = team.toDomain(),
        members = members?.map { it.toDomain() } ?: emptyList(),
    )

fun TeamAccountJoinRequestDTO.toDomain() =
    TeamAccountJoinRequestDomain(
        request = request.toDomain(),
        account = account.toDomain(),
    )
