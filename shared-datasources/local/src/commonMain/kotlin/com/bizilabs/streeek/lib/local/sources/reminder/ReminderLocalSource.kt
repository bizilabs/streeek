package com.bizilabs.streeek.lib.local.sources.reminder

import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.models.ReminderCache
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ReminderLocalSource {
    val reminders: Flow<Map<String, ReminderCache>>

    suspend fun insert(reminder: ReminderCache)

    suspend fun update(
        reminder: ReminderCache,
        currentLabel: String?,
    )

    suspend fun delete(reminder: ReminderCache)
}

internal class ReminderLocalSourceImpl(
    private val source: PreferenceSource,
) : ReminderLocalSource {
    private val key = stringPreferencesKey("streeek.reminders")

    override val reminders: Flow<Map<String, ReminderCache>>
        get() =
            source.getNullable(key = key).mapLatest { json ->
                json?.let { Json.decodeFromString(it) } ?: emptyMap()
            }

    private suspend fun get(): MutableMap<String, ReminderCache> = reminders.first().toMutableMap()

    private suspend fun save(map: Map<String, ReminderCache>) {
        source.update(key = key, value = Json.encodeToString(map))
    }

    override suspend fun insert(reminder: ReminderCache) {
        val map = get()
        map[reminder.label] = reminder
        save(map)
    }

    override suspend fun update(
        reminder: ReminderCache,
        currentLabel: String?,
    ) {
        val map = get()
        val currentReminder = map[currentLabel]
        if (currentReminder?.label?.isNotEmpty() == true) {
            map.remove(currentLabel)
            map[reminder.label] = reminder
            save(map)
        } else {
            map[reminder.label] = reminder
            save(map)
        }
    }

    override suspend fun delete(reminder: ReminderCache) {
        val map = get()
        map.remove(reminder.label)
        save(map)
    }
}
