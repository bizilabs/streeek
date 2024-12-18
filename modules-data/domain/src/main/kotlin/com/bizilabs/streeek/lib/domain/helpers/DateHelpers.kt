package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

object DateFormats {
    const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val EEE_MMM_dd_yyyy_HH_mm = "EEE, MMM dd yyyy HH:mm a"
}

fun String.asDate(format: String = DateFormats.ISO_8601): Instant? {
    return tryOrNull {
        Instant.parse(this)
    }
}

fun Instant.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return this.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).asString(format = format)
}

fun LocalDate.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return tryOrNull {
        this.toString()
    }
}

fun LocalDateTime.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return tryOrNull {
        this.toString()
    }
}

fun LocalDate.isSameDay(date: LocalDate): Boolean {
    return this.minus(date).days == 0
}

val LocalDate.dayShort
    get() = this.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
