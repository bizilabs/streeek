package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.LabelDomain
import com.bizilabs.streeek.lib.remote.models.GithubLabelDTO

fun GithubLabelDTO.toDomain() = LabelDomain(
    id = id,
    name = name,
    color = color,
    description = description
)
