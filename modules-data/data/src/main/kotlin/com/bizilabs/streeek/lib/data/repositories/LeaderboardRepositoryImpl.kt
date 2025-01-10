package com.bizilabs.streeek.lib.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.helper.AccountHelper
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.LeaderboardPagingSource
import com.bizilabs.streeek.lib.data.paging.PagingHelpers
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.Leaderboard
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.leaderboard.LeaderboardLocalSource
import com.bizilabs.streeek.lib.remote.sources.leaderboard.LeaderboardRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

internal class LeaderboardRepositoryImpl(
    private val remoteSource: LeaderboardRemoteSource,
    private val localSource: LeaderboardLocalSource,
    private val accountLocalSource: AccountLocalSource,
) : LeaderboardRepository, AccountHelper(source = accountLocalSource) {
    override val syncing: Flow<Boolean>
        get() = localSource.syncing

    override val selectedLeaderBoardId: Flow<String?>
        get() = localSource.selected

    override val leaderboards: Flow<Map<String, LeaderboardDomain>>
        get() =
            localSource.leaderboards.mapLatest { map ->
                map.mapValues { it.value.toDomain() }
            }

    override suspend fun setIsSyncing(isSyncing: Boolean) {
        localSource.setIsSyncing(isSyncing = isSyncing)
    }

    override suspend fun getPagedData(leaderboard: LeaderboardDomain): Flow<PagingData<LeaderboardAccountDomain>> {
        return Pager(
            config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                LeaderboardPagingSource(
                    leaderboard = leaderboard,
                    accountLocalSource = accountLocalSource,
                    leaderboardLocalSource = localSource,
                    leaderboardRemoteSource = remoteSource,
                )
            },
        ).flow
    }

    override suspend fun getDaily(page: Int): DataResult<LeaderboardDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't find account")
        return remoteSource.fetchDailyLeaderboard(accountId = accountId, page = page)
            .asDataResult { it.toDomain(name = Leaderboard.DAILY.name, page = page) }
    }

    override suspend fun getWeekly(page: Int): DataResult<LeaderboardDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't find account")
        return remoteSource.fetchWeeklyLeaderboard(accountId = accountId, page = page)
            .asDataResult { it.toDomain(name = Leaderboard.WEEKLY.name, page = page) }
    }

    override suspend fun getMonthly(page: Int): DataResult<LeaderboardDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't find account")
        return remoteSource.fetchMonthlyLeaderboard(accountId = accountId, page = page)
            .asDataResult { it.toDomain(name = Leaderboard.MONTHLY.name, page = page) }
    }

    override suspend fun getUltimate(page: Int): DataResult<LeaderboardDomain> {
        val accountId = getAccountId() ?: return DataResult.Error("Couldn't find account")
        return remoteSource.fetchMonthlyLeaderboard(accountId = accountId, page = page)
            .asDataResult { it.toDomain(name = Leaderboard.ULTIMATE.name, page = page) }
    }

    override suspend fun update(leaderboard: LeaderboardDomain) {
        localSource.update(leaderboard.toCache())
    }

    override suspend fun set(leaderboard: LeaderboardDomain) {
        localSource.setSelected(leaderboard = leaderboard.toCache())
    }
}
