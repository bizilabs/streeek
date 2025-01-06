package com.bizilabs.streeek.lib.common.helpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

@Composable
fun StartActivity(intent: Intent) {
    val activity = LocalContext.current as ComponentActivity
    activity.startActivity(intent)
}

abstract class BaseActivity : ComponentActivity() {
    fun restartActivity() {
        with(this) {
            finish()
            startActivity(Intent(this, this::class.java))
        }
    }
}
