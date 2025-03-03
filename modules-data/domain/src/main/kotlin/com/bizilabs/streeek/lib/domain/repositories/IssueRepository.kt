package com.bizilabs.streeek.lib.domain.repositories

import androidx.paging.PagingData
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.EditIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    val issues: Flow<PagingData<IssueDomain>>

    fun getIssues(isFetchingUserIssues: Boolean): Flow<PagingData<IssueDomain>>

    fun searchIssues(
        searchQuery: String,
        isFetchingUserIssues: Boolean,
    ): Flow<PagingData<IssueDomain>>

    fun getIssueComments(issueNumber: Long): Flow<PagingData<CommentDomain>>

    suspend fun getIssue(id: Long): DataResult<IssueDomain>

    suspend fun createIssue(createIssueDomain: CreateIssueDomain): DataResult<IssueDomain>

    suspend fun editIssue(editIssueDomain: EditIssueDomain): DataResult<IssueDomain>

    suspend fun getUsername(): String?
}
