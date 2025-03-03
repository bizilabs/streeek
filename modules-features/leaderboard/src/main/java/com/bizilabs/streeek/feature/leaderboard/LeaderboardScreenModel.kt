package com.bizilabs.streeek.feature.leaderboard

import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.components.paging.getPagingDataLoading
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import com.bizilabs.streeek.lib.domain.repositories.TauntRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureLeaderboard =
    module {
        factory {
            LeaderboardScreenModel(
                repository = get(),
                accountRepository = get(),
                tauntRepository = get(),
            )
        }
    }

data class LeaderboardScreenState(
    val hasPassedNavigationArgument: Boolean = false,
    val name: String? = null,
    val leaderboard: LeaderboardDomain? = null,
    val dialogState: DialogState? = null,
    val account: AccountDomain? = null,
)

class LeaderboardScreenModel(
    private val repository: LeaderboardRepository,
    private val accountRepository: AccountRepository,
    private val tauntRepository: TauntRepository,
) : StateScreenModel<LeaderboardScreenState>(LeaderboardScreenState()) {
    private var _pages = MutableStateFlow(getPagingDataLoading<LeaderboardAccountDomain>())
    val pages: Flow<PagingData<LeaderboardAccountDomain>> =
        _pages.asStateFlow().cachedIn(screenModelScope)

    fun onValueChange(name: String) {
        if (state.value.hasPassedNavigationArgument) return
        mutableState.update { it.copy(name = name, hasPassedNavigationArgument = true) }
        getLeaderboard()
        fetchAccount()
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

    fun onClickDismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    private fun fetchAccount() {
        screenModelScope.launch {
            accountRepository.account.collectLatest { account ->
                mutableState.update {
                    it.copy(account = account)
                }
            }
        }
    }

    fun onClickMember(
        teamMemberPoints: Long,
        teamMemberId: Long,
        teamMemberName: String,
    ) {
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            val state = state.value
            if (teamMemberId == state.account?.id ||
                teamMemberPoints > (state.leaderboard?.rank?.current?.points ?: 0L)
            ) {
                mutableState.update {
                    it.copy(
                        dialogState =
                            DialogState.Error(
                                title = "Oops",
                                message = "You can only taunt members below you",
                            ),
                    )
                }
            } else {
                when (val result = tauntRepository.taunt(teamMemberId.toString())) {
                    is DataResult.Success -> {
                        mutableState.update {
                            it.copy(
                                dialogState =
                                    DialogState.Success(
                                        title = "Success",
                                        message = "Taunt delivered to $teamMemberName",
                                    ),
                            )
                        }
                    }

                    is DataResult.Error -> {
                        mutableState.update {
                            it.copy(
                                dialogState =
                                    DialogState.Error(
                                        title = "Error",
                                        message = result.message,
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}
