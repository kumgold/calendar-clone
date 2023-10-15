package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.data.toExternal
import com.goldcompany.apps.data.data.toLocal
import com.goldcompany.apps.data.db.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun getAllTask(): List<Task> {
        return taskDao.getAllTask().map { task ->
            task.toExternal()
        }
    }

    suspend fun getTask(id: String): Task? {
        return taskDao.getTask(id)?.toExternal()
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toLocal())
    }
}