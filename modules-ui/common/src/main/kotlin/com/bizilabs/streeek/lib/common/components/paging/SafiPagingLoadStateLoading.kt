package com.bizilabs.streeek.lib.common.components.paging

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiPagingLoadStateLoading(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    SafiCenteredColumn(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier
            .padding(16.dp)
            .size(size)
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingLoadStateLoadingPreview() {
    SafiTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SafiPagingLoadStateLoading()
        }
    }
}
