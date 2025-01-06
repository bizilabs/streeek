package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiCenteredColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
    )
}

@Composable
fun SafiCenteredRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = content,
    )
}

@Preview
@Composable
private fun SafiCenteredColumnPreview() {
    SafiTheme {
        Scaffold {
            SafiCenteredColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Text(text = "Safi Centered Column")
            }
        }
    }
}

@Preview
@Composable
private fun SafiCenteredRowPreview() {
    SafiTheme {
        Scaffold {
            SafiCenteredRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(it),
            ) {
                Text(text = "Safi CenteredRow")
            }
        }
    }
}
