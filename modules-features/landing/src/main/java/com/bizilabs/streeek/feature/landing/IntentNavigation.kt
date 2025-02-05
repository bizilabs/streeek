package com.bizilabs.streeek.feature.landing

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.domain.models.notifications.asNotificationResult

@Composable
fun getNavigationDestinationFromURI(): List<Screen> {
    val intent = (LocalContext.current as? ComponentActivity)?.intent ?: return emptyList()
    val result = intent.asNotificationResult() ?: return emptyList()
    val uri = result.uri
    if (uri.isBlank()) return emptyList()
    val map = uri.asIntentExtraArguments()
    val action = map["action"]
    if (action == null || !(action.equals("navigate", true))) return emptyList()
    return getNavigationDestination(map = map)
}

private fun String.asIntentExtraArguments(): Map<String, String> {
    val params =
        replace("https://app.streeek.com/v1?", "")
            .split("&")
    val map = mutableMapOf<String, String>()
    params.forEach {
        val keyAndValue = it.split("=")
        val key = keyAndValue.firstOrNull()
        val value = keyAndValue.lastOrNull()
        if (key != null && value != null) {
            map[key] = value
        }
    }
    return map
}

@Composable
private fun getNavigationDestination(map: Map<String, String>): List<Screen> {
    val destination = map["destination"] ?: return emptyList()
    return when (destination) {
        "NOTIFICATIONS" -> listOf(rememberScreen(SharedScreen.Tabs(tab = destination)))
        "ACHIEVEMENTS" -> listOf(rememberScreen(SharedScreen.Tabs(tab = destination)))
        "FEED" -> listOf(rememberScreen(SharedScreen.Tabs))
        "LEADERBOARDS" -> {
            val name = map["name"]?.uppercase()
            if (name == null) {
                listOf(rememberScreen(SharedScreen.Tabs(tab = destination)))
            } else {
                listOf(
                    rememberScreen(SharedScreen.Tabs(tab = destination)),
                    rememberScreen(SharedScreen.Leaderboard(name = name)),
                )
            }
        }

        "TEAMS" -> {
            val teamId = map["teamId"]?.toLongOrNull()
            if (teamId == null) {
                listOf(rememberScreen(SharedScreen.Tabs(tab = destination)))
            } else {
                listOf(
                    rememberScreen(SharedScreen.Tabs(tab = destination)),
                    rememberScreen(SharedScreen.Team(teamId = teamId)),
                )
            }
        }

        "REMINDERS" -> {
            val label = map["label"]
            val day = map["day"]?.toIntOrNull()
            val code = map["code"]?.toIntOrNull()
            if (label != null && day != null && code != null) {
                listOf(
                    rememberScreen(SharedScreen.Tabs(tab = "PROFILE")),
                    rememberScreen(SharedScreen.Reminders),
                    rememberScreen(
                        SharedScreen.Reminder(label = label, day = day, code = code),
                    ),
                )
            } else {
                listOf(rememberScreen(SharedScreen.Tabs(tab = "PROFILE")))
            }
        }

        else -> emptyList()
    }
}
