package com.bizilabs.streeek.lib.remote.helpers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
inline fun <reified T> T.asJson() = Json.encodeToString(this)

fun createJson() = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
    explicitNulls = false
    classDiscriminator = "#class"
}