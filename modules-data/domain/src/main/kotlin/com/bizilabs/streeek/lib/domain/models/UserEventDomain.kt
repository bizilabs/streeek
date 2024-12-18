package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class UserEventDomain(
    val id: String,
    val type: String,
    val createdAt: LocalDateTime,
    val actor: ActorDomain,
    val repo: EventRepositoryDomain,
    val payload: EventPayloadDomain
)

data class EventRepositoryDomain(
    val id: String,
    val url: String,
    val name: String,
)
