package com.bizilabs.streeek.lib.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SafiInfoSection(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    image: Int? = null,
    contentDescription: String = "",
    action: (@Composable () -> Unit)? = null,
) {
    SafiCenteredColumn(
        modifier = modifier.padding(16.dp),
    ) {
        if (image != null) {
            Image(painter = painterResource(id = image), contentDescription = title)
        } else {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            text = title,
            maxLines = 1,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            text = description,
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
        )
        AnimatedVisibility(
            modifier = Modifier.padding(top = 16.dp),
            visible = action != null,
        ) {
            action?.invoke()
        }
    }
}
