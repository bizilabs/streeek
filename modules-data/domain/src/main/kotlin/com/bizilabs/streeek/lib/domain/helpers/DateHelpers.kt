package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

object DateFormats {
    const val ISO_8601_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"
    const val YYYY_MM_dd_T_HH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    const val YYYY_MM_ddTHH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss"
    const val YYYY_MM_dd = "yyyy-MM-dd"
    const val YYYY_MM_dd_space_HH_mm_ss_SSSSSS = "yyyy-MM-dd HH:mm:ss.SSSSSS"
    const val YYYY_MM_dd_T_HH_mm_ss_SSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    const val YYYY_MM_dd_space_HH_mm_ss_XXX = "yyyy-MM-dd HH:mm:ssXXX"
    const val YYYY_MM_dd_T_HH_mm_ss_SSSSSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX"
    const val YYYY_MM_dd_T_HH_mm_ss_SSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSS"
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

val LocalDateTime.asSystem
    get() = toInstant(TimeZone.currentSystemDefault())

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.asDate(format: String = DateFormats.ISO_8601_Z): Instant? {
    return tryOrNull {
        val formatter = DateTimeComponents.Format { byUnicodePattern(format) }
        Instant.parse(this, formatter).let {
            Timber.d("Trying to parse String to Instant :\nVALUE = $this\nFORMAT = $format\nOUTPUT = $it")
            it
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.asLocalDate(format: String = DateFormats.YYYY_MM_dd): LocalDate? {
    Timber.d("Trying to parse String to LocalDate :\nVALUE = $this\nFORMAT= $format")
    return tryOrNull {
        val formatter = LocalDate.Format { byUnicodePattern(format) }
        LocalDate.parse(this, formatter)
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.asLocalDateTime(format: String = DateFormats.YYYY_MM_dd): LocalDateTime? {
    Timber.d("Trying to parse String to LocalDate :\nVALUE = $this\nFORMAT= $format")
    return tryOrNull {
        val formatter: DateTimeFormat<LocalDateTime> = LocalDateTime.Format { byUnicodePattern(format) }
        LocalDateTime.parse(this, formatter)
    }
}

fun Instant.asString(format: String = DateFormats.ISO_8601_Z): String? {
    return this.toLocalDateTime(TimeZone.UTC).asString(format = format)
}

fun LocalDate.asLocalDateTime(): LocalDateTime {
    return atTime(0, 0, 0)
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDate.asString(format: String = DateFormats.ISO_8601_Z): String? {
    return tryOrNull {
        val formatter = LocalDate.Format { byUnicodePattern(format) }
        formatter.format(this)
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.asString(format: String = DateFormats.ISO_8601_Z): String? {
    return tryOrNull {
        val formatter = LocalDateTime.Format { byUnicodePattern(format) }
        formatter.format(this)
    }
}

fun LocalDate.isSameDay(date: LocalDate): Boolean {
    return this.minus(date).days == 0
}

fun LocalDate.isPastDue(): Boolean {
    return this.asLocalDateTime().isPastDue()
}

fun LocalDateTime.isPastDue(): Boolean {
    val today = LocalDate(2025, 1, 1).asLocalDateTime().asSystem
    val past = today.minus(this.asSystem)
    return past.inWholeDays < 1
}

val LocalDate.dayShort
    get() = this.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
