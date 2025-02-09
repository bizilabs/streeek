package com.bizilabs.streeek.lib.presentation

import android.app.Application
import com.bizilabs.streeek.lib.domain.managers.notifications.initNotificationChannels
import com.bizilabs.streeek.lib.presentation.helpers.initTimber
import com.bizilabs.streeek.lib.presentation.helpers.initVoyager

abstract class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initVoyager()
        initNotificationChannels()
    }
}
