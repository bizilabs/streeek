package com.bizilabs.streeek.lib.remote.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private const val DATASTORE_DB = "streeek.remote.test.preferences_pb"

fun fakeDataStore() = TestDataStore.store

private object TestDataStore {
    val store: DataStore<Preferences> = createDataStore { DATASTORE_DB }
}
