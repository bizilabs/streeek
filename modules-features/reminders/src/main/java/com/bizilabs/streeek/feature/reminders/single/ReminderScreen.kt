package com.bizilabs.streeek.feature.reminders.single

import android.R.attr.contentDescription
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.lib.design.components.SafiShakingBox
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.resources.SafiResources
import kotlinx.datetime.Clock

class ReminderScreen(val label: String, val day: Int, val code: Int) : Screen {
    @Composable
    override fun Content() {
        val activity = LocalContext.current as ComponentActivity
        val screenModel: ReminderScreenModel = getScreenModel()
        screenModel.updateValues(label = label, day = day, code = code)
        val state by screenModel.state.collectAsStateWithLifecycle()
        ReminderScreenContent(
            state = state,
            onClickSnooze = screenModel::onClickSnooze,
            onSwipeDismiss = screenModel::onSwipeDismiss,
            dismiss = { activity.finish() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreenContent(
    state: ReminderScreenState,
    onClickSnooze: () -> Unit,
    onSwipeDismiss: () -> Unit,
    dismiss: () -> Unit,
) {
    if (state.dismiss) dismiss()

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = -200f

    val animatedOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "Swipe Animation",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(SafiResources.Drawables.Logo),
                            contentDescription = "icon",
                        )
                        SafiTopBarHeader(
                            modifier = Modifier.padding(start = 8.dp),
                            title = "Reminder",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .offset(y = animatedOffset.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                dragOffset =
                                    (dragOffset + dragAmount).coerceAtMost(0f)
                            },
                            onDragEnd = {
                                if (dragOffset < dismissThreshold) {
                                    onSwipeDismiss()
                                } else {
                                    dragOffset = 0f
                                }
                            },
                        )
                    },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SafiShakingBox {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Filled.Alarm,
                        contentDescription = "alarm",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = state.label,
                )

                Text(
                    text = Clock.System.now().datetimeSystem.asString(DateFormats.HH_MM) ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayMedium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClickSnooze,
                    modifier = Modifier,
                ) {
                    Text(text = "Snooze")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier =
                    Modifier
                        .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardDoubleArrowUp,
                    contentDescription = "Swipe Up",
                )
                Text(
                    text = "Dismiss",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderScreenContentPreview() {
    SafiTheme {
        ReminderScreenContent(
            state = ReminderScreenState(label = "kawabanga"),
            onClickSnooze = {},
            onSwipeDismiss = {},
        ) {}
    }
}
