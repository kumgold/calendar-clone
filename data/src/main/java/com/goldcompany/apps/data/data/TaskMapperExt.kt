package com.goldcompany.apps.data.data

import com.goldcompany.apps.data.db.TaskEntity

fun TaskEntity.toExternal() = Task(
    id = id,
    isCompleted = isCompleted,
    title = title,
    description = description,
    startTimeMilli = startTimeMilli,
    endTimeMilli = endTimeMilli
)

fun Task.toLocal() = TaskEntity(
    id = id,
    isCompleted = isCompleted,
    title = title,
    description = description,
    startTimeMilli = startTimeMilli,
    endTimeMilli = endTimeMilli
)