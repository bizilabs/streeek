package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.EditIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.remote.models.CreateIssueDTO
import com.bizilabs.streeek.lib.remote.models.EditIssueDTO
import com.bizilabs.streeek.lib.remote.models.GithubIssueDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun GithubIssueDTO.toDomain() =
    IssueDomain(
        id = id,
        url = url,
        number = number,
        title = title,
        body = body ?: "",
        user = user.toDomain(),
        labels = labels.map { it.toDomain() },
        createdAt =
            createdAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now().toLocalDateTime(
                TimeZone.UTC,
            ),
        updatedAt =
            updatedAt.asDate()?.toLocalDateTime(TimeZone.UTC) ?: Clock.System.now()
                .toLocalDateTime(TimeZone.UTC),
        closedAt = closedAt?.asDate()?.toLocalDateTime(TimeZone.UTC),
    )

fun IssueDomain.toDTO() =
    GithubIssueDTO(
        id = id,
        url = url,
        number = number,
        title = title,
        body = body,
        user = user.toDTO(),
        labels = labels.map { it.toDTO() },
        createdAt = createdAt.asString(DateFormats.ISO_8601_Z) ?: "",
        updatedAt = updatedAt.asString(DateFormats.ISO_8601_Z) ?: "",
        closedAt = closedAt?.asString(DateFormats.ISO_8601_Z),
    )

fun CreateIssueDomain.toDTO(): CreateIssueDTO =
    CreateIssueDTO(
        title = title,
        body = body,
        labels = labels,
    )

fun EditIssueDomain.toDTO(): EditIssueDTO =
    EditIssueDTO(
        title = title,
        body = body,
        labels = labels,
        issue_number = issue_number,
        repo = repo,
        owner = owner,
    )
