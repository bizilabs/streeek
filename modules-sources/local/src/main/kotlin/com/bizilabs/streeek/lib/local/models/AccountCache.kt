package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AccountCache(
    val id: String,
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    val avatarUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val points: Long,
    val level: LevelCache?
) {
    fun asJson(): String = Json.encodeToString(this)
}

internal fun String.fromJsonToAccountCache(): AccountCache = Json.decodeFromString(this)
