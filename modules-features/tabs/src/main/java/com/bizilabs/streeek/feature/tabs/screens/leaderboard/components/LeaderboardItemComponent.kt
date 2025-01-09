package com.bizilabs.streeek.feature.tabs.screens.leaderboard.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain

@Composable
fun LeaderboardItemComponent(member: LeaderboardAccountDomain, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier =
        modifier
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        onClick = {
            Toast.makeText(
                context,
                "Coming soon...\nYou'll be able to stalk ${member.account.username}",
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        SafiCenteredRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Card(
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(20),
                border =
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                AsyncImage(
                    modifier =
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(20)),
                    model = member.account.avatarUrl,
                    contentDescription = "user avatar url",
                    contentScale = ContentScale.Crop,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = member.account.username,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Text(
                    text =
                    buildString {
                        append(member.rank.points)
                        append(" EXP")
                    },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.padding(16.dp),
                text = member.rank.position.asRank(),
                fontSize =
                if (member.rank.position < 100) {
                    MaterialTheme.typography.titleLarge.fontSize
                } else {
                    MaterialTheme.typography.bodyLarge.fontSize
                },
            )
        }
    }
}