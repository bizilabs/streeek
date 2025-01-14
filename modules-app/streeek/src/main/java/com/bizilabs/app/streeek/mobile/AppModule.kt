package com.bizilabs.app.streeek.mobile

import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule =
    module {
        factory<Int>(named("version_code")) { BuildConfig.VERSION_CODE }
        factory<String>(named("version_name")) { BuildConfig.VERSION_NAME }
    }
