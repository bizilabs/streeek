package com.bizilabs.streeek.lib.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class MainActivityState(
    val typography: SafiTypography = SafiTypography.MONO,
    val hasNetworkConnection: Boolean = true,
)

class MainViewModel(
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MainActivityState())
    val state = mutableState.asStateFlow()

    init {
        observeTypography()
        observeNetworkConnection()
    }

    private fun observeTypography() {
        viewModelScope.launch {
            preferenceRepository.typography.collect { value ->
                mutableState.update { it.copy(typography = SafiTypography.valueOf(value)) }
            }
        }
    }

    private fun observeNetworkConnection() {
        viewModelScope.launch {
            preferenceRepository.hasNetworkConnection.collect { value ->
                mutableState.update { it.copy(hasNetworkConnection = value) }
                Timber.d("Netty -> Has network connection : $value")
            }
        }
    }
}
