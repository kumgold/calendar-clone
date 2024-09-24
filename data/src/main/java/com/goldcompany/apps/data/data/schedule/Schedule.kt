package com.goldcompany.apps.data.data.schedule

data class Schedule(
    val id: String = System.currentTimeMillis().toString(),
    val title: String = "",
    val description: String = "",
    val startDateTimeMilli: Long = System.currentTimeMillis(),
    val startHour: Int = 0,
    val startMinute: Int = 0,
    val endDateTimeMilli: Long = System.currentTimeMillis(),
    val endHour: Int = 0,
    val endMinute: Int = 0,
    val isAllDay: Boolean = false,
    val place: String? = null,
    val alarmList: MutableList<Long> = mutableListOf()
)