package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object DateFormats {
    const val ISO_8601_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"
    const val YYYY_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
    const val YYYY_MM_dd_space_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    const val EEE_MMM_dd_yyyy_HH_mm = "EEE, MMM dd yyyy HH:mm a"
}

val SystemLocalDateTime: LocalDateTime
    get() = Clock.System.now().datetimeSystem

val UTCLocalDateTime: LocalDateTime
    get() = Clock.System.now().datetimeUTC

val Instant.datetimeUTC
    get() = toLocalDateTime(TimeZone.UTC)

val Instant.datetimeSystem
    get() = toLocalDateTime(TimeZone.currentSystemDefault())

val LocalDateTime.asUTC
    get() = toInstant(TimeZone.UTC)

val LocalDateTime.asSystem
    get() = toInstant(TimeZone.currentSystemDefault())

internal val INCEPTION
    get() = LocalDateTime(2024, 12, 1, 0, 0, 0)

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.asDate(format: String = DateFormats.ISO_8601_Z): Instant? {
    return tryOrNull {
        val formatter = DateTimeComponents.Format { byUnicodePattern(format) }
        Instant.parse(this, formatter)
    }
}

fun Instant.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return this.toLocalDateTime(TimeZone.UTC).asString(format = format)
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDate.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return tryOrNull {
        val formatter = LocalDate.Format { byUnicodePattern(format) }
        formatter.format(this)
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return tryOrNull {
        val formatter = LocalDateTime.Format { byUnicodePattern(format) }
        formatter.format(this)
    }
}

fun LocalDate.isSameDay(date: LocalDate): Boolean {
    return this.minus(date).days == 0
}

fun LocalDate.isPastDue(): Boolean {
    return this.atTime(0,0,0,0).asUTC < INCEPTION.asUTC
}

fun LocalDateTime.isPastDue(): Boolean {
    return this.asUTC < INCEPTION.asUTC
}

val LocalDate.dayShort
    get() = this.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
