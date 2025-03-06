package com.bizilabs.streeek.lib.local

import com.bizilabs.streeek.lib.local.database.StreeekDatabase
import com.bizilabs.streeek.lib.local.helpers.DataStoreModule
import com.bizilabs.streeek.lib.local.helpers.DatabaseModule
import com.bizilabs.streeek.lib.local.helpers.getDatabase
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionDAO
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSource
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.leaderboard.LeaderboardLocalSource
import com.bizilabs.streeek.lib.local.sources.leaderboard.LeaderboardLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.level.LevelDAO
import com.bizilabs.streeek.lib.local.sources.level.LevelLocalSource
import com.bizilabs.streeek.lib.local.sources.level.LevelLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationDAO
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationLocalSource
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSourceImpl
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSourceImpl
import com.bizilabs.streeek.lib.local.sources.reminder.ReminderLocalSource
import com.bizilabs.streeek.lib.local.sources.reminder.ReminderLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.team.TeamLocalSource
import com.bizilabs.streeek.lib.local.sources.team.TeamLocalSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val LocalModule =
    module {
        includes(DataStoreModule, DatabaseModule)
        single<StreeekDatabase> { getDatabase(builder = get()) }
        // sources
        single<PreferenceSource> { PreferenceSourceImpl(dataStore = get(named("local"))) }
        single<LocalPreferenceSource> { LocalPreferenceSourceImpl(source = get()) }
        single<AccountLocalSource> { AccountLocalSourceImpl(preferenceSource = get()) }
        single<TeamLocalSource> { TeamLocalSourceImpl(source = get()) }
        single<LeaderboardLocalSource> { LeaderboardLocalSourceImpl(source = get()) }
        single<ReminderLocalSource> { ReminderLocalSourceImpl(source = get()) }
        // contributions
        single<ContributionDAO> { get<StreeekDatabase>().contributions }
        single<ContributionsLocalSource> {
            ContributionsLocalSourceImpl(dao = get(), preferenceSource = get())
        }
        // levels
        single<LevelDAO> { get<StreeekDatabase>().levels }
        single<LevelLocalSource> { LevelLocalSourceImpl(source = get()) }
        // notifications
        single<NotificationDAO> { get<StreeekDatabase>().notifications }
        single<NotificationLocalSource> { NotificationLocalSourceImpl(dao = get()) }
    }
