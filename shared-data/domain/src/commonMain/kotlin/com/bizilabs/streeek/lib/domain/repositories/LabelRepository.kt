package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.LabelDomain

interface LabelRepository {
    suspend fun getLabels(page: Int): DataResult<List<LabelDomain>>
}
