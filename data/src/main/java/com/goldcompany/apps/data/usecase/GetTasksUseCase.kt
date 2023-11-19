package com.goldcompany.apps.data.usecase

import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    operator fun invoke(): Task = repository.getAllTasksForWidget().first()
}