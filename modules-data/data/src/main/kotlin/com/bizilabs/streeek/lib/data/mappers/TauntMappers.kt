package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.TauntDomain
import com.bizilabs.streeek.lib.remote.models.TauntDTO

fun TauntDTO.toDomain() =
    TauntDomain(
        success = success,
        message = message,
    )
