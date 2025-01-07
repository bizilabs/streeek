package com.bizilabs.streeek.feature.issues

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.domain.repositories.IssueRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IssuesScreenState(
    val issue: IssueDomain? = null,
    val isLoading: Boolean = false,
)

class IssuesScreenModel(
    private val repository: IssueRepository,
) : StateScreenModel<IssuesScreenState>(IssuesScreenState()) {
    val issues = repository.issues

    fun onClickIssue(issue: IssueDomain) {
        screenModelScope.launch {
            mutableState.update { it.copy(issue = issue) }
            delay(250)
            mutableState.update { it.copy(issue = null) }
        }
    }
}
