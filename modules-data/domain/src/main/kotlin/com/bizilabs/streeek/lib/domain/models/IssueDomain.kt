package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class IssueDomain(
    val id: Long,
    val url: String,
    val number: Long,
    val title: String,
    val body: String,
    val user: ActorDomain,
    val labels: List<LabelDomain>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val closedAt: LocalDateTime?
)
