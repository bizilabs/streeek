package com.bizilabs.streeek.lib.domain.repositories

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    val notifications: Flow<PagingData<NotificationDomain>>

    suspend fun getNotifications(page: Int): DataResult<List<NotificationDomain>>

    suspend fun create(
        title: String,
        message: String,
        payload: String? = null
    ): DataResult<NotificationDomain>

    suspend fun update(notification: NotificationDomain): DataResult<NotificationDomain>

    suspend fun delete(notification: NotificationDomain): DataResult<Boolean>

}