package com.bizilabs.streeek.lib.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val typography: Flow<String>
    val isSyncingContributions: Flow<Boolean>
    val hasNetworkConnection: Flow<Boolean>

    suspend fun setIsSyncingContributions(isSyncing: Boolean)

    suspend fun updateTypography(typography: String)

    suspend fun updateNetworkConnection(hasNetworkConnection: Boolean)
}
