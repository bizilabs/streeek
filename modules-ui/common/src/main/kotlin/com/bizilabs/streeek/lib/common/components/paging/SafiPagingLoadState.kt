package com.bizilabs.streeek.lib.common.components.paging

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiPagingLoadState(
    state: LoadState,
    modifier: Modifier = Modifier,
    error: @Composable ((Throwable) -> Unit)? = null,
    success: @Composable (Boolean) -> Unit = {}
) {
    AnimatedContent(
        modifier = modifier,
        targetState = state,
        label = "paging load state"
    ) { value ->
        when (value) {
            LoadState.Loading -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            is LoadState.Error -> {
                SafiCenteredColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    error?.let { it(value.error) }
                }
            }

            is LoadState.NotLoading -> {
                success(value.endOfPaginationReached)
            }
        }
    }
}


@Preview("Paging Loading")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingContentLoadingPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiPagingLoadState(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                state = LoadState.Loading
            )
        }
    }
}

@Preview("Paging Error")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingContentErrorPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiPagingLoadState(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                state = LoadState.Error(Exception("Couldn't get page details")),
                error = { throwable ->
                    TextButton(onClick = {}) {
                        Text(text = "retry")
                    }
                }
            ) { }
        }
    }
}

@Preview("Paging Success")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingContentSuccessEndOfPaginationPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiPagingLoadState(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                state = LoadState.NotLoading(endOfPaginationReached = true),
            ) { endOfPagination ->
                LazyColumn {
                    items(3) {
                        Card(modifier = Modifier.padding(4.dp), onClick = {}) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                text = it.toString()
                            )
                        }
                    }
                    item {
                        AnimatedVisibility(endOfPagination) {
                            SafiCenteredColumn(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "END OF PAGINATION",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview("Paging Success")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingContentSuccessNotEndOfPaginationPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiPagingLoadState(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                state = LoadState.NotLoading(endOfPaginationReached = false),
            ) { endOfPagination ->
                LazyColumn {
                    items(3) {
                        Card(modifier = Modifier.padding(4.dp), onClick = {}) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                text = it.toString()
                            )
                        }
                    }
                    item {
                        AnimatedVisibility(endOfPagination) {
                            SafiCenteredColumn(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = "END OF PAGINATION")
                            }
                        }
                    }
                }
            }
        }
    }
}