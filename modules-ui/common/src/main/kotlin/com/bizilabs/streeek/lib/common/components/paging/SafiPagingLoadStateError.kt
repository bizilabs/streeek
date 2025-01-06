package com.bizilabs.streeek.lib.common.components.paging

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiPagingLoadStateError(
    throwable: Throwable,
    modifier: Modifier = Modifier,
    defaultError: String = "error",
    retry: () -> Unit
) {
    SafiCenteredColumn(modifier = modifier) {
        Text(
            text = throwable.localizedMessage ?: defaultError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = retry) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "refresh list"
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiPagingLoadStateErrorPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            SafiPagingLoadStateError(
                modifier = Modifier.padding(innerPadding),
                throwable = Exception("Failed to load list")
            ) { }
        }
    }
}

