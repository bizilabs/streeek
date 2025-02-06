package com.bizilabs.streeek.lib.design.components

import android.R.attr.bottom
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.theme.SafiTheme

data class SafiBottomValue(
    val title: String,
    val action: () -> Unit,
)

@Composable
fun SafiBottomAction(
    title: String,
    description: String,
    icon: ImageVector,
    primaryAction: SafiBottomValue,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    secondaryAction: SafiBottomValue? = null,
    onCloseClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.small,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier =
                    Modifier.background(
                        iconTint.copy(alpha = 0.2f),
                        MaterialTheme.shapes.small,
                    ),
            ) {
                SafiShakingBox {
                    Icon(
                        modifier = Modifier.padding(1.dp),
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = primaryAction.action,
                        shape = RoundedCornerShape(5.dp),
                    ) {
                        Text(text = primaryAction.title)
                    }
                    if (secondaryAction != null) {
                        SafiOutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = secondaryAction.action,
                        ) {
                            Text(text = secondaryAction.title)
                        }
                    }
                }
            }
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = title,
                modifier =
                    Modifier
                        .clickable {
                            if (onCloseClick != null) {
                                onCloseClick()
                            }
                        },
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiBottomActionPreview() {
    SafiTheme {
        Scaffold(
            snackbarHost = {
                SafiBottomAction(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    title = "Enable Notifications",
                    description = "We can't seem to send you notifications. Please enable them for a better experience",
                    icon = Icons.Rounded.Notifications,
                    primaryAction = SafiBottomValue("enable") {},
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier =
                        Modifier.fillMaxWidth().padding(8.dp).height(200.dp)
                            .padding(bottom = 100.dp),
                ) { }
            }
        }
    }
}
