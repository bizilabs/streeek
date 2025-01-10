package com.bizilabs.streeek.lib.local.sources.leaderboard

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.models.LeaderboardCache
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface LeaderboardLocalSource {
    val syncing: Flow<Boolean>

    val selected: Flow<String?>

    val leaderboards: Flow<Map<String, LeaderboardCache>>

    suspend fun setIsSyncing(isSyncing: Boolean)

    suspend fun setSelected(leaderboard: LeaderboardCache)

    suspend fun create(leaderboard: LeaderboardCache)

    suspend fun update(leaderboard: LeaderboardCache)

    suspend fun delete(leaderboard: LeaderboardCache)
}

internal class LeaderboardLocalSourceImpl(
    private val source: PreferenceSource,
) : LeaderboardLocalSource {
    private val leaderboardKey = stringPreferencesKey("leaderboard.id")
    private val leaderboardSyncKey = booleanPreferencesKey("leaderboard.sync")
    private val leaderboardsKey = stringPreferencesKey("leaderboard.list")

    override val syncing: Flow<Boolean>
        get() = source.get(key = leaderboardSyncKey, default = false)

    override val selected: Flow<String?>
        get() = source.getNullable(key = leaderboardKey)

    override val leaderboards: Flow<Map<String, LeaderboardCache>>
        get() =
            source.getNullable(key = leaderboardsKey).mapLatest { json ->
                json?.let { Json.decodeFromString(it) } ?: emptyMap()
            }

    private suspend fun getMutableMap() = leaderboards.first().toMutableMap()

    private suspend fun saveMutableMap(map: MutableMap<String, LeaderboardCache>) {
        source.update(leaderboardsKey, Json.encodeToString(map))
    }

    override suspend fun setIsSyncing(isSyncing: Boolean) {
        source.update(leaderboardSyncKey, isSyncing)
    }

    override suspend fun setSelected(leaderboard: LeaderboardCache) {
        source.update(leaderboardKey, leaderboard.name)
    }

    override suspend fun create(leaderboard: LeaderboardCache) {
        val map = getMutableMap()
        map[leaderboard.name] = leaderboard
        saveMutableMap(map)
    }

    override suspend fun update(leaderboard: LeaderboardCache) {
        val map = getMutableMap()
        map[leaderboard.name] = leaderboard
        saveMutableMap(map)
    }

    override suspend fun delete(leaderboard: LeaderboardCache) {
        val map = getMutableMap()
        map.remove(leaderboard.name)
        saveMutableMap(map)
    }
}
