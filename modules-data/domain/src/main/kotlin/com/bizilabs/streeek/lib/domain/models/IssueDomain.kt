package com.bizilabs.streeek.lib.domain.models

import java.util.Date

data class IssueDomain(
    val id: Long,
    val url: String,
    val number: Long,
    val title: String,
    val body: String,
    val user: ActorDomain,
    val labels: List<LabelDomain>,
    val createdAt: Date,
    val updatedAt: Date,
    val closedAt: Date?
)
