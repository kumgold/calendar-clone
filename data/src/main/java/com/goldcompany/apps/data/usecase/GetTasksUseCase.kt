package com.goldcompany.apps.data.usecase

import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.repository.TodoRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TodoRepository) {
    operator fun invoke(): Todo = repository.getAllTodosForWidget().first()
}