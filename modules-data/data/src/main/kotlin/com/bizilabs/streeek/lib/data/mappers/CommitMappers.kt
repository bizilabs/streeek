package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.CommitCommentDomain
import com.bizilabs.streeek.lib.domain.models.CommitDomain
import com.bizilabs.streeek.lib.remote.models.CommitCommentDTO
import com.bizilabs.streeek.lib.remote.models.CommitDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun CommitDTO.toDomain() = CommitDomain(
    sha = sha,
    url = url,
    author = author.toDomain(),
    message = message,
    distinct = distinct
)

fun CommitDomain.toDTO() =
    CommitDTO(sha = sha, url = url, author = author.toDTO(), message = message, distinct = distinct)

fun CommitCommentDTO.toDomain() = CommitCommentDomain(
    id = id,
    createdAt = createdAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now()
        .toLocalDateTime(TimeZone.UTC),
    updatedAt = updatedAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now()
        .toLocalDateTime(TimeZone.UTC),
    body = body,
    user = user.toDomain()
)

fun CommitCommentDomain.toDTO() = CommitCommentDTO(
    id = id,
    createdAt = createdAt.asString(DateFormats.ISO_8601_Z) ?: "",
    updatedAt = updatedAt.asString(DateFormats.ISO_8601_Z) ?: "",
    body = body,
    user = user.toDTO()
)
