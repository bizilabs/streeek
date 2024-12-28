package com.bizilabs.streeek.lib.remote.sources.level

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult

interface LevelRemoteSource {
    suspend fun getLevels(page: Int): NetworkResult<List<LevelDTO>>
}