package com.bizilabs.streeek.feature.reminders

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.feature.reminders.list.ReminderListScreen
import com.bizilabs.streeek.feature.reminders.list.ReminderListScreenModel
import com.bizilabs.streeek.feature.reminders.single.ReminderScreen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val ScreenReminders = screenModule {
    register<SharedScreen.Reminders> { ReminderListScreen }
    register<SharedScreen.Reminder> { parameters -> ReminderScreen(id = parameters.id) }
}

val FeatureModuleReminders = module  {
    factory<ReminderListScreenModel> { ReminderListScreenModel() }
}
