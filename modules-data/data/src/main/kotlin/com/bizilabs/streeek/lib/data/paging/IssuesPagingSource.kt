package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.GithubIssueDTO
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSource
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class IssuesPagingSource(
    private val isFetchingUserIssues: Boolean,
    private val accountLocalSource: AccountLocalSource,
    private val issuesRemoteSource: IssuesRemoteSource,
) : PagingSource<Int, IssueDomain>() {
    override fun getRefreshKey(state: PagingState<Int, IssueDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IssueDomain> {
        val page = params.key ?: PagingHelpers.START_PAGE

        val username =
            accountLocalSource.account.firstOrNull()?.username
                ?: return LoadResult.Error(Exception("Couldn't get logged in account"))

        val result = when(isFetchingUserIssues){
            true -> issuesRemoteSource.fetchUserIssues(username = username, page = page)
            false -> issuesRemoteSource.fetchIssues(page = page)
        }

        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data

        val list = data.map(GithubIssueDTO::toDomain)

        val nextKey =
            when {
                list.isEmpty() -> null
                list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        Timber.d("Issues -> $list")

        val previousKey = if (page == PagingHelpers.START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }
}
