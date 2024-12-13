package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.remote.models.GithubUserEventDTO
import java.sql.Date

fun GithubUserEventDTO.toDomain() = UserEventDomain(
    id = id,
    type = type,
    createdAt = createdAt.asDate()?.time ?: Date.valueOf(createdAt),
    actor = actor.toDomain()
)
