package com.goldcompany.apps.data.usecase

import com.goldcompany.apps.data.data.task.Todo
import com.goldcompany.apps.data.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(): Todo = repository.getAllTasksForWidget().first()
}