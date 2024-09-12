package com.goldcompany.apps.data.data.schedule

data class Schedule(
    val id: String = System.currentTimeMillis().toString(),
    val title: String = "",
    val description: String = "",
    val startDateTimeMilli: Long = System.currentTimeMillis(),
    val startHour: Int,
    val startMinute: Int,
    val endDateTimeMilli: Long = System.currentTimeMillis(),
    val endHour: Int,
    val endMinute: Int,
    val isAllDay: Boolean = false,
    val place: String? = null
)