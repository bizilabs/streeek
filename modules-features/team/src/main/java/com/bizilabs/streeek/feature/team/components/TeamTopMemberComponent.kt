package com.bizilabs.streeek.feature.team.components

import android.R.attr.text
import android.R.attr.visible
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QuestionMark
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.resources.SafiResources

@Composable
fun TeamTopMemberComponent(
    isFirst: Boolean,
    modifier: Modifier = Modifier,
    member: TeamMemberDomain? = null,
) {
    val containerColor = if (isFirst) Color(0xFFE6A817) else MaterialTheme.colorScheme.success
    val contentColor = if (isFirst) Color.Black else MaterialTheme.colorScheme.onSuccess

    SafiCenteredColumn(
        modifier = modifier,
    ) {
        AnimatedContent(
            targetState = member?.account,
            label = "animate member",
        ) { account ->
            when {
                account != null -> {
                    Box {
                        AsyncImage(
                            modifier =
                                Modifier
                                    .padding(top = 48.dp, bottom = 12.dp)
                                    .clip(CircleShape)
                                    .border(BorderStroke(2.dp, containerColor), CircleShape)
                                    .size(if (isFirst) 120.dp else 90.dp),
                            model = account.avatarUrl,
                            contentDescription = "user avatar url",
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            modifier = Modifier.align(Alignment.TopCenter),
                        ) {
                            AnimatedVisibility(visible = isFirst) {
                                Icon(
                                    modifier = Modifier.size(48.dp),
                                    painter = painterResource(SafiResources.Drawables.Crown),
                                    contentDescription = "crown",
                                    tint = containerColor,
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor,
                                ),
                        ) {
                            Text(
                                modifier =
                                    Modifier
                                        .padding(
                                            vertical = 4.dp,
                                            horizontal = 8.dp,
                                        ),
                                text = member?.rank.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                else -> {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        SafiCenteredColumn(
                            modifier =
                                Modifier
                                    .padding(top = 48.dp, bottom = 12.dp)
                                    .clip(CircleShape)
                                    .border(BorderStroke(2.dp, containerColor), CircleShape)
                                    .size(if (isFirst) 120.dp else 90.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(0.25f)),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.QuestionMark,
                                contentDescription = "unknown member",
                            )
                        }
                    }
                }
            }
        }

        SafiCenteredColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
        ) {
            if (member?.account?.role?.isAdmin == true) {
                Text(
                    modifier =
                        Modifier
                            .padding(vertical = 4.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.success)
                            .padding(horizontal = 8.dp),
                    text = member.account.role.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSuccess,
                )
            }
            Text(
                text = member?.account?.username ?: "",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = member?.let { "${it.points} EXP" } ?: "",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
