package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationDTO
import com.bizilabs.streeek.lib.remote.sources.notifications.NotificationRemoteSource
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class NotificationPagingSource(
    private val accountLocalSource: AccountLocalSource,
    private val notificationRemoteSource: NotificationRemoteSource,
    private val notificationLocalSource: NotificationLocalSource,
) : PagingSource<Int, NotificationDomain>() {

    override fun getRefreshKey(state: PagingState<Int, NotificationDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDomain> {

        val page = params.key ?: Paging.START_PAGE

        val accountId = accountLocalSource.account.firstOrNull()?.id
            ?: return LoadResult.Error(Exception("Couldn't get logged in account"))

        val result = notificationRemoteSource.fetchNotifications(accountId = accountId, page = page)

        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data

        val list = data.map(NotificationDTO::toDomain)

        val nextKey = when {
            list.isEmpty() -> null
            list.size < Paging.PAGE_SIZE -> null
            else -> page.plus(1)
        }

        Timber.d("Notifications -> $list")

        val previousKey = if (page == Paging.START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)

    }

}
