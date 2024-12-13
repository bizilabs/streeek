package com.bizilabs.streeek.feature.tabs

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val tabsModule = module {
    factory{ TabsScreenModel(repository = get()) }
}

data class TabsScreenState(
    val userState: FetchState<UserDomain> = FetchState.Loading
)

class TabsScreenModel(
    private val repository: UserRepository
) : StateScreenModel<TabsScreenState>(TabsScreenState()) {
    init {
        getUser()
    }

    private fun getUser() {
        screenModelScope.launch {
            mutableState.update { it.copy(userState = FetchState.Loading) }
            val update = when (val result = repository.getUser()) {
                is DataResult.Error -> FetchState.Error(message = result.message)
                is DataResult.Success -> FetchState.Success(value = result.data)
            }
            mutableState.update { it.copy(userState = update) }
        }
    }
}