package com.goldcompany.apps.data.data

import com.goldcompany.apps.data.db.TaskEntity

fun TaskEntity.toExternal() = Task(
    id = id.toString(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    startTimeMilli = startTimeMilli,
    endTimeMilli = endTimeMilli
)

fun List<TaskEntity>.toExternal() = map(TaskEntity::toExternal)

fun Task.toLocal() = TaskEntity(
    id = id.toLong(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    startTimeMilli = startTimeMilli,
    endTimeMilli = endTimeMilli
)