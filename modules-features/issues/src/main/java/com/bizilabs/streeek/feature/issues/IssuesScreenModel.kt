package com.bizilabs.streeek.feature.issues

import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.domain.repositories.IssueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class IssuesScreenState(
    val issue: IssueDomain? = null,
    val isLoading: Boolean = false,
)

class IssuesScreenModel(
    private val repository: IssueRepository,
) : StateScreenModel<IssuesScreenState>(IssuesScreenState()) {
    val issues = repository.issues

    private var searchJob: Job? = null

    private val searchQuery = MutableStateFlow("")

    private val _searchResults = MutableStateFlow<PagingData<IssueDomain>>(PagingData.empty())
    val searchResults: StateFlow<PagingData<IssueDomain>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        searchJob?.cancel()

        searchJob =
            screenModelScope.launch {
                searchQuery
                    .debounce(300)
                    .distinctUntilChanged()
                    .onEach { Timber.d("_searchQuery emitted: $it") }
                    .flatMapLatest { query ->
                        _isSearching.value = query.isNotEmpty()
                        flow {
                            try {
                                val resultFlow =
                                    if (query.isNotEmpty()) {
                                        repository.searchIssues(query, isFetchingUserIssues = false)
                                    } else {
                                        repository.getIssues(isFetchingUserIssues = false)
                                    }
                                resultFlow.cachedIn(screenModelScope).collect { pagingData ->
                                    emit(pagingData)
                                }
                            } catch (e: Exception) {
                                emit(PagingData.empty())
                            } finally {
                                if (query == searchQuery.value) {
                                    _isSearching.value = false
                                }
                            }
                        }
                    }
                    .collect { pagingData ->
                        _searchResults.value = pagingData
                    }
            }
    }

    fun onClickIssue(issue: IssueDomain) {
        screenModelScope.launch {
            mutableState.update { it.copy(issue = issue) }
            delay(250)
            mutableState.update { it.copy(issue = null) }
        }
    }
}
