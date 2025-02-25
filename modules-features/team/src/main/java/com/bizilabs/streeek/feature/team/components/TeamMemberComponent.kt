package com.bizilabs.streeek.feature.team.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberAccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberLevelDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain

@Composable
fun TeamMemberComponent(
    member: TeamMemberDomain,
    modifier: Modifier = Modifier,
    onClickMember: (TeamMemberDomain) -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = {
            onClickMember(member)
        },
    ) {
        SafiCenteredRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(20),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
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

            Column {
                if (member.account.role.isAdmin) {
                    Text(
                        modifier =
                        Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.success)
                            .padding(horizontal = 8.dp),
                        text = member.account.role.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSuccess,
                    )
                }
                Text(
                    text = member.account.username,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Text(
                    text =
                    buildString {
                        append(member.points)
                        append(" pts")
                    },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.padding(16.dp),
                text = member.rank.asRank(),
                fontSize =
                if (member.rank < 100) {
                    MaterialTheme.typography.titleLarge.fontSize
                } else {
                    MaterialTheme.typography.bodyLarge.fontSize
                },
            )
        }
    }
}

private val DummyTeamWithMembers =
    TeamWithMembersDomain(
        team =
        TeamDomain(
            id = 1,
            name = "Streeek",
            public = true,
            createdAt = SystemLocalDateTime,
            count = 10,
        ),
        details =
        TeamMemberDetailsDomain(
            role = TeamMemberRole.MEMBER,
            rank = 20,
        ),
        members =
        listOf(
            TeamMemberDomain(
                account =
                TeamMemberAccountDomain(
                    avatarUrl = "",
                    createdAt = SystemLocalDateTime,
                    id = 1,
                    role = TeamMemberRole.MEMBER,
                    username = "mambo",
                ),
                rank = 1,
                points = 1000,
                level =
                TeamMemberLevelDomain(
                    id = 1,
                    name = "kawabanga",
                    number = 1,
                    maxPoints = 1000,
                    minPoints = 0,
                ),
            ),
        ),
    )

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TeamMemberComponentPreview() {
    SafiTheme {
        Surface {
            TeamMemberComponent(
                member = DummyTeamWithMembers.members.first(),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClickMember = {},
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TeamMemberComponentAdminPreview() {
    SafiTheme {
        Surface {
            TeamMemberComponent(
                member =
                DummyTeamWithMembers.members.first().let {
                    it.copy(account = it.account.copy(role = TeamMemberRole.ADMIN))
                },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClickMember = {},
            )
        }
    }
}
