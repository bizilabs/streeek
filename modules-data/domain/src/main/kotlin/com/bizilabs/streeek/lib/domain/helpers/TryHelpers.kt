package com.bizilabs.streeek.lib.domain.helpers

import timber.log.Timber

fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "Try Or Null Exception")
        null
    }
}
