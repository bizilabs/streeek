package com.bizilabs.streeek.lib.local.sources.team

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.models.TeamDetailsCache
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface TeamLocalSource {
    val isSyncing: Flow<Boolean>
    val teamId: Flow<Long?>
    val teams: Flow<Map<Long, TeamDetailsCache>>

    suspend fun updateIsSyncing(isSyncing: Boolean)

    suspend fun get(id: Long): LocalResult<TeamDetailsCache>

    suspend fun setSelected(team: TeamDetailsCache)

    suspend fun add(team: TeamDetailsCache)

    suspend fun update(team: TeamDetailsCache)

    suspend fun delete(team: TeamDetailsCache)

    suspend fun delete(teamId: Long)
}

internal class TeamLocalSourceImpl(val source: PreferenceSource) : TeamLocalSource {
    private val syncing = booleanPreferencesKey("team.syncing")
    private val teamKey = longPreferencesKey("team.id")
    private val teamsKey = stringPreferencesKey("teams.list")

    override val isSyncing: Flow<Boolean>
        get() = source.get(syncing, false)

    override val teamId: Flow<Long?>
        get() = source.getNullable(teamKey)

    override val teams: Flow<Map<Long, TeamDetailsCache>>
        get() =
            source.getNullable(teamsKey).mapLatest { json ->
                json?.let { Json.decodeFromString(it) } ?: emptyMap()
            }

    override suspend fun updateIsSyncing(isSyncing: Boolean) {
        source.update(syncing, isSyncing)
    }

    private suspend fun getMutableMap() = teams.first().toMutableMap()

    private suspend fun saveMutableMap(map: MutableMap<Long, TeamDetailsCache>) {
        source.update(teamsKey, Json.encodeToString(map))
    }

    override suspend fun get(id: Long): LocalResult<TeamDetailsCache> {
        val map = getMutableMap()
        val team = map[id] ?: return LocalResult.Error("Couldn't get ")
        return LocalResult.Success(team)
    }

    override suspend fun setSelected(team: TeamDetailsCache) {
        source.update(teamKey, team.team.id)
    }

    override suspend fun add(team: TeamDetailsCache) {
        val map = getMutableMap()
        map[team.team.id] = team
        saveMutableMap(map)
    }

    override suspend fun update(team: TeamDetailsCache) {
        val map = getMutableMap()
        map[team.team.id] = team
        saveMutableMap(map)
    }

    override suspend fun delete(team: TeamDetailsCache) {
        remove(id = team.team.id)
    }

    override suspend fun delete(teamId: Long) {
        remove(id = teamId)
    }

    private suspend fun remove(id: Long) {
        val map = getMutableMap()
        map.remove(id)
        val selected = teamId.first()
        if (selected == id) {
            map.values.firstOrNull()?.let { setSelected(it) }
        }
        saveMutableMap(map)
    }
}
