package com.bizilabs.streeek.lib.domain.models

import java.util.Date

data class AccountDomain(
    val id: String,
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    val avatarUrl: String,
    val createdAt: Date,
    val updatedAt: Date
)
