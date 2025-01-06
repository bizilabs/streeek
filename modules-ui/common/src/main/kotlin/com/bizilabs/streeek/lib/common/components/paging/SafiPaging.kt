package com.bizilabs.streeek.lib.common.components.paging

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems

fun <T : Any> getPagingDataLoading(): PagingData<T> =
    PagingData.from(
        emptyList(),
        sourceLoadStates =
            LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
    )

fun <T : Any> getPagingData(data: List<T> = emptyList()): PagingData<T> =
    PagingData.from(
        data = data,
        sourceLoadStates =
            LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
    )

@Composable
fun <T : Any> SafiPagingComponent(
    data: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    prependError: @Composable ((Throwable) -> Unit)? = null,
    prependSuccess: @Composable (Boolean) -> Unit = {},
    refreshLoading: @Composable () -> Unit = { SafiPagingLoadStateLoading() },
    refreshError: @Composable ((Throwable) -> Unit)? = null,
    refreshEmpty: @Composable () -> Unit = {},
    appendError: @Composable ((Throwable) -> Unit)? = null,
    appendSuccess: @Composable (Boolean) -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    SafiPagingLoadStates(
        modifier = modifier,
        states = data.loadState,
        count = data.itemCount,
        prependError = prependError,
        prependSuccess = prependSuccess,
        refreshLoading = refreshLoading,
        refreshError = refreshError,
        refreshEmpty = refreshEmpty,
        appendError = appendError,
        appendSuccess = appendSuccess,
    ) { index ->
        val item = data[index]
        item?.let { content(it) }
    }
}
