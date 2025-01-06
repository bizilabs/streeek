package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.LevelDomain
import kotlinx.coroutines.flow.Flow

interface LevelRepository {
    val levels: Flow<List<LevelDomain>>

    suspend fun saveLevels(levels: List<LevelDomain>): DataResult<Boolean>

    suspend fun updateLevels(levels: List<LevelDomain>): DataResult<Boolean>

    suspend fun getLevels(): DataResult<List<LevelDomain>>
}
