package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSource
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val localSource: LocalPreferenceSource,
) : PreferenceRepository {
    override val typography: Flow<String>
        get() = localSource.typography

    override val isSyncingContributions: Flow<Boolean>
        get() = localSource.isSyncingContributions

    override val hasNetworkConnection: Flow<Boolean>
        get() = localSource.hasNetworkConnection

    override val userHasOnBoarded: Flow<Boolean>
        get() = localSource.userHasOnBoarded

    override val dismissTime: Flow<String>
        get() = localSource.dismissTime

    override suspend fun updateNetworkConnection(hasNetworkConnection: Boolean) {
        localSource.updateNetworkConnection(hasNetworkConnection = hasNetworkConnection)
    }

    override suspend fun setIsSyncingContributions(isSyncing: Boolean) {
        localSource.setIsSyncingContributions(isSyncing = isSyncing)
    }

    override suspend fun updateTypography(typography: String) {
        localSource.updateTypography(typography = typography)
    }

    override suspend fun updateUserHasOnBoarded(hasOnBoarded: Boolean) {
        localSource.updateUserHasOnBoarded(hasOnBoarded = hasOnBoarded)
    }

    override suspend fun updateDismissTime() {
        localSource.updateDismissTime()
    }
}
