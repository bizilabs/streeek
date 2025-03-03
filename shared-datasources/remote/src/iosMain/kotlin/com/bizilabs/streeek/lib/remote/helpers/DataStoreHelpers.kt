package com.bizilabs.streeek.lib.remote.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val DataStoreModule: Module =
    module {
        single<DataStore<Preferences>>(named("remote")) { dataStore() }
    }

@OptIn(ExperimentalForeignApi::class)
fun dataStore(): DataStore<Preferences> =
    createDataStore(
        producePath = {
            val documentDirectory: NSURL? =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            requireNotNull(documentDirectory).path + "/$DATASTORE_FILE_NAME"
        },
    )
