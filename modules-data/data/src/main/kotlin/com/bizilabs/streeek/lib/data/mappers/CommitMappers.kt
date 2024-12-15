package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.CommitCommentDomain
import com.bizilabs.streeek.lib.domain.models.CommitDomain
import com.bizilabs.streeek.lib.remote.models.CommitCommentDTO
import com.bizilabs.streeek.lib.remote.models.CommitDTO
import java.util.Date

fun CommitDTO.toDomain() = CommitDomain(sha = sha, url = url, author = author.toDomain())
fun CommitDomain.toDTO() = CommitDTO(sha = sha, url = url, author = author.toDTO())

fun CommitCommentDTO.toDomain() = CommitCommentDomain(
    id = id,
    createdAt = createdAt.asDate()?.time ?: Date(),
    updatedAt = updatedAt.asDate()?.time ?: Date(),
    body = body,
    user = user.toDomain()
)

fun CommitCommentDomain.toDTO() = CommitCommentDTO(
    id = id,
    createdAt = createdAt.asString(DateFormats.ISO_8601) ?: "",
    updatedAt = updatedAt.asString(DateFormats.ISO_8601) ?: "",
    body = body,
    user = user.toDTO()
)
