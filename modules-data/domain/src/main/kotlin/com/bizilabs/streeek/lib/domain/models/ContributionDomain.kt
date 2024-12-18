package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class ContributionDomain(
    val id: Long,
    val createdAt: LocalDateTime,
    val accountId: Long,
    val githubEventId: String,
    val githubEventType: String,
    val githubEventDate: LocalDateTime,
    val githubEventRepo: EventRepositoryDomain,
    val githubEventActor: ActorDomain,
    val githubEventPayload: EventPayloadDomain,
    val points: Long,
)
