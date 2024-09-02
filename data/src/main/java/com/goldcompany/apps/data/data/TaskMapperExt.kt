package com.goldcompany.apps.data.data

import com.goldcompany.apps.data.db.TaskEntity
import com.goldcompany.apps.data.util.convertDateToMilli
import com.goldcompany.apps.data.util.convertMilliToDate

fun TaskEntity.toExternal() = Task(
    id = id.toString(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    date = convertMilliToDate(dateTimeMilli)
)

fun List<TaskEntity>.toExternal() = map(TaskEntity::toExternal)

fun Task.toLocal() = TaskEntity(
    id = id.toLong(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    dateTimeMilli = convertDateToMilli(date)
)