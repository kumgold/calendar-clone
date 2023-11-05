package com.goldcompany.apps.data.data

import com.goldcompany.apps.data.db.TaskEntity
import com.goldcompany.apps.data.util.convertDateToMilli
import com.goldcompany.apps.data.util.convertMilliToDate

fun TaskEntity.toExternal() = Task(
    id = id.toString(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    startDate = convertMilliToDate(startTimeMilli),
    endDate = convertMilliToDate(endTimeMilli)
)

fun List<TaskEntity>.toExternal() = map(TaskEntity::toExternal)

fun Task.toLocal() = TaskEntity(
    id = id.toLong(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    startTimeMilli = convertDateToMilli(startDate),
    endTimeMilli = convertDateToMilli(endDate)
)