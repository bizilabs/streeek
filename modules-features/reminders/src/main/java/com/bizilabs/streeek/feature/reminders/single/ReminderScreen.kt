package com.bizilabs.streeek.feature.reminders.single

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReminderScreen(val label: String, val day: Int, val code: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        ReminderScreenContent(label, onDismiss = {
            navigator?.popAll()
        })
    }
}

@Composable
fun ReminderScreenContent(
    label: String,
    onDismiss: () -> Unit,
) {
//    Scaffold { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            Text("Welcome to this screen: $label")
//        }
//    }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = -200f // Threshold to trigger dismiss
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val animatedOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "Swipe Animation",
    )

    fun onDismiss() {
        scope.launch {
            // Perform any action before closing
            Toast.makeText(context, "Alarm dismissed!", Toast.LENGTH_SHORT)
                .show() // Replace with actual action (e.g., stopping alarm sound)
            delay(200) // Optional delay for smooth UX
            onDismiss()
//            navController.popBackStack() // Close screen
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .offset(y = animatedOffset.dp) // Move screen based on drag
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            dragOffset =
                                (dragOffset + dragAmount).coerceAtMost(0f) // Allow only upward drag
                        },
                        onDragEnd = {
                            if (dragOffset < dismissThreshold) {
                                onDismiss() // Trigger dismiss when swiped far enough
                            } else {
                                dragOffset = 0f // Reset position if not fully swiped
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
            Text(
                text = "Alarm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "7:30 AM",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Snooze logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = CircleShape,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(50)),
            ) {
                Text(
                    text = "Snooze",
                    fontSize = 16.sp,
                    color = Color.White,
                )
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
                painter = painterResource(id = android.R.drawable.arrow_up_float), // Replace with a better arrow icon
                contentDescription = "Swipe Up",
                tint = Color.Black,
            )

            Text(
                text = "Dismiss",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black,
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderScreenContentPreview(modifier: Modifier = Modifier) {
    ReminderScreenContent("", {})
}
