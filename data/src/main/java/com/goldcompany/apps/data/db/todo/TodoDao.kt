package com.goldcompany.apps.data.db.todo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE dateTimeMilli = :milli")
    suspend fun getDailyTodos(milli: Long): List<TodoEntity>

    @Query("SELECT * FROM todo WHERE dateTimeMilli >= :startMilli AND dateTimeMilli < :endMilli")
    suspend fun getMonthlyTodos(startMilli: Long, endMilli: Long): List<TodoEntity>

    @Query("SELECT * FROM todo WHERE isCompleted = 0")
    fun getAllTodosForWidget(): List<TodoEntity>

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getTodo(id: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todoEntity: TodoEntity)

    @Update
    suspend fun updateTodo(todoEntity: TodoEntity)

    @Query("UPDATE todo SET isCompleted = :completed WHERE id = :todoId")
    suspend fun updateCompletedTodo(todoId: Long, completed: Boolean)

    @Query("DELETE FROM todo WHERE id = :todoId")
    suspend fun deleteTodo(todoId: Long)
}