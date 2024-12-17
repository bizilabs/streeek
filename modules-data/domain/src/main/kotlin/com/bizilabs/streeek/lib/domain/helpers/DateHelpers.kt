package com.bizilabs.streeek.lib.domain.helpers

import kotlinx.datetime.LocalDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormats {
    const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val EEE_MMM_dd_yyyy_HH_mm = "EEE, MMM dd yyyy HH:mm a"
}

fun String.asDate(format: String = DateFormats.ISO_8601): Calendar? {
    return tryOrNull {
        val inputFormat = SimpleDateFormat(format, Locale.getDefault())
//        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val parsedDate = inputFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = parsedDate
            ?: throw Exception("Couldn't change String : [$this] to date with the following format : [$format]")
        calendar
    }
}

fun Calendar.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return time.asString(format)
}

fun Date.asString(format: String = DateFormats.EEE_MMM_dd_yyyy_HH_mm): String? {
    return tryOrNull {
        val outputFormat = SimpleDateFormat(format, Locale.getDefault())
//        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
        outputFormat.format(this)
    }
}

fun Date.isSameDay(date: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = this

    val calendar2 = Calendar.getInstance()
    calendar2.time = date

    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
            calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
}

val LocalDate.dayShort
    get() = this.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
