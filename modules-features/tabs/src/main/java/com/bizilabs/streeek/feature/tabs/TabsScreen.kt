package com.bizilabs.streeek.feature.tabs

import android.R.attr.text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

val featureTabs = screenModule {
    register<SharedScreen.Tabs> { TabsScreen }
}

object TabsScreen : Screen {
    @Composable
    override fun Content() {
        TabsScreenContent()
    }
}

@Composable
fun TabsScreenContent() {
    Scaffold { paddingValues ->
        SafiCenteredColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(text = "Tabs")
        }
    }
}