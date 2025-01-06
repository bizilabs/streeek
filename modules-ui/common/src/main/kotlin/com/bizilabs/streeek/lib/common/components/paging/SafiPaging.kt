package com.bizilabs.streeek.lib.common.components.paging

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> SafiPaging(
    data: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    prependError: @Composable ((Throwable) -> Unit)? = null,
    prependSuccess: @Composable (Boolean) -> Unit = {},
    refreshLoading: @Composable () -> Unit = { SafiPagingLoadStateLoading() },
    refreshError: @Composable ((Throwable) -> Unit)? = null,
    refreshEmpty: @Composable () -> Unit = {},
    appendError: @Composable ((Throwable) -> Unit)? = null,
    appendSuccess: @Composable (Boolean) -> Unit = {},
    content: @Composable (T) -> Unit
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
        appendSuccess = appendSuccess
    ) { index ->
        val item = data[index]
        item?.let { content(it) }
    }
}
