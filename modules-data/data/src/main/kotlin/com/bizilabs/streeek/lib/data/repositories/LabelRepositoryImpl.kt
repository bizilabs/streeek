package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import com.bizilabs.streeek.lib.domain.repositories.LabelRepository
import com.bizilabs.streeek.lib.remote.sources.labels.LabelRemoteSource

class LabelRepositoryImpl(
    private val remoteSource: LabelRemoteSource,
) : LabelRepository {
    override suspend fun getLabels(page: Int): DataResult<List<LabelDomain>> {
        return remoteSource.fetchLabels(page = page)
            .asDataResult { list -> list.map { it.toDomain() } }
    }
}
