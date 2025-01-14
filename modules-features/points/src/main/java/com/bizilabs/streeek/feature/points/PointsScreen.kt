package com.bizilabs.streeek.feature.points

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader

val ScreenPoints = screenModule { register<SharedScreen.Points> { PointsScreen } }

object PointsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        PointsScreenContent { navigator?.pop() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsScreenContent(onClickNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                },
                title = {
                    SafiTopBarHeader(
                        title = "Arcane Knowledge",
                        subtitle = "Learn how to earn experience points (EXP)."
                    )
                }
            )
        },
    ) { innerPadding ->

    }
}