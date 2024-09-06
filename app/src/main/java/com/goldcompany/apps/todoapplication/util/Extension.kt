package com.goldcompany.apps.todoapplication.util

import java.time.LocalDate
import java.time.ZoneOffset

fun LocalDate.dateToMilli(): Long {
    return this.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
}