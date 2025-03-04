package com.bizilabs.streeek.lib.remote.helpers

private const val DATASTORE_DB = "streeek.remote.test.preferences_pb"

fun fakeDataStore() = createDataStore { DATASTORE_DB }
