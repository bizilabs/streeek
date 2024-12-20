package com.bizilabs.streeek.lib.domain.models

data class ReviewDomain(
    val id: Long,
    val user: ActorDomain,
    val body: String?,
    val state: String
)
