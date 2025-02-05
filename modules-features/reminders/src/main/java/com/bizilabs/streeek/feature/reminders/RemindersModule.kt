package com.bizilabs.streeek.feature.reminders

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.feature.reminders.list.ReminderListScreen
import com.bizilabs.streeek.feature.reminders.list.ReminderListScreenModel
import com.bizilabs.streeek.feature.reminders.manager.ReminderManager
import com.bizilabs.streeek.feature.reminders.manager.ReminderManagerImpl
import com.bizilabs.streeek.feature.reminders.single.ReminderScreen
import com.bizilabs.streeek.feature.reminders.single.ReminderScreenModel
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val ScreenReminders =
    screenModule {
        register<SharedScreen.Reminders> { ReminderListScreen }
        register<SharedScreen.Reminder> { parameters ->
            ReminderScreen(
                label = parameters.label,
                day = parameters.day,
                code = parameters.code,
            )
        }
    }

val FeatureModuleReminders =
    module {
        factory<ReminderListScreenModel> {
            ReminderListScreenModel(manager = get(), context = get(), repository = get())
        }
        factory<ReminderScreenModel> { ReminderScreenModel(manager = get(), context = get()) }
        factory<ReminderManager> { ReminderManagerImpl(context = get()) }
    }
