package com.bizilabs.streeek.lib.domain.helpers

import io.github.aakira.napier.Napier

fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        Napier.e(e) { "Try Or Null Exception" }
        null
    }
}
