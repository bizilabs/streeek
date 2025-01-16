package com.bizilabs.streeek.lib.domain.repositories

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    val syncing: Flow<Boolean>

    val selectedLeaderBoardId: Flow<String?>

    val leaderboards: Flow<Map<String, LeaderboardDomain>>

    suspend fun setIsSyncing(isSyncing: Boolean)

    suspend fun getPagedData(leaderboard: LeaderboardDomain): Flow<PagingData<LeaderboardAccountDomain>>

    suspend fun getWeekly(page: Int): DataResult<LeaderboardDomain>

    suspend fun getMonthly(page: Int): DataResult<LeaderboardDomain>

    suspend fun getUltimate(page: Int): DataResult<LeaderboardDomain>

    suspend fun update(leaderboard: LeaderboardDomain)

    suspend fun set(leaderboard: LeaderboardDomain)
}
