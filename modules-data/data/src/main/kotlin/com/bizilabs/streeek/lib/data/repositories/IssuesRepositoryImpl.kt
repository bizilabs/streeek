package com.bizilabs.streeek.lib.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toDTO
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.IssueCommentPagingSource
import com.bizilabs.streeek.lib.data.paging.IssuesPagingSource
import com.bizilabs.streeek.lib.data.paging.PagingHelpers
import com.bizilabs.streeek.lib.data.paging.SearchIssuesPagingSource
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.EditIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.domain.repositories.IssueRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class IssuesRepositoryImpl(
    private val remoteSource: IssuesRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : IssueRepository {
    override val issues: Flow<PagingData<IssueDomain>>
        get() =
            Pager(
                config =
                    PagingConfig(
                        pageSize = PagingHelpers.PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                pagingSourceFactory = {
                    IssuesPagingSource(
                        isFetchingUserIssues = false,
                        accountLocalSource = accountLocalSource,
                        issuesRemoteSource = remoteSource,
                    )
                },
            ).flow

    override suspend fun getIssue(id: Long): DataResult<IssueDomain> {
        return remoteSource.fetchIssue(number = id).asDataResult { it.toDomain() }
    }

    override fun getIssues(isFetchingUserIssues: Boolean): Flow<PagingData<IssueDomain>> =
        Pager(
            config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                IssuesPagingSource(
                    isFetchingUserIssues = isFetchingUserIssues,
                    accountLocalSource = accountLocalSource,
                    issuesRemoteSource = remoteSource,
                )
            },
        ).flow

    override suspend fun getUsername(): String {
        return accountLocalSource.account.firstOrNull()?.username ?: ""
    }

    override fun searchIssues(
        searchQuery: String,
        isFetchingUserIssues: Boolean,
    ): Flow<PagingData<IssueDomain>> =
        Pager(
            config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchIssuesPagingSource(
                    searchQuery = searchQuery,
                    isFetchingUserIssues = isFetchingUserIssues,
                    issuesRemoteSource = remoteSource,
                )
            },
        ).flow

    override fun getIssueComments(issueNumber: Long): Flow<PagingData<CommentDomain>> =
        Pager(
            config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                IssueCommentPagingSource(
                    issueNumber = issueNumber,
                    issuesRemoteSource = remoteSource,
                )
            },
        ).flow

    override suspend fun createIssue(createIssueDomain: CreateIssueDomain): DataResult<IssueDomain> {
        return remoteSource.createIssue(request = createIssueDomain.toDTO())
            .asDataResult { it.toDomain() }
    }

    override suspend fun editIssue(editIssueDomain: EditIssueDomain): DataResult<IssueDomain> {
        return remoteSource.editIssue(request = editIssueDomain.toDTO())
            .asDataResult { it.toDomain() }
    }
}
