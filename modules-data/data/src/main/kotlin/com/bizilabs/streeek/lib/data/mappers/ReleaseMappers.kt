package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.ReleaseDomain
import com.bizilabs.streeek.lib.remote.models.github.GithubReleaseDTO

fun GithubReleaseDTO.toDomain() = ReleaseDomain(
    id = id,
    name = name,
    body = body,
    url = url,
    author = author.toDomain()
)

fun ReleaseDomain.toDTO() = GithubReleaseDTO(
    id = id,
    name = name,
    body = body,
    url = url,
    author = author.toDTO()
)
