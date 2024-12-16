package com.bizilabs.streeek.feature.tabs

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.domain.workers.startDailySyncContributionsWork
import com.bizilabs.streeek.lib.domain.workers.startSyncContributionsWork
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber

val tabsModule = module {
    factory {
        TabsScreenModel(
            context = get(),
            contributionRepository = get(),
            accountRepository = get()
        )
    }
}

data class TabsScreenState(
    val accountState: FetchState<AccountDomain> = FetchState.Loading,
    val eventsState: FetchState<List<UserEventDomain>> = FetchState.Loading,
    val contributions: FetchState<List<ContributionDomain>> = FetchState.Loading
)

class TabsScreenModel(
    private val context: Context,
    private val contributionRepository: ContributionRepository,
    private val accountRepository: AccountRepository
) : StateScreenModel<TabsScreenState>(TabsScreenState()) {

    init {
        observeAccount()
        observeContributions()
        startSyncContributionsWork()
    }

    private fun startSyncContributionsWork() {
        with(context){
//            startSyncContributionsWork()
            startDailySyncContributionsWork()
        }
    }

    private fun observeAccount() {
        screenModelScope.launch {
            accountRepository.account.collect { account ->
                val update = when (account) {
                    null -> FetchState.Error("No account found")
                    else -> FetchState.Success(value = account)
                }
                mutableState.update { it.copy(accountState = update) }
            }
        }
    }

    private fun getUserEvents() {
        screenModelScope.launch {
            mutableState.update { it.copy(eventsState = FetchState.Loading) }
            val update = when (val result = contributionRepository.getEvents(page = 1)) {
                is DataResult.Error -> FetchState.Error(message = result.message)
                is DataResult.Success -> {
                    Timber.d(
                        "Events: \n${
                            result.data.map {
                                mapOf(
                                    "z---" to "\n",
                                    "id" to it.id,
                                    "type" to it.type,
                                    "createdAt" to it.createdAt,
                                )
                            }
                        }"
                    )
                    FetchState.Success(value = result.data)
                }
            }
            mutableState.update { it.copy(eventsState = update) }
        }
    }

    private fun observeContributions() {
        screenModelScope.launch {
            contributionRepository.contributions.collect { contributions ->
                mutableState.update { it.copy(contributions = FetchState.Success(value = contributions)) }
            }
        }
    }

}