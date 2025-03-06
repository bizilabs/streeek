package com.bizilabs.streeek.lib.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bizilabs.streeek.lib.local.models.ContributionCache

@Entity("contributions")
data class ContributionEntity(
    @PrimaryKey
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
    fun toCache() =
        ContributionCache(
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
