package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.remote.models.GithubIssueDTO
import java.util.Date

fun GithubIssueDTO.toDomain() = IssueDomain(
    id = id,
    url = url,
    number = number,
    title = title,
    body = body,
    user = user.toDomain(),
    labels = labels.map { it.toDomain() },
    createdAt = createdAt.asDate()?.time ?: Date(),
    updatedAt = updatedAt.asDate()?.time ?: Date(),
    closedAt = closedAt?.asDate()?.time
)

fun IssueDomain.toDTO() = GithubIssueDTO(
    id = id,
    url = url,
    number = number,
    title = title,
    body = body,
    user = user.toDTO(),
    labels = labels.map { it.toDTO() },
    createdAt = createdAt.asString(DateFormats.ISO_8601) ?: "",
    updatedAt = updatedAt.asString(DateFormats.ISO_8601) ?: "",
    closedAt = closedAt?.asString(DateFormats.ISO_8601)
)
