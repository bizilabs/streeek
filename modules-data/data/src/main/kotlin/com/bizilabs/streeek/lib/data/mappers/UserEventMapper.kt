package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.EventRepositoryDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.remote.models.GithubEventRepositoryDTO
import com.bizilabs.streeek.lib.remote.models.GithubUserEventDTO

fun GithubUserEventDTO.toDomain() =
    UserEventDomain(
        id = id,
        type = type,
        createdAt = createdAt.asDate()?.datetimeUTC ?: SystemLocalDateTime,
        actor = actor.toDomain(),
        repo = repo.toDomain(),
        payload = payload.toDomain(),
    )

fun GithubEventRepositoryDTO.toDomain() =
    EventRepositoryDomain(
        id = id,
        url = url,
        name = name,
    )

fun EventRepositoryDomain.toDTO() =
    GithubEventRepositoryDTO(
        id = id,
        url = url,
        name = name,
    )
