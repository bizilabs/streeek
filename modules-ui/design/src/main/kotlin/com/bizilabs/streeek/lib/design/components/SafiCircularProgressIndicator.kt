package com.bizilabs.streeek.lib.design.components

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
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) {
    CircularProgressIndicator(modifier = modifier.size(size))
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SafiCircularProgressIndicatorPreview() {
    SafiTheme {
        Surface {
            SafiCircularProgressIndicator(modifier = Modifier.padding(24.dp))
        }
    }
}
