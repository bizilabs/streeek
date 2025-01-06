package com.bizilabs.streeek.feature.issues

import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.lib.domain.repositories.IssuesRepository

data class IssuesScreenState(
    val isLoading: Boolean = false,
)

class IssuesScreenModel(
    private val repository: IssuesRepository,
) : StateScreenModel<IssuesScreenState>(IssuesScreenState()) {
    val issues = repository.issues
}
