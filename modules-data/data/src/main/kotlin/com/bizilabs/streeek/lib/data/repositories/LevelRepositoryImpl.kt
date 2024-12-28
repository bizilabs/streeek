package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.LevelDomain
import com.bizilabs.streeek.lib.domain.repositories.LevelRepository
import com.bizilabs.streeek.lib.local.sources.level.LevelLocalSource
import com.bizilabs.streeek.lib.remote.sources.level.LevelRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class LevelRepositoryImpl(
    private val localSource: LevelLocalSource,
    private val remoteSource: LevelRemoteSource
) : LevelRepository {

    override suspend fun getLevels(): DataResult<List<LevelDomain>> {
        return remoteSource.getLevels()
            .asDataResult { list -> list.map { it.toDomain() } }
    }

    override val levels: Flow<List<LevelDomain>>
        get() = localSource.levels.mapLatest { list -> list.map { it.toDomain() } }

    override suspend fun saveLevels(levels: List<LevelDomain>): DataResult<Boolean> {
        return localSource.saveLevels(levels.map { it.toCache() }).asDataResult { it }
    }

    override suspend fun updateLevels(levels: List<LevelDomain>): DataResult<Boolean> {
        return localSource.updateLevels(levels.map { it.toCache() }).asDataResult { it }
    }

}
