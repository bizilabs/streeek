package com.bizilabs.streeek.lib.design.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiBottomInfoComponent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    action: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                contentColor = contentColor,
                containerColor = containerColor,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(modifier = Modifier.padding(8.dp)) {
                action?.invoke()
            }
        }
    }
}

@Composable
fun SafiBottomInfoComponent(
    title: @Composable () -> Unit,
    message: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    action: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                contentColor = contentColor,
                containerColor = containerColor,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .weight(1f),
            ) {
                title()
                message()
            }
            Box(modifier = Modifier.padding(8.dp)) {
                action?.invoke()
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiBottomInfoComponentPreview() {
    SafiTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                SafiBottomInfoComponent(title = "Error", message = "Couldn't get needed details") {
                    TextButton(onClick = {}) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiBottomInfoComponentErrorPreview() {
    SafiTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                SafiBottomInfoComponent(
                    title = "Error",
                    message = "Couldn't get needed details",
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ) {
                    TextButton(onClick = {}) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiBottomInfoComponentSuccessPreview() {
    SafiTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                SafiBottomInfoComponent(
                    title = "Error",
                    message = "Couldn't get needed details",
                    contentColor = MaterialTheme.colorScheme.onSuccess,
                    containerColor = MaterialTheme.colorScheme.success,
                ) {
                    TextButton(onClick = {}) {
                        Text(
                            text = "Continue",
                            color = MaterialTheme.colorScheme.onSuccess,
                        )
                    }
                }
            }
        }
    }
}
