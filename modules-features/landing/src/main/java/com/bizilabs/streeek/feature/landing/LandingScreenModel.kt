package com.bizilabs.streeek.feature.landing

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LandingScreenDestination {
    CURRENT,
    ONBOARDING,
    AUTHENTICATE,
    SETUP,
    TABS,
}

data class LandingScreenState(
    val destination: LandingScreenDestination = LandingScreenDestination.CURRENT,
)

class LandingScreenModel(
    private val authenticationRepository: AuthenticationRepository,
    private val accountRepository: AccountRepository,
    private val preferenceRepository: PreferenceRepository,
) : StateScreenModel<LandingScreenState>(LandingScreenState()) {
    init {
        checkNavigation()
    }

    private fun checkNavigation() {
        screenModelScope.launch {
            val userHasOnBoarded = preferenceRepository.userHasOnBoarded.first()
            val authenticated = authenticationRepository.authenticated.first()
            val account = accountRepository.account.first()
            val destination =
                when {
                    !userHasOnBoarded -> LandingScreenDestination.ONBOARDING
                    !authenticated -> LandingScreenDestination.AUTHENTICATE
                    account == null -> LandingScreenDestination.SETUP
                    else -> LandingScreenDestination.TABS
                }
            mutableState.update { it.copy(destination = destination) }
        }
    }
}
