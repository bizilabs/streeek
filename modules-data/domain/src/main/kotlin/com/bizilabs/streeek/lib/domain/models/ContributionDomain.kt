package com.bizilabs.streeek.lib.domain.models

import java.util.Date

data class ContributionDomain(
    val id: Long,
    val createdAt: Date,
    val accountId: Long,
    val githubEventId: String,
    val githubEventType: String,
    val githubEventDate: Date,
    val githubEventRepo: EventRepositoryDomain,
    val githubEventActor: ActorMinimalDomain,
    val githubEventPayload: EventPayloadDomain,
    val points: Long,
)
