package com.bizilabs.streeek.lib.local.sources.preference

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow

interface LocalPreferenceSource {
    val isSyncingContributions: Flow<Boolean>
    suspend fun setIsSyncingContributions(isSyncing: Boolean)
}

class LocalPreferenceSourceImpl(
    val source: PreferenceSource
) : LocalPreferenceSource {

    object Keys {
        val SyncingContributions = booleanPreferencesKey("syncing_contributions")
    }

    override val isSyncingContributions: Flow<Boolean>
        get() = source.get(key = Keys.SyncingContributions, default = false)

    override suspend fun setIsSyncingContributions(isSyncing: Boolean) {
        source.update(key = Keys.SyncingContributions, value = isSyncing)
    }

}
