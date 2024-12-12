package com.bizilabs.app.streeek.mobile

import com.bizilabs.streeek.lib.data.dataModule
import com.bizilabs.streeek.lib.presentation.MainApplication
import com.bizilabs.streeek.lib.presentation.helpers.initKoin

class MobileApplication : MainApplication() {
    override fun onCreate() {
        super.onCreate()
        initKoin(dataModule)
    }
}