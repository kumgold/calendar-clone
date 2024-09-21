package com.goldcompany.apps.data.data.schedule

import com.goldcompany.apps.data.db.schedule.ScheduleEntity

fun ScheduleEntity.toExternal() = Schedule(
    id = id.toString(),
    title = title,
    description = description,
    startDateTimeMilli = startDateMilli,
    startHour = startHour,
    startMinute = startMinute,
    endDateTimeMilli = endDateMilli,
    endHour = endHour,
    endMinute = endMinute,
    isAllDay = isAllDay,
    place = null
)

fun Schedule.toLocal() = ScheduleEntity(
    id = id.toLong(),
    title = title,
    description = description,
    startDateMilli = startDateTimeMilli,
    startHour = startHour,
    startMinute = startMinute,
    endDateMilli = endDateTimeMilli,
    endHour = endHour,
    endMinute = endMinute,
    isAllDay = isAllDay,
    place = place
)