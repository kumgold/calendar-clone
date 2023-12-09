package com.goldcompany.apps.data.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

private const val DATE_PATTERN = "yyyy-MM-dd"

fun convertMilliToDate(milli: Long?): String? {
    if (milli == null) return null

    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    val instant = Instant.ofEpochMilli(milli)
    val date = LocalDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId())

    return formatter.format(date)
}

fun convertDateToMilli(date: String?): Long? {
    if (date == null) return null

    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    val localDate = LocalDate.parse(date, formatter)

    return LocalDateTime.of(localDate, LocalTime.of(0, 0))
        .atOffset(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}