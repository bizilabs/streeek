package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.remote.models.CommentDTO

fun CommentDTO.toDomain() = CommentDomain(
    id = id,
    user = user.toDomain(),
    body = body,
    createdAt = createdAt.asDate()?.datetimeSystem ?: SystemLocalDateTime,
    updatedAt = updatedAt.asDate()?.datetimeSystem ?: SystemLocalDateTime
)

fun CommentDomain.toDTO() = CommentDTO(
    id = id,
    user = user.toDTO(),
    body = body,
    createdAt = createdAt.asString() ?: "",
    updatedAt = updatedAt.asString() ?: ""
)
