package com.bizilabs.streeek.feature.tabs.screens.teams.components

import android.R.attr.contentDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.resources.SafiResources

@Composable
fun TeamTopMemberComponent(
    isFirst: Boolean,
    name: String = "MamboBryan",
    exp: String = "500 EXP",
    rank: String = "1",
    image: String = "https://avatars.githubusercontent.com/u/40160345?v=4",
    modifier: Modifier = Modifier
) {

    val containerColor = if (isFirst) Color(0xFFE6A817) else MaterialTheme.colorScheme.success
    val contentColor = if (isFirst) Color.Black else MaterialTheme.colorScheme.onSuccess

    SafiCenteredColumn(
        modifier = modifier
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .padding(top = 48.dp, bottom = 12.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, containerColor), CircleShape)
                    .size(if (isFirst) 120.dp else 90.dp),
                model = image,
                contentDescription = "user avatar url",
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                AnimatedVisibility(visible = isFirst) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(SafiResources.Drawables.Crown),
                        contentDescription = "crown",
                        tint = containerColor
                    )
                }
            }

            Card(
                modifier = Modifier.align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            vertical = 4.dp,
                            horizontal = 8.dp
                        ),
                    text = rank,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        SafiCenteredColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = exp, style = MaterialTheme.typography.labelSmall)
        }
    }
}
