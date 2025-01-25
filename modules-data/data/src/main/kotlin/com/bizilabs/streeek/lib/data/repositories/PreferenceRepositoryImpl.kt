package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSource
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val localSource: LocalPreferenceSource,
    private val remoteSource: RemotePreferencesSource,
) : PreferenceRepository {
    override val typography: Flow<String>
        get() = localSource.typography

    override val isSyncingContributions: Flow<Boolean>
        get() = localSource.isSyncingContributions

    override val hasNetworkConnection: Flow<Boolean>
        get() = localSource.hasNetworkConnection

    override suspend fun updateNetworkConnection(hasNetworkConnection: Boolean) {
        localSource.updateNetworkConnection(hasNetworkConnection = hasNetworkConnection)
    }

    override suspend fun setIsSyncingContributions(isSyncing: Boolean) {
        localSource.setIsSyncingContributions(isSyncing = isSyncing)
    }

    override suspend fun updateTypography(typography: String) {
        localSource.updateTypography(typography = typography)
    }
}
