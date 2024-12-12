package com.bizilabs.streeek.lib.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object Landing : SharedScreen()
    object Authentication : SharedScreen()
    object Tabs : SharedScreen()
}