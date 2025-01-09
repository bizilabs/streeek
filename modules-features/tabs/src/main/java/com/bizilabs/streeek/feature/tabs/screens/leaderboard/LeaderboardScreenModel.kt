package com.bizilabs.streeek.feature.tabs.screens.leaderboard

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateSyncLeaderboardWork
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
        factory<LeaderboardScreenModel> {
            LeaderboardScreenModel(context = get(), repository = get())
        }
    }

data class LeaderboardScreenState(
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val isSyncing: Boolean = false,
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

class LeaderboardScreenModel(
    private val context: Context,
    private val repository: LeaderboardRepository,
) : StateScreenModel<LeaderboardScreenState>(LeaderboardScreenState()) {
    private val selectedLeaderboard =
        combine(repository.selectedLeaderBoardId, repository.leaderboards) { id, map ->
            Timber.d("Leaderboard Map -> $map")
            Timber.d("Selected Leaderboard being updated....")
            Timber.d("Selected Leaderboard : ${mutableState.value.leaderboard}")
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
                val position = value?.rank?.current?.position
                position?.let { if (it <= 10L) showConfetti() }
            }
        }
    }

    private fun observeSyncingLeaderboards(){
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

    fun onClickMenuTeamCreate() {
        screenModelScope.launch {
            mutableState.update { it.copy(isCreating = true) }
            delay(250)
            mutableState.update { it.copy(isCreating = false) }
        }
    }

    fun onClickMenuTeamJoin() {
        screenModelScope.launch {
            mutableState.update { it.copy(isJoining = true) }
            delay(250)
            mutableState.update { it.copy(isJoining = false) }
        }
    }

    fun onValueChangeLeaderboard(leaderboard: LeaderboardDomain) {
        screenModelScope.launch {
            repository.set(leaderboard = leaderboard)
        }
    }

    fun onClickMenuRefreshTeam() {
        context.startImmediateSyncLeaderboardWork()
    }
}
