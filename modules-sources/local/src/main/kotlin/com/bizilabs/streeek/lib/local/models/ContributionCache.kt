package com.bizilabs.streeek.lib.local.models

import com.bizilabs.streeek.lib.local.entities.ContributionEntity
import kotlinx.serialization.Serializable

@Serializable
data class ContributionCache(
    val id: Long,
    val createdAt: String,
    val accountId: Long,
    val githubEventId: String,
    val githubEventType: String,
    val githubEventDate: String,
    val githubEventRepo: String,
    val githubEventActor: String,
    val githubEventPayload: String,
    val points: Long,
) {
    fun toEntity() =
        ContributionEntity(
            id = id,
            createdAt = createdAt,
            accountId = accountId,
            githubEventId = githubEventId,
            githubEventType = githubEventType,
            githubEventDate = githubEventDate,
            githubEventRepo = githubEventRepo,
            githubEventActor = githubEventActor,
            githubEventPayload = githubEventPayload,
            points = points,
        )
}
