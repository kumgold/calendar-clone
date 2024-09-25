package com.goldcompany.apps.calendar.alarm

import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

data class AlarmItem(
    val displayText: String,
    val dateTimeMilli: Long,
    val checked: Boolean
)

fun getTimerList(
    alarmList: List<Long>,
    currentDateMilli: Long,
    hour: Int,
    minute: Int,
    isAllDay: Boolean
): List<AlarmItem> {
    val date = Instant.ofEpochMilli(currentDateMilli).atZone(ZoneId.systemDefault()).toLocalDate()
    val c = Calendar.getInstance().apply {
        set(date.year, date.monthValue-1, date.dayOfMonth)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val list = mutableListOf(
        AlarmItem(
            "00:00 a.m. on the day",
            c.timeInMillis,
            alarmList.contains(c.timeInMillis)
        ),
        AlarmItem(
            "08:00 a.m. on the day",
            c.apply {
                set(Calendar.HOUR_OF_DAY, 8)
            }.timeInMillis,
            alarmList.contains(
                c.apply {
                    set(Calendar.HOUR_OF_DAY, 8)
                }.timeInMillis
            )
        ),
        AlarmItem(
            "12:00 p.m. on the day",
            c.apply {
                set(Calendar.HOUR_OF_DAY, 12)
            }.timeInMillis,
            alarmList.contains(
                c.apply {
                    set(Calendar.HOUR_OF_DAY, 12)
                }.timeInMillis
            )
        )
    )

    if (!isAllDay) {
        list += listOf(
            AlarmItem(
                "5 minutes before event",
                c.apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute - 5)
                }.timeInMillis,
                alarmList.contains(
                    c.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute - 5)
                    }.timeInMillis
                )
            ),
            AlarmItem(
                "an hour before event",
                c.apply {
                    set(Calendar.HOUR_OF_DAY, hour-1)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis,
                alarmList.contains(
                    c.apply {
                        set(Calendar.HOUR_OF_DAY, hour-1)
                        set(Calendar.MINUTE, minute)
                    }.timeInMillis
                )
            )
        )
    }

    return list
}