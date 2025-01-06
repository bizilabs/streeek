package com.bizilabs.streeek.lib.domain.models

data class ReleaseDomain(
    val id: Long,
    val name: String,
    val body: String,
    val url: String,
    val author: ActorDomain,
)
