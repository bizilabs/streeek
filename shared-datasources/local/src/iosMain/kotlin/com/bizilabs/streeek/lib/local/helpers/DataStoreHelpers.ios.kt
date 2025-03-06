package com.bizilabs.streeek.lib.local.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val DataStoreModule: Module =
    module {
        single<DataStore<Preferences>>(named("local")) { dataStore() }
    }

fun dataStore(): DataStore<Preferences> =
    createDataStore(
        producePath = {
            documentDirectory() + "/$DATASTORE_FILE_NAME"
        },
    )
