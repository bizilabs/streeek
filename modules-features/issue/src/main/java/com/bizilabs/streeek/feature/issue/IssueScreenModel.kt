package com.bizilabs.streeek.feature.issue

import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.components.paging.getPagingDataLoading
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import com.bizilabs.streeek.lib.domain.repositories.IssueRepository
import com.bizilabs.streeek.lib.domain.repositories.LabelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IssueScreenState(
    val number: Long? = null,
    val hasLoadedFromNavigation: Boolean = false,
    val title: String = "",
    val description: String = "",
    val isSelectingLabels: Boolean = false,
    val labels: List<LabelDomain> = emptyList(),
    val labelsState: FetchListState<LabelDomain> = FetchListState.Loading,
    val issueState: FetchState<IssueDomain> = FetchState.Loading,
    val dialogState: DialogState? = null,
) {
    val isCreateActionEnabled: Boolean
        get() = title.isNotBlank()
}

class IssueScreenModel(
    private val issueRepository: IssueRepository,
    private val labelRepository: LabelRepository,
) : StateScreenModel<IssueScreenState>(IssueScreenState()) {
    private val stateNumber: MutableStateFlow<Long?> = MutableStateFlow(null)
    val comments: Flow<PagingData<CommentDomain>> =
        stateNumber.flatMapLatest {
            when (it) {
                null -> MutableStateFlow(getPagingDataLoading())
                else -> issueRepository.getIssueComments(issueNumber = it)
            }
        }

    private fun getLabels(forceFetch: Boolean) {
        screenModelScope.launch {
            if (forceFetch.not() && state.value.labelsState is FetchListState.Success) return@launch
            mutableState.update { it.copy(labelsState = FetchListState.Loading) }
            val update =
                when (val result = labelRepository.getLabels(page = 1)) {
                    is DataResult.Error -> FetchListState.Error(result.message)
                    is DataResult.Success -> {
                        if (result.data.isNotEmpty()) {
                            FetchListState.Success(result.data)
                        } else {
                            FetchListState.Empty
                        }
                    }
                }
            mutableState.update { it.copy(labelsState = update) }
        }
    }

    private fun getIssue() {
        val id = state.value.number ?: return
        screenModelScope.launch {
            mutableState.update { it.copy(issueState = FetchState.Loading) }
            val update =
                when (val result = issueRepository.getIssue(id = id)) {
                    is DataResult.Error -> FetchState.Error(result.message)
                    is DataResult.Success -> FetchState.Success(result.data)
                }
            mutableState.update { it.copy(issueState = update) }
        }
    }

    private fun createIssue() {
        val data = state.value
        val title = data.title
        val description = data.description
        val labels = data.labels.map { it.name }
        if (title.isBlank()) return
        screenModelScope.launch {
            val issue = CreateIssueDomain(title = title, body = description, labels = labels)
            mutableState.update { it.copy(dialogState = DialogState.Loading()) }
            val update =
                when (val result = issueRepository.createIssue(createIssueDomain = issue)) {
                    is DataResult.Error ->
                        DialogState.Error(
                            title = "Error",
                            message = result.message,
                        )

                    is DataResult.Success -> {
                        val number = result.data.number
                        mutableState.update { it.copy(number = number) }
                        stateNumber.update { number }
                        getIssue()
                        DialogState.Success(
                            title = "Success",
                            message = "Issue created successfully",
                        )
                    }
                }
            mutableState.update { it.copy(dialogState = update) }
        }
    }

    fun onClickCreateIssue() {
        createIssue()
    }

    fun onValueChangeId(id: Long?) {
        if (state.value.hasLoadedFromNavigation.not()) {
            mutableState.update { it.copy(number = id, hasLoadedFromNavigation = true) }
            stateNumber.update { id }
            getIssue()
        }
    }

    fun onValueChangeTitle(title: String) {
        mutableState.update { it.copy(title = title) }
    }

    fun onValueChangeDescription(description: String) {
        mutableState.update { it.copy(description = description) }
    }

    fun onClickOpenLabels() {
        mutableState.update { it.copy(isSelectingLabels = true) }
        getLabels(forceFetch = false)
    }

    fun onClickInsertLabel(labelDomain: LabelDomain) {
        val labels = state.value.labels.toMutableList()
        if (labels.contains(labelDomain)) {
            labels.remove(labelDomain)
        } else {
            labels.add(labelDomain)
        }
        mutableState.update { it.copy(labels = labels) }
    }

    fun onClickRemoveLabel(label: LabelDomain) {
        val labels = state.value.labels.toMutableList()
        labels.remove(label)
        mutableState.update { it.copy(labels = labels) }
    }

    fun onClickLabelsDismissSheet() {
        mutableState.update { it.copy(isSelectingLabels = false) }
    }

    fun onClickLabelsRetry() {
        getLabels(forceFetch = true)
    }

    fun onClickDismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }
}
