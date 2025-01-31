package com.bizilabs.streeek.feature.tabs.screens.notifications.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.NotificationDomain

@Composable
fun NotificationItemComponent(
    notification: NotificationDomain,
    onClickNotification: (NotificationDomain) -> Unit,
) {
    val isRead = notification.readAt != null
    val containerColor =
        if (isRead) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.success.copy(alpha = 0.1f)
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClickNotification(notification) },
        shape = RectangleShape,
        colors =
            CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
                containerColor = containerColor,
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = notification.message)
            }
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
        }
    }
}
