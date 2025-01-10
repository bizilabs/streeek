package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.PagingHelpers.START_PAGE
import com.bizilabs.streeek.lib.domain.models.Leaderboard
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.leaderboard.LeaderboardLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.sources.leaderboard.LeaderboardRemoteSource
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class LeaderboardPagingSource(
    private val leaderboard: LeaderboardDomain,
    private val leaderboardRemoteSource: LeaderboardRemoteSource,
    private val leaderboardLocalSource: LeaderboardLocalSource,
    private val accountLocalSource: AccountLocalSource
) : PagingSource<Int, LeaderboardAccountDomain>() {
    override fun getRefreshKey(state: PagingState<Int, LeaderboardAccountDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeaderboardAccountDomain> {
        val page = params.key ?: PagingHelpers.START_PAGE

        val accountId =
            accountLocalSource.account.firstOrNull()?.id
                ?: return LoadResult.Error(Exception("Couldn't get logged in account"))

        if (page == leaderboard.page) {
            val prev = if (page == PagingHelpers.START_PAGE) null else page.minus(1)
            val next = if (leaderboard.list.size < PAGE_SIZE) null else page + 1
            return LoadResult.Page(data = leaderboard.list, prevKey = prev, nextKey = next)
        }

        val result = when (Leaderboard.valueOf(leaderboard.name.uppercase())) {
            Leaderboard.DAILY -> leaderboardRemoteSource.fetchDailyLeaderboard(
                accountId = accountId,
                page = page
            )

            Leaderboard.WEEKLY -> leaderboardRemoteSource.fetchWeeklyLeaderboard(
                accountId = accountId,
                page = page
            )

            Leaderboard.MONTHLY -> leaderboardRemoteSource.fetchMonthlyLeaderboard(
                accountId = accountId,
                page = page
            )

            Leaderboard.ULTIMATE -> leaderboardRemoteSource.fetchUltimateLeaderboard(
                accountId = accountId,
                page = page
            )
        }


        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data.toDomain(name = leaderboard.name, page = page)
        val cache = leaderboard.updateOrCreate(value = data).toCache()
        if (page == START_PAGE) leaderboardLocalSource.update(cache)

        val list = data.list

        val nextKey =
            when {
                list.isEmpty() || list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        Timber.d("Leaderboard -> $list")

        val previousKey = if (page == START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }
}