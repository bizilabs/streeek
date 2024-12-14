package com.bizilabs.streeek.lib.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSourceImpl
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "streeek.local")

val LocalModule = module {
    single<DataStore<Preferences>>(named("local")) { get<Context>().dataStore }
    single<PreferenceSource> { PreferenceSourceImpl(dataStore = get(named("local"))) }
    single<AccountLocalSource> { AccountLocalSourceImpl(preferenceSource = get()) }
}