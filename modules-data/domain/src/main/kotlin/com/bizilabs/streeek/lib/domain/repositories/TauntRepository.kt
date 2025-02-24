package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TauntDomain

interface TauntRepository {
    suspend fun taunt(tauntedId: String): DataResult<TauntDomain>
}