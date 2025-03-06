package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    val reminders: Flow<Map<String, ReminderDomain>>

    suspend fun update(
        reminder: ReminderDomain,
        currentLabel: String?,
    )

    suspend fun delete(reminder: ReminderDomain)
}
