package com.bizilabs.streeek.feature.authentication

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.eygraber.uri.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthenticationScreenState(
    val intent: Uri? = null,
    val uri: Uri? = null,
    val fetchState: FetchState<String>? = null,
    val navigateToTabs: Boolean = false,
)

class AuthenticationScreenModel(
    private val authenticationRepository: AuthenticationRepository,
) : StateScreenModel<AuthenticationScreenState>(AuthenticationScreenState()) {
    fun onClickAuthenticate() {
        mutableState.update { it.copy(fetchState = null) }
        getAuthenticationIntent()
    }

    fun onUriReceived(uri: Uri) {
        mutableState.update { it.copy(uri = uri) }
        getAuthenticationToken(uri = uri)
    }

    private fun getAuthenticationIntent() {
        screenModelScope.launch {
            val value = authenticationRepository.getAuthenticationUri()
            mutableState.update { it.copy(intent = value) }
        }
    }

    private fun getAuthenticationToken(uri: Uri) {
        screenModelScope.launch {
            mutableState.update { it.copy(fetchState = FetchState.Loading) }
            val value = authenticationRepository.getAuthenticationToken(uri = uri)
            val update =
                when (value) {
                    is DataResult.Error -> FetchState.Error(value.message)
                    is DataResult.Success -> FetchState.Success(value.data)
                }
            mutableState.update { it.copy(fetchState = update) }
            delay(2500)
            mutableState.update { it.copy(navigateToTabs = update is FetchState.Success) }
        }
    }
}
