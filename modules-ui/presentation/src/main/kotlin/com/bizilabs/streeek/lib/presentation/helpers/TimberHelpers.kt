package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Application
import android.util.Log
import com.bizilabs.streeek.lib.presentation.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.get
import timber.log.Timber

fun Application.initTimber() {
    if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
    } else {
        Timber.plant(FirebaseCrashlyticsTree())
    }
}

private class FirebaseCrashlyticsTree : Timber.Tree() {
    override fun isLoggable(
        tag: String?,
        priority: Int,
    ): Boolean {
        return priority == Log.WARN || priority == Log.ERROR
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        FirebaseCrashlytics.getInstance().log("$tag: $message")
        if (t != null) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
