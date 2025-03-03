package com.bizilabs.streeek.lib.remote.helpers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

inline fun <reified T> T.asJson() = Json.encodeToString(this)

fun createJson() =
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = true
        classDiscriminator = "#class"
    }

internal inline fun <reified T> T.asJsonObject() = createJson().encodeToJsonElement(this).jsonObject
