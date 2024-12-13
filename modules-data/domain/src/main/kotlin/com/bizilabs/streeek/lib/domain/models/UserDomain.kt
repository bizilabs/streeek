package com.bizilabs.streeek.lib.domain.models

import kotlinx.serialization.SerialName

data class UserDomain(
    val id: Int,
    val name: String,
    val email: String,
    val bio: String,
    val url: String
)

data class ActorDomain(
    val id: String,
    val name: String,
    val url: String
)
