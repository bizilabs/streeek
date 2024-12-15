package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.remote.models.ContributionDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadSerializer
import com.bizilabs.streeek.lib.remote.models.GithubActorMinimalDTO
import com.bizilabs.streeek.lib.remote.models.GithubEventRepositoryDTO
import kotlinx.serialization.json.Json
import java.util.Date

private fun String.asGithubRepo(): GithubEventRepositoryDTO = Json.decodeFromString(this)
private fun String.asActorMinimal(): GithubActorMinimalDTO = Json.decodeFromString(this)
private fun String.asEventPayload(): EventPayloadDTO =
    Json.decodeFromString(EventPayloadSerializer, this)

fun ContributionDTO.toDomain() = ContributionDomain(
    id = id,
    createdAt = createdAt,
    accountId = accountId,
    githubEventId = githubEventId,
    githubEventType = githubEventType,
    githubEventDate = githubEventDate.asDate()?.time ?: Date(),
    githubEventRepo = githubEventRepo.asGithubRepo().toDomain(),
    githubEventActor = githubEventActor.asActorMinimal().toDomain(),
    githubEventPayload = githubEventPayload.asEventPayload().toDomain(),
    points = points
)
