package com.bizilabs.streeek.lib.domain.models

data class UserDomain(
    val id: Int,
    val name: String,
    val email: String,
    val bio: String,
    val url: String
)

data class ActorDomain(
    val id: Long,
    val name: String,
    val url: String
)

data class ActorMinimalDomain(val name: String)