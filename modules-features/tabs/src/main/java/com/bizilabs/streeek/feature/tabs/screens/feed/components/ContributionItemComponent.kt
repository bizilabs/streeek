package com.bizilabs.streeek.feature.tabs.screens.feed.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.models.ContributionDomain

@Composable
fun ContributionItemComponent(contribution: ContributionDomain, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        onClick = {
            Toast.makeText(context, contribution.githubEventType, Toast.LENGTH_SHORT).show()
        },
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onBackground.copy(
                0.2f
            )
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Text(
                text = "${contribution.points}",
                modifier = Modifier
                    .alpha(0.25f)
                    .align(Alignment.TopEnd)
                    .rotate(-32f)
                ,
                softWrap = false,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            ) {
                Text(text = contribution.githubEventType)
                Text(text = contribution.githubEventRepo.name)
                Text(text = contribution.createdAt.time.toString())
            }
        }
    }
}
