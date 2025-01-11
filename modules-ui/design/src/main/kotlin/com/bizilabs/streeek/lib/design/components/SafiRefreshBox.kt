package com.bizilabs.streeek.lib.design.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
@ExperimentalMaterial3Api
fun SafiRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state,
            containerColor = MaterialTheme.colorScheme.onBackground,
            color = MaterialTheme.colorScheme.background,
        )
    },
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = contentAlignment,
    ) {
        content()
        indicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiRefreshBoxPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiRefreshBox(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                isRefreshing = false,
                onRefresh = {},
            ) {
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiRefreshBoxLoadingPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiRefreshBox(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                isRefreshing = true,
                onRefresh = {},
            ) {
            }
        }
    }
}
