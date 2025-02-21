package com.bizilabs.streeek.lib.common.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifecycleHandler(
    onInitialized: () -> Unit = {},
    onCreated: () -> Unit = {},
    onStarted: () -> Unit = {},
    onResumed: () -> Unit = {},
    onDestroy: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> onDestroy()
            Lifecycle.State.INITIALIZED -> onInitialized()
            Lifecycle.State.CREATED -> onCreated()
            Lifecycle.State.STARTED -> onStarted()
            Lifecycle.State.RESUMED -> onResumed()
        }
    }
}
