package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.data.todo.toExternal
import com.goldcompany.apps.data.data.todo.toLocal
import com.goldcompany.apps.data.db.todo.TodoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getAllTasks(): Flow<List<Todo>> {
        return todoDao.getAllTasks().map { task ->
            task.toExternal()
        }
    }

    suspend fun getDailyTasks(milli: Long): List<Todo> {
        return todoDao.getDailyTasks(milli).map { task ->
            task.toExternal()
        }
    }

    suspend fun getMonthlyTasks(startMilli: Long, endMilli: Long): List<Todo> {
        return todoDao.getMonthlyTasks(startMilli, endMilli).map { task ->
            task.toExternal()
        }
    }

    fun getAllTasksForWidget(): List<Todo> {
        return todoDao.getAllTasksForWidget().map {
            it.toExternal()
        }
    }

    suspend fun getTask(id: String): Todo? {
        return todoDao.getTask(id)?.toExternal()
    }

    suspend fun addTask(todo: Todo) {
        todoDao.insertTask(todo.toLocal())
    }

    suspend fun updateTask(todo: Todo) {
        todoDao.updateTask(todo.toLocal())
    }

    suspend fun updateCompleted(taskId: Long, completed: Boolean) {
        todoDao.updateCompleted(taskId, completed)
    }

    suspend fun deleteTask(taskId: Long) {
        todoDao.deleteTask(taskId)
    }
}