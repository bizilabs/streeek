package com.bizilabs.streeek.lib.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDTO
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.NotificationPagingSource
import com.bizilabs.streeek.lib.data.paging.Paging
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import com.bizilabs.streeek.lib.local.models.NotificationCache
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationCreateDTO
import com.bizilabs.streeek.lib.remote.sources.notifications.NotificationRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

class NotificationRepositoryImpl(
    private val localSource: NotificationLocalSource,
    private val remoteSource: NotificationRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : NotificationRepository {

    private fun List<NotificationCache>.toDomain() = map { item -> item.toDomain() }

    private fun Flow<List<NotificationCache>>.toDomain() =
        mapLatest { list -> list.toDomain() }

    private suspend fun getAccount() = accountLocalSource.account.first()

    private suspend fun getAccountId() = getAccount()?.id?.toLong()

    override val notifications: Flow<PagingData<NotificationDomain>>
        get() = Pager(
            config = PagingConfig(pageSize = Paging.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                NotificationPagingSource(
                    accountLocalSource = accountLocalSource,
                    notificationRemoteSource = remoteSource,
                    notificationLocalSource = localSource
                )
            }
        ).flow

    override suspend fun getNotifications(page: Int): DataResult<List<NotificationDomain>> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't get logged in account")
        return when (val result =
            remoteSource.fetchNotifications(accountId = accountId, page = page)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    override suspend fun create(
        title: String,
        message: String,
        payload: String?
    ): DataResult<NotificationDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't get logged in account")
        val request = NotificationCreateDTO(
            accountId = accountId,
            title = title,
            message = message,
            payload = payload
        )
        return when (val result = remoteSource.create(request = request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                localSource.insert(item = result.data.toDomain().toCache())
                DataResult.Success(data = result.data.toDomain())
            }
        }
    }

    override suspend fun update(notification: NotificationDomain): DataResult<NotificationDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't get logged in account")
        val result = remoteSource.update(notification = notification.toDTO(accountId = accountId))
        return when (result) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                localSource.update(item = result.data.toDomain().toCache())
                DataResult.Success(data = result.data.toDomain())
            }
        }
    }

    override suspend fun delete(notification: NotificationDomain): DataResult<Boolean> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't get logged in account")
        val result = remoteSource.delete(notification = notification.toDTO(accountId = accountId))
        return when (result) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                localSource.delete(id = notification.id)
                DataResult.Success(data = result.data)
            }
        }
    }

}
