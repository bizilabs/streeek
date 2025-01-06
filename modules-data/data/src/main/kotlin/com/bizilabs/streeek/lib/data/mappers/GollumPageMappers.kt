package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.GollumPageDomain
import com.bizilabs.streeek.lib.remote.models.GollumPageDTO

fun GollumPageDTO.toDomain() = GollumPageDomain(name, title, action)

fun GollumPageDomain.toDTO() = GollumPageDTO(name, title, action)

fun List<GollumPageDTO>.toDomain() = map { it.toDomain() }

fun List<GollumPageDomain>.toDTO() = map { it.toDTO() }
