package com.goldcompany.apps.data.data.todo

import com.goldcompany.apps.data.db.todo.TodoEntity

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