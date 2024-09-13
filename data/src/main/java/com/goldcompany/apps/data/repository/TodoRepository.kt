package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.data.todo.toExternal
import com.goldcompany.apps.data.data.todo.toLocal
import com.goldcompany.apps.data.db.todo.TodoDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun getMonthlyTodos(startMilli: Long, endMilli: Long): List<Todo> {
        return todoDao.getMonthlyTodos(startMilli, endMilli).map { task ->
            task.toExternal()
        }
    }

    fun getAllTodosForWidget(): List<Todo> {
        return todoDao.getAllTodosForWidget().map {
            it.toExternal()
        }
    }

    suspend fun getTodo(id: String): Todo? {
        return todoDao.getTodo(id)?.toExternal()
    }

    suspend fun addTodo(todo: Todo) {
        todoDao.insertTodo(todo.toLocal())
    }

    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo.toLocal())
    }

    suspend fun updateCompletedTodo(todoId: Long, completed: Boolean) {
        todoDao.updateCompletedTodo(todoId, completed)
    }

    suspend fun deleteTodo(todoId: Long) {
        todoDao.deleteTodo(todoId)
    }
}