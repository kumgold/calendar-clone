package com.goldcompany.apps.calendar.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

private const val DATE_PATTERN = "yyyy-MM-dd"

fun LocalDate.convertDateToMilli(): Long {
    return this.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun Long.convertMilliToDate(): String {
    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    val instant = Instant.ofEpochMilli(this)
    val date = LocalDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId())

    return formatter.format(date)
}