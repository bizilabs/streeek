package com.bizilabs.streeek.feature.landing

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LandingScreenDestination {
    CURRENT, AUTHENTICATE, TABS
}

data class LandingScreenState(
    val destination: LandingScreenDestination = LandingScreenDestination.CURRENT
)

class LandingScreenModel(
    private val repository: AuthenticationRepository
) : StateScreenModel<LandingScreenState>(LandingScreenState()) {

    init {
        checkNavigation()
    }

    private fun checkNavigation() {
        screenModelScope.launch {
            delay(2500)
            val authenticated = repository.authenticated.first()
            if (authenticated.not()) {
                mutableState.update { it.copy(destination = LandingScreenDestination.AUTHENTICATE) }
            }
            val destination = when {
                !authenticated -> LandingScreenDestination.AUTHENTICATE
                else -> LandingScreenDestination.TABS
            }
            mutableState.update { it.copy(destination = destination) }
        }
    }
}