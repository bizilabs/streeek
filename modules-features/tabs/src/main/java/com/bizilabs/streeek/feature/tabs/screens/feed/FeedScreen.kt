package com.bizilabs.streeek.feature.tabs.screens.feed

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.helpers.SetupStatusBarColor
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.dayShort
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.resources.SafiResources
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.core.WeekDay
import kotlinx.datetime.LocalDate

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        SetupStatusBarColor(color = MaterialTheme.colorScheme.surface)
        val screenModel: FeedScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val date by screenModel.date.collectAsStateWithLifecycle()
        val contributions by screenModel.contributions.collectAsStateWithLifecycle(emptyList())
        FeedScreenContent(
            state = state,
            date = date,
            contributions = contributions,
            onClickDate = screenModel::onClickDate
        )
    }
}

@Composable
fun FeedScreenContent(
    state: FeedScreenState,
    date: LocalDate,
    contributions: List<ContributionDomain>,
    onClickDate: (LocalDate) -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    FeedHeader(selectedDate = date, state = state)
                    WeekCalendar(
                        dayContent = { weekDay ->
                            CalendarItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                day = weekDay,
                                state = state,
                                selectedDate = date,
                                onClickDate = onClickDate
                            )
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    ) { paddingValues ->
        val context = LocalContext.current
        FeedContent(
            paddingValues = paddingValues,
            state = state,
            context = context,
            contributions = contributions
        )
    }
}

@Composable
private fun FeedContent(
    paddingValues: PaddingValues,
    state: FeedScreenState,
    contributions: List<ContributionDomain>,
    context: Context
) {
    AnimatedContent(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxSize(),
        targetState = state.isFetchingContributions,
        label = "list_animation"
    ) {
        when (it) {
            true -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            false -> {
                when {
                    contributions.isEmpty() -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.PushPin,
                                title = "No Contributions Found",
                                description = "You haven't been busy today... Push some few commits!"
                            )
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(contributions) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            it.githubEventType,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground.copy(
                                            0.2f
                                        )
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = it.createdAt.asString()
                                                ?: "Some Date..."
                                        )
                                        Text(text = "ID : ${it.id} > git : ${it.githubEventId}")
                                        SafiCenteredRow {
                                            Text(text = it.githubEventType)
                                            Text(text = " > ", fontSize = 24.sp)
                                            Text(text = it.githubEventRepo.name)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}

@Composable
private fun CalendarItem(
    selectedDate: LocalDate,
    state: FeedScreenState,
    day: WeekDay,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit
) {
    val date = day.date
    val isSelected = selectedDate == date
    val border = if (isSelected)
        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    else
        BorderStroke(0.dp, Color.Transparent)
    Card(
        modifier = modifier,
        onClick = { onClickDate(date) },
        border = border,
        colors = CardDefaults.cardColors(
            contentColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                0.5f
            ),
            containerColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
        )
    ) {
        SafiCenteredColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = date.dayShort,
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
            Text(
                text = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FeedHeader(
    selectedDate: LocalDate,
    state: FeedScreenState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(28.dp),
            onClick = {},
            shape = RoundedCornerShape(20),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(SafiResources.Drawables.Icon),
                contentDescription = "app_logo",
                contentScale = ContentScale.Crop
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() },
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier
                .padding(16.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(20)),
            model = state.account?.avatarUrl,
            contentDescription = "user avatar url",
        )

    }
}
