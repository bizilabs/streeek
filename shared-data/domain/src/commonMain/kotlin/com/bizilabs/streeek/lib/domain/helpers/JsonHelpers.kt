package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.serialization.json.Json

val JsonSerializer =
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        prettyPrint = true
    }

inline fun <reified T> T.asJson() = JsonSerializer.encodeToString(this)
