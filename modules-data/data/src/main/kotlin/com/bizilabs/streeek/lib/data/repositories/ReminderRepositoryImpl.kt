package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import com.bizilabs.streeek.lib.domain.repositories.ReminderRepository
import com.bizilabs.streeek.lib.local.sources.reminder.ReminderLocalSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class ReminderRepositoryImpl(
    private val localSource: ReminderLocalSource,
) : ReminderRepository {
    override val reminders: Flow<Map<String, ReminderDomain>>
        get() = localSource.reminders.mapLatest { map -> map.mapValues { reminder -> reminder.value.toDomain() } }

    override suspend fun update(
        reminder: ReminderDomain,
        currentLabel: String?,
    ) {
        localSource.update(reminder = reminder.toCache(), currentLabel = currentLabel)
    }

    override suspend fun delete(reminder: ReminderDomain) {
        localSource.delete(reminder = reminder.toCache())
    }
}
