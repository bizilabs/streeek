package com.bizilabs.streeek.lib.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val typography: Flow<String>
    val isSyncingContributions: Flow<Boolean>

    suspend fun setIsSyncingContributions(isSyncing: Boolean)

    suspend fun updateTypography(typography: String)
}
