package com.bizilabs.streeek.lib.remote.helpers

import kotlinx.serialization.json.Json

fun createJson() = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
    explicitNulls = false
    classDiscriminator = "#class"
}