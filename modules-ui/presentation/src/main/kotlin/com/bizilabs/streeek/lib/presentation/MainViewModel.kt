package com.bizilabs.streeek.lib.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.design.helpers.SafiBarColors
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainActivityState(
    val typography: SafiTypography = SafiTypography.MONO,
    val hasNetworkConnection: Boolean = true,
    val barColors: SafiBarColors = SafiBarColors(top = Color.White, bottom = Color.White),
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
            }
        }
    }

    fun onValueChangeBarColors(colors: SafiBarColors) {
        mutableState.update { it.copy(barColors = colors) }
    }
}
