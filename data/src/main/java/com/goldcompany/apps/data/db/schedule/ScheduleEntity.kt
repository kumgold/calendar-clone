package com.goldcompany.apps.data.db.schedule

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo val id: Long = 0,
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val startDateMilli: Long = System.currentTimeMillis(),
    @ColumnInfo val startHour: Int,
    @ColumnInfo val startMinute: Int,
    @ColumnInfo val endDateMilli: Long = System.currentTimeMillis(),
    @ColumnInfo val endHour: Int,
    @ColumnInfo val endMinute: Int,
    @ColumnInfo val isAllDay: Boolean,
    @ColumnInfo val place: String
)