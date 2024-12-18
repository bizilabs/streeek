package com.bizilabs.streeek.feature.tabs.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import cafe.adriel.voyager.core.screen.Screen
import com.bizilabs.streeek.lib.common.helpers.BaseActivity
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

object TeamsScreen : Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current as BaseActivity
        context.restartActivity()
        TeamsScreenContent()
    }
}

@Composable
fun TeamsScreenContent() {
    Scaffold { paddingValues ->
        SafiCenteredColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(text = "Teams")
        }
    }
}
