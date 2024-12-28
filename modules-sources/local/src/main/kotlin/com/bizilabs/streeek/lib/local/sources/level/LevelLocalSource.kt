package com.bizilabs.streeek.lib.local.sources.level

import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.helpers.safeTransaction
import com.bizilabs.streeek.lib.local.models.LevelCache
import com.bizilabs.streeek.lib.local.models.toCache
import com.bizilabs.streeek.lib.local.models.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

interface LevelLocalSource {
    val levels: Flow<List<LevelCache>>
    suspend fun saveLevels(levels: List<LevelCache>): LocalResult<Boolean>
    suspend fun updateLevels(levels: List<LevelCache>): LocalResult<Boolean>
}

internal class LevelLocalSourceImpl(
    private val source: LevelDAO
) : LevelLocalSource {

    override val levels: Flow<List<LevelCache>>
        get() = source.getLevels().mapLatest { list -> list.map { it.toCache() } }

    override suspend fun saveLevels(levels: List<LevelCache>): LocalResult<Boolean> =
        safeTransaction {
            source.insertLevels(levels.map { it.toEntity() })
            true
        }

    override suspend fun updateLevels(levels: List<LevelCache>): LocalResult<Boolean> =
        safeTransaction {
            source.updateLevels(levels.map { it.toEntity() })
            true
        }

}