package com.bizilabs.streeek.feature.setup

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureSetup =
    module {
        factory {
            SetupScreenModel(
                userRepository = get(),
                accountRepository = get(),
                workersRepository = get(),
            )
        }
    }

data class SetupScreenState(
    val userState: FetchState<UserDomain> = FetchState.Loading,
    val accountState: FetchState<AccountDomain> = FetchState.Loading,
)

class SetupScreenModel(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val workersRepository: WorkersRepository,
) : StateScreenModel<SetupScreenState>(SetupScreenState()) {
    init {
        getUser()
    }

    private fun getUser() {
        screenModelScope.launch {
            mutableState.update { it.copy(userState = FetchState.Loading) }
            val update =
                when (val result = userRepository.getUser()) {
                    is DataResult.Error -> FetchState.Error(message = result.message)
                    is DataResult.Success -> FetchState.Success(value = result.data)
                }
            mutableState.update { it.copy(userState = update) }
            if (update is FetchState.Success) getAccount()
        }
    }

    private fun getAccount() {
        val user = (mutableState.value.userState as? FetchState.Success)?.value ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(accountState = FetchState.Loading) }
            when (val result = accountRepository.getAccountWithGithubId(id = user.id)) {
                is DataResult.Error -> {
                    mutableState.update { it.copy(accountState = FetchState.Error(message = result.message)) }
                }

                is DataResult.Success -> {
                    val account = result.data
                    if (account == null) {
                        createAccount(user = user)
                    } else {
                        syncContributions()
                        mutableState.update { it.copy(accountState = FetchState.Success(value = account)) }
                    }
                }
            }
        }
    }

    private fun createAccount(user: UserDomain) {
        screenModelScope.launch {
            mutableState.update { it.copy(accountState = FetchState.Loading) }
            val update =
                when (
                    val result =
                        accountRepository.createAccount(
                            githubId = user.id,
                            username = user.name,
                            email = user.email,
                            bio = user.bio ?: "",
                            avatarUrl = user.url,
                        )
                ) {
                    is DataResult.Error -> FetchState.Error(message = result.message)
                    is DataResult.Success -> {
                        syncContributions()
                        FetchState.Success(value = result.data)
                    }
                }
            mutableState.update { it.copy(accountState = update) }
        }
    }

    private fun syncContributions() {
        workersRepository.runSyncContributions()
    }

    fun onClickGetUserRetry() {
        getUser()
    }

    fun onClickGetAccountRetry() {
        getAccount()
    }
}
