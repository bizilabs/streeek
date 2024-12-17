package com.bizilabs.streeek.feature.tabs.screens.feed

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.dsl.module

val FeedModule = module {
    factory { FeedScreenModel(accountRepository = get(), contributionRepository = get()) }
}

data class FeedScreenState(
    val account: AccountDomain? = null,
    val contributions: List<ContributionDomain> = emptyList(),
    val isFetchingContributions: Boolean = true,
)

class FeedScreenModel(
    private val accountRepository: AccountRepository,
    private val contributionRepository: ContributionRepository
) : StateScreenModel<FeedScreenState>(FeedScreenState()) {

    private val _date = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.UTC).date)
    val date = _date.asStateFlow()

    val contributions = _date.flatMapLatest {
        mutableState.update { it.copy(isFetchingContributions = true) }
        contributionRepository.getLocalContributionsByDate(date = it).also {
            mutableState.update { it.copy(isFetchingContributions = false) }
        }
    }

    init {
        observeAccount()
    }

    private fun observeAccount() {
        screenModelScope.launch {
            accountRepository.account.collect { account ->
                mutableState.update { it.copy(account = account) }
            }
        }
    }

    // actions
    fun onClickDate(date: LocalDate) {
        _date.update { date }
    }

}
