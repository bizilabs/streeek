package com.bizilabs.streeek.lib.common.components.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

@Composable
fun SafiPagingLoadStates(
    count: Int,
    states: CombinedLoadStates,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    prependError: @Composable ((Throwable) -> Unit)?,
    prependSuccess: @Composable (Boolean) -> Unit,
    refreshLoading: @Composable () -> Unit,
    refreshError: @Composable ((Throwable) -> Unit)?,
    refreshEmpty: @Composable () -> Unit,
    appendError: @Composable ((Throwable) -> Unit)?,
    appendSuccess: @Composable (Boolean) -> Unit,
    content: @Composable (Int) -> Unit,
) {
    val prepend = states.prepend
    val refresh = states.refresh
    val append = states.append

    LazyColumn(modifier = modifier, state = lazyListState) {
        item {
            SafiPagingLoadState(
                state = prepend,
                error = prependError,
                success = prependSuccess,
            )
        }
        when (refresh) {
            is LoadState.Error -> {
                item {
                    SafiCenteredColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                    ) {
                        refreshError?.invoke(refresh.error)
                    }
                }
            }

            LoadState.Loading -> {
                item {
                    SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                        refreshLoading()
                    }
                }
            }

            is LoadState.NotLoading -> {
                if (count == 0) {
                    item {
                        SafiCenteredColumn(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                        ) {
                            refreshEmpty()
                        }
                    }
                } else {
                    items(count) { index ->
                        content(index)
                    }
                }
            }
        }
        item {
            SafiPagingLoadState(
                state = append,
                error = appendError,
                success = appendSuccess,
            )
        }
    }
}
