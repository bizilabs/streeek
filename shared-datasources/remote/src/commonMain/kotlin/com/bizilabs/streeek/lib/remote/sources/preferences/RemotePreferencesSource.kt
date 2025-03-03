package com.bizilabs.streeek.lib.remote.sources.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface RemotePreferencesSource {
    val accessToken: Flow<String?>

    suspend fun updateAccessToken(token: String)

    suspend fun clear()
}

class RemotePreferencesSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : RemotePreferencesSource {
    object Keys {
        val accessToken = stringPreferencesKey("access_token")
    }

    fun <T> getNullable(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { it[key] }
    }

    suspend fun <T> update(
        key: Preferences.Key<T>,
        value: T,
    ) {
        withContext(Dispatchers.IO) {
            dataStore.edit { it[key] = value }
        }
    }

    override val accessToken: Flow<String?>
        get() = getNullable(key = Keys.accessToken)

    override suspend fun updateAccessToken(token: String) {
        update(key = Keys.accessToken, value = token)
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
