package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.datetime.Clock
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
    const val YYYY_MM_DDTHH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss"
    const val HH_MM = "HH:mm"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val DD_MM_YYYY = "dd/MM/YYYY"
    const val YYYY_MM_DD_T_HH_MM_SS_SSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
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

val LocalDateTime.datetimeUTC
    get() = toInstant(TimeZone.UTC)

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
fun String.asLocalDate(format: String = DateFormats.YYYY_MM_DD): LocalDate? {
    Timber.d("Trying to parse String to LocalDate :\nVALUE = $this\nFORMAT= $format")
    return tryOrNull {
        val formatter = LocalDate.Format { byUnicodePattern(format) }
        LocalDate.parse(this, formatter)
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun String.asLocalDateTime(format: String = DateFormats.YYYY_MM_DD): LocalDateTime? {
    Timber.d("Trying to parse String to LocalDate :\nVALUE = $this\nFORMAT= $format")
    return tryOrNull {
        val formatter: DateTimeFormat<LocalDateTime> =
            LocalDateTime.Format { byUnicodePattern(format) }
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
    val inception = LocalDate(year = 2025, monthNumber = 1, dayOfMonth = 1).asLocalDateTime().datetimeUTC
    val duration = this.datetimeUTC.minus(inception)
    return duration.inWholeDays < 1
}

val LocalDate.dayShort
    get() = this.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

fun LocalDateTime.toTimeAgo(): String {
    val now = Clock.System.now()
    val instantThis = this.toInstant(TimeZone.UTC)
    val duration = now - instantThis

    return when {
        duration.inWholeMinutes < 1 -> "Just now"
        duration.inWholeMinutes < 60 -> {
            val minutes = duration.inWholeMinutes
            if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
        }
        duration.inWholeHours < 24 -> {
            val hours = duration.inWholeHours
            if (hours == 1L) "1 hour ago" else "$hours hours ago"
        }
        duration.inWholeDays < 7 -> {
            val days = duration.inWholeDays
            if (days == 1L) "1 day ago" else "$days days ago"
        }
        duration.inWholeDays < 28 -> {
            val weeks = duration.inWholeDays / 7
            if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        }
        else -> this.date.asString(format = DateFormats.DD_MM_YYYY) ?: ""
    }
}

fun LocalDateTime.timeLeftInMinutes(): Long {
    val instantThis = this.toInstant(TimeZone.UTC)
    val instantNow = UTCLocalDateTime.toInstant(TimeZone.UTC)

    return (instantThis - instantNow).inWholeMinutes
}

fun Long.timeLeftAsString(): String {
    val hours = this / 60
    val minutes = this % 60

    return when {
        hours > 24 -> {
            val days = hours / 24
            if (days == 1L) "1 day" else "$days days"
        }
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "a few seconds"
    }
}
