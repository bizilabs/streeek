package com.bizilabs.streeek.lib.domain.models

import java.util.Date

data class UserEventDomain(
    val id: String,
    val type: String,
    val createdAt: Date,
    val actor: ActorDomain,
    val repo: EventRepositoryDomain
)

data class EventRepositoryDomain(
    val id: String,
    val url: String,
    val name: String,
)
