package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.data.toExternal
import com.goldcompany.apps.data.data.toLocal
import com.goldcompany.apps.data.db.TaskDao
import com.goldcompany.apps.data.db.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    suspend fun getTasksStream(): Flow<List<Task>> {
        return taskDao.observeAll().map { tasks ->
            withContext(Dispatchers.IO) {
                tasks.toExternal()
            }
        }
    }

    suspend fun getTask(id: String): Flow<Task> {
        return taskDao.observeById(id).map { it.toExternal() }
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toLocal())
    }
}