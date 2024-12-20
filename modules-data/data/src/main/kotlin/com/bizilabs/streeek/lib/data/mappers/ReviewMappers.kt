package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.ReviewDomain
import com.bizilabs.streeek.lib.remote.models.GithubReviewDTO

fun GithubReviewDTO.toDomain() = ReviewDomain(
    id = id,
    user = user.toDomain(),
    body = body,
    state = state
)

fun ReviewDomain.toDTO() = GithubReviewDTO(
    id = id,
    user = user.toDTO(),
    body = body,
    state = state
)
