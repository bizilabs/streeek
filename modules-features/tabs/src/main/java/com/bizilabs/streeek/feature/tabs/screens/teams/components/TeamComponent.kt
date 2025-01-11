package com.bizilabs.streeek.feature.tabs.screens.teams.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizilabs.streeek.lib.design.helpers.pastelSurface
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain

@Composable
fun TeamComponent(
    modifier: Modifier = Modifier,
    teamDetails: TeamDetailsDomain,
    onClickAction: () -> Unit = {},
) {
    val (iconColor, iconVector) =
        if (teamDetails.rank.currentIsHigher) {
            Pair(MaterialTheme.colorScheme.success, Icons.Outlined.ArrowUpward)
        } else {
            Pair(MaterialTheme.colorScheme.error, Icons.Outlined.ArrowDownward)
        }
    Card(
        modifier = modifier,
        onClick = onClickAction,
        border =
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(
                    0.2f,
                ),
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.pastelSurface.random()),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = teamDetails.team.name,
                    softWrap = false,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 0.1.sp,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = "User Rank",
                        tint = iconColor,
                    )

                    Text(
                        modifier = Modifier,
                        text = "${teamDetails.rank.current}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 0.1.sp,
                    )
                }
            }
            Column(
                modifier =
                    Modifier
                        .alpha(0.1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "${teamDetails.team.count}",
                    softWrap = false,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 0.1.sp,
                )
                Text(
                    modifier = Modifier,
                    text = "members",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 0.1.sp,
                )
            }
        }
    }
}
