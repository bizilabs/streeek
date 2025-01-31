package com.bizilabs.streeek.feature.leaderboard

import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.components.paging.getPagingDataLoading
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureLeaderboard =
    module {
        factory { LeaderboardScreenModel(repository = get()) }
    }

data class LeaderboardScreenState(
    val hasPassedNavigationArgument: Boolean = false,
    val name: String? = null,
    val leaderboard: LeaderboardDomain? = null,
)

class LeaderboardScreenModel(
    private val repository: LeaderboardRepository,
) : StateScreenModel<LeaderboardScreenState>(LeaderboardScreenState()) {
    private var _pages = MutableStateFlow(getPagingDataLoading<LeaderboardAccountDomain>())
    val pages: Flow<PagingData<LeaderboardAccountDomain>> = _pages.asStateFlow().cachedIn(screenModelScope)

    fun onValueChange(name: String) {
        if (state.value.hasPassedNavigationArgument) return
        mutableState.update { it.copy(name = name, hasPassedNavigationArgument = true) }
        getLeaderboard()
    }

    private fun getLeaderboard() {
        val name = mutableState.value.name
        screenModelScope.launch {
            val leaderboards = repository.leaderboards.firstOrNull()
            val leaderboard = leaderboards?.get(name)
            if (leaderboard == null) return@launch
            val paging = repository.getPagedData(leaderboard = leaderboard).first()
            mutableState.update { it.copy(leaderboard = leaderboard) }
            _pages.update { paging }
        }
    }
}
