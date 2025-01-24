package com.bizilabs.streeek.lib.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainActivityState(
    val typography: SafiTypography = SafiTypography.MONO,
)

class MainViewModel(
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MainActivityState())
    val state = mutableState.asStateFlow()

    init {
        observeTypography()
    }

    private fun observeTypography() {
        viewModelScope.launch {
            preferenceRepository.typography.collect {
                mutableState.value =
                    mutableState.value.copy(typography = SafiTypography.valueOf(it))
            }
        }
    }
}
