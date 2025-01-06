package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asLocalDate
import com.bizilabs.streeek.lib.domain.helpers.asLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.local.models.ContributionCache
import com.bizilabs.streeek.lib.remote.helpers.asJson
import com.bizilabs.streeek.lib.remote.models.ContributionDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadSerializer
import com.bizilabs.streeek.lib.remote.models.GithubActorDTO
import com.bizilabs.streeek.lib.remote.models.GithubEventRepositoryDTO
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

val JsonSerializer =
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
        classDiscriminator = "#class"
    }

private fun String.asGithubRepo(): GithubEventRepositoryDTO = JsonSerializer.decodeFromString(this)

private fun String.asActor(): GithubActorDTO = JsonSerializer.decodeFromString(this)

private fun String.asEventPayload(): EventPayloadDTO = JsonSerializer.decodeFromString(EventPayloadSerializer, this)

fun ContributionDTO.toDomain() =
    ContributionDomain(
        id = id,
        createdAt = Instant.parse(createdAt).datetimeSystem,
        accountId = accountId,
        githubEventId = githubEventId,
        githubEventType = githubEventType,
        githubEventDate = githubEventDate.asLocalDate(DateFormats.YYYY_MM_DD)?.asLocalDateTime() ?: SystemLocalDateTime,
        githubEventRepo = githubEventRepo.asGithubRepo().toDomain(),
        githubEventActor = githubEventActor.asActor().toDomain(),
        githubEventPayload = githubEventPayload.asEventPayload().toDomain(),
        points = points,
    )

fun ContributionDomain.toCache() =
    ContributionCache(
        id = id,
        createdAt = createdAt.asString() ?: "",
        accountId = accountId,
        githubEventId = githubEventId,
        githubEventType = githubEventType,
        githubEventDate = githubEventDate.asString(DateFormats.YYYY_MM_DD) ?: "",
        githubEventRepo = githubEventRepo.toDTO().asJson(),
        githubEventActor = githubEventActor.toDTO().asJson(),
        githubEventPayload = githubEventPayload.toDTO().asJson(),
        points = points,
    )

fun ContributionCache.toDomain() =
    ContributionDomain(
        id = id,
        createdAt = createdAt.asDate()?.datetimeSystem ?: SystemLocalDateTime,
        accountId = accountId,
        githubEventId = githubEventId,
        githubEventType = githubEventType,
        githubEventDate = githubEventDate.asLocalDate(DateFormats.YYYY_MM_DD)?.asLocalDateTime() ?: SystemLocalDateTime,
        githubEventRepo = githubEventRepo.asGithubRepo().toDomain(),
        githubEventActor = githubEventActor.asActor().toDomain(),
        githubEventPayload = githubEventPayload.asEventPayload().toDomain(),
        points = points,
    )
