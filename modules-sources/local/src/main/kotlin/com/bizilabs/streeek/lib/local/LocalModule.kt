package com.bizilabs.streeek.lib.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bizilabs.streeek.lib.local.database.StreeekDatabase
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionDAO
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSource
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSourceImpl
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "streeek.local")

val LocalModule = module {
    single<DataStore<Preferences>>(named("local")) { get<Context>().dataStore }
    // room
    single<StreeekDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = StreeekDatabase::class.java,
            name = StreeekDatabase.DATABASE_NAME
        ).build()
    }
    single<ContributionDAO> { get<StreeekDatabase>().contributions }
    // sources
    single<PreferenceSource> { PreferenceSourceImpl(dataStore = get(named("local"))) }
    single<LocalPreferenceSource> { LocalPreferenceSourceImpl(source = get()) }
    single<AccountLocalSource> { AccountLocalSourceImpl(preferenceSource = get()) }
    single<ContributionsLocalSource> { ContributionsLocalSourceImpl(dao = get()) }
}