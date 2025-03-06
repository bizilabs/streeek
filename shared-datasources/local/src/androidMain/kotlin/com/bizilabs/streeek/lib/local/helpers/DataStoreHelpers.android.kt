package com.bizilabs.streeek.lib.local.helpers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val DataStoreModule: Module =
    module {
        single<DataStore<Preferences>>(named("local")) { dataStore(get()) }
    }

private fun dataStore(context: Context): DataStore<Preferences> =
    createDataStore(
        producePath = { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath },
    )
