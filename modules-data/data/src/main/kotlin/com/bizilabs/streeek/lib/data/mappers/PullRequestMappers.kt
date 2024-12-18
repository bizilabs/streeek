package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.PullRequestDomain
import com.bizilabs.streeek.lib.remote.models.PullRequestDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun PullRequestDTO.toDomain() = PullRequestDomain(
    id = id,
    url = url,
    number = number,
    title = title,
    body = body,
    user = user.toDomain(),
    labels = labels.map { it.toDomain() },
    createdAt = createdAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC),
    updatedAt = updatedAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(TimeZone.UTC),
    comments = comments,
    reviewComments = reviewComments,
    commits = commits,
    additions = additions,
    deletions = deletions
)

fun PullRequestDomain.toDTO() = PullRequestDTO(
    id = id,
    url = url,
    number = number,
    title = title,
    body = body,
    user = user.toDTO(),
    labels = labels.map { it.toDTO() },
    createdAt = createdAt.asString(DateFormats.ISO_8601) ?: "",
    updatedAt = updatedAt.asString(DateFormats.ISO_8601) ?: "",
    comments = comments,
    reviewComments = reviewComments,
    commits = commits,
    additions = additions,
    deletions = deletions
)
