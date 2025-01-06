package com.bizilabs.streeek.lib.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val isSyncingContributions: Flow<Boolean>

    suspend fun setIsSyncingContributions(isSyncing: Boolean)
}
