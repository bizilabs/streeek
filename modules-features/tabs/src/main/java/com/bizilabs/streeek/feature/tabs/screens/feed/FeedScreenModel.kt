package com.bizilabs.streeek.feature.tabs.screens.feed

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateDailySyncContributionsWork
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.dsl.module

internal val FeedModule =
    module {
        factory {
            FeedScreenModel(
                context = get(),
                accountRepository = get(),
                preferenceRepository = get(),
                contributionRepository = get(),
            )
        }
    }

data class FeedScreenState(
    val isSyncing: Boolean = false,
    val isMonthView: Boolean = false,
    val account: AccountDomain? = null,
    val isFetchingContributions: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val dates: List<LocalDate> = emptyList(),
) {
    val isToday: Boolean
        get() = selectedDate == LocalDate.now()
}

class FeedScreenModel(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val preferenceRepository: PreferenceRepository,
    private val contributionRepository: ContributionRepository,
) : StateScreenModel<FeedScreenState>(FeedScreenState()) {
    private val _date = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val date = _date.asStateFlow()

    val contributions: Flow<List<ContributionDomain>> =
        _date.flatMapLatest {
            mutableState.update { it.copy(isFetchingContributions = true) }
            contributionRepository.getLocalContributionsByDate(date = it).also {
                mutableState.update { it.copy(isFetchingContributions = false) }
            }
        }

    init {
        observeDates()
        observeAccount()
        observeSyncingContributions()
    }

    private fun observeDates() {
        screenModelScope.launch {
            contributionRepository.dates.collectLatest { dates ->
                mutableState.update { it.copy(dates = dates.map { it.date }) }
            }
        }
    }

    private fun observeAccount() {
        screenModelScope.launch {
            accountRepository.account.collect { account ->
                mutableState.update { it.copy(account = account) }
            }
        }
    }

    private fun observeSyncingContributions() {
        screenModelScope.launch {
            preferenceRepository.isSyncingContributions.collectLatest { value ->
                mutableState.update { it.copy(isSyncing = value) }
            }
        }
    }

    // actions
    fun onClickDate(date: LocalDate) {
        _date.update { date }
        mutableState.update { it.copy(selectedDate = date) }
    }

    fun onRefreshContributions() {
        context.startImmediateDailySyncContributionsWork()
    }

    fun onClickToggleMonthView() {
        mutableState.update { it.copy(isMonthView = it.isMonthView.not()) }
    }
}
