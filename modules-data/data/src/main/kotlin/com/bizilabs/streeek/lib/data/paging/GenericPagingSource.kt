package com.bizilabs.streeek.lib.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.paging.PagingHelpers.START_PAGE
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult

fun <T : Any, R : Any> genericPager(
    getResults: suspend (Int) -> NetworkResult<T>,
    mapper: (T) -> List<R>,
) = Pager(
    config = PagingConfig(pageSize = PagingHelpers.PAGE_SIZE, enablePlaceholders = false),
    pagingSourceFactory = {
        GenericPagingSource(
            getResults = getResults,
            mapper = mapper,
        )
    },
).flow

private class GenericPagingSource<T : Any, R : Any>(
    private val getResults: suspend (Int) -> NetworkResult<T>,
    private val mapper: (T) -> List<R>,
) : PagingSource<Int, R>() {
    override fun getRefreshKey(state: PagingState<Int, R>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, R> {
        val page = params.key ?: START_PAGE
        val result = getResults(page)
        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)
        val data = (result as NetworkResult.Success).data
        val list = mapper(data)
        val nextKey =
            when {
                list.isEmpty() || list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        val previousKey = if (page == START_PAGE) null else page.minus(1)
        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }

}