package com.bizilabs.streeek.feature.landing

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LandingScreenDestination {
    CURRENT, AUTHENTICATE, SETUP, TABS
}

data class LandingScreenState(
    val destination: LandingScreenDestination = LandingScreenDestination.CURRENT
)

class LandingScreenModel(
    private val authenticationRepository: AuthenticationRepository,
    private val accountRepository: AccountRepository
) : StateScreenModel<LandingScreenState>(LandingScreenState()) {

    init {
        checkNavigation()
    }

    private fun checkNavigation() {
        screenModelScope.launch {
            delay(1500)
            val authenticated = authenticationRepository.authenticated.first()
            val account = accountRepository.account.first()
            val destination = when {
                !authenticated -> LandingScreenDestination.AUTHENTICATE
                account == null -> LandingScreenDestination.SETUP
                else -> LandingScreenDestination.TABS
            }
            mutableState.update { it.copy(destination = destination) }
        }
    }
}