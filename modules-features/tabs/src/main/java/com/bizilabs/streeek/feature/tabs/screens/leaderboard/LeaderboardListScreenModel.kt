package com.bizilabs.streeek.feature.tabs.screens.leaderboard

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber
import kotlin.collections.get

internal val LeaderboardModule =
    module {
        factory<LeaderboardListScreenModel> {
            LeaderboardListScreenModel(context = get(), repository = get())
        }
    }

data class LeaderboardListScreenState(
    val isSyncing: Boolean = false,
    val leaderboardName: String? = null,
    val leaderboard: LeaderboardDomain? = null,
    val leaderboards: List<LeaderboardDomain> = emptyList(),
    val showConfetti: Boolean = false,
) {
    val list: List<LeaderboardAccountDomain>
        get() =
            when {
                leaderboard == null -> emptyList()
                leaderboard.page == 1 -> leaderboard.list.filterIndexed { index, _ -> index > 2 }
                else -> leaderboard.list
            }
}

class LeaderboardListScreenModel(
    private val context: Context,
    private val repository: LeaderboardRepository,
) : StateScreenModel<LeaderboardListScreenState>(LeaderboardListScreenState()) {
    private val selectedLeaderboard =
        combine(repository.selectedLeaderBoardId, repository.leaderboards) { id, map ->
            map[id]
        }

    init {
        observeLeaderboards()
        observeSyncingLeaderboards()
        observeSelectedLeaderboard()
    }

    private fun observeSelectedLeaderboard() {
        screenModelScope.launch {
            selectedLeaderboard.collectLatest { value ->
                mutableState.update { it.copy(leaderboard = value) }
                Timber.d("Top -> ${value?.top}")
                val position = value?.rank?.current?.position
                position?.let { if (it <= 10L) showConfetti() }
            }
        }
    }

    private fun observeSyncingLeaderboards() {
        screenModelScope.launch {
            repository.syncing.collectLatest { value ->
                mutableState.update { it.copy(isSyncing = value) }
            }
        }
    }

    private fun observeLeaderboards() {
        screenModelScope.launch {
            repository.leaderboards.collectLatest { map ->
                mutableState.update { it.copy(leaderboards = map.values.toList()) }
            }
        }
    }

    private fun showConfetti() {
        screenModelScope.launch {
            mutableState.update { it.copy(showConfetti = true) }
            delay(2000)
            mutableState.update { it.copy(showConfetti = false) }
        }
    }

    fun onValueChangeLeaderboard(leaderboard: LeaderboardDomain) {
        screenModelScope.launch {
            repository.set(leaderboard = leaderboard)
        }
    }

    fun onClickViewMore() {
        screenModelScope.launch {
            mutableState.update { it.copy(leaderboardName = mutableState.value.leaderboard?.name) }
            delay(250)
            mutableState.update { it.copy(leaderboardName = null) }
        }
    }
}
