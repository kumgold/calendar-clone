package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.data.toExternal
import com.goldcompany.apps.data.data.toLocal
import com.goldcompany.apps.data.db.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { task ->
            task.toExternal()
        }
    }

    suspend fun getDailyTasks(millis: Long): List<Task> {
        return taskDao.getDailyTasks(millis).map { task ->
            task.toExternal()
        }
    }

    fun getAllTasksForWidget(): List<Task> {
        return taskDao.getAllTasksForWidget().map {
            it.toExternal()
        }
    }

    suspend fun getTask(id: String): Task? {
        return taskDao.getTask(id)?.toExternal()
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toLocal())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toLocal())
    }

    suspend fun updateCompleted(taskId: Long, completed: Boolean) {
        taskDao.updateCompleted(taskId, completed)
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTask(taskId)
    }
}