package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.CommentDTO
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSource
import timber.log.Timber

class IssueCommentPagingSource(
    private val issueNumber: Long,
    private val issuesRemoteSource: IssuesRemoteSource,
) : PagingSource<Int, CommentDomain>() {
    override fun getRefreshKey(state: PagingState<Int, CommentDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentDomain> {
        val page = params.key ?: PagingHelpers.START_PAGE

        val result = issuesRemoteSource.fetchIssueComments(number = issueNumber, page = page)

        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data

        val list = data.map(CommentDTO::toDomain)

        val nextKey =
            when {
                list.isEmpty() -> null
                list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        Timber.d("Comments -> $list")

        val previousKey = if (page == PagingHelpers.START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }
}
