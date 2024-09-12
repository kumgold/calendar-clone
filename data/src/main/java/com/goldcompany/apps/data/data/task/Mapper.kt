package com.goldcompany.apps.data.data.task

import com.goldcompany.apps.data.db.task.TodoEntity

fun TodoEntity.toExternal() = Todo(
    id = id.toString(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    dateMilli = dateTimeMilli
)

fun List<TodoEntity>.toExternal() = map(TodoEntity::toExternal)

fun Todo.toLocal() = TodoEntity(
    id = id.toLong(),
    isCompleted = isCompleted,
    title = title,
    description = description,
    dateTimeMilli = dateMilli
)