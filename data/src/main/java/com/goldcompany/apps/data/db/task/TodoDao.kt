package com.goldcompany.apps.data.db.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM task WHERE dateTimeMilli = :milli")
    suspend fun getDailyTasks(milli: Long): List<TodoEntity>

    @Query("SELECT * FROM task WHERE dateTimeMilli >= :startMilli AND dateTimeMilli < :endMilli")
    suspend fun getMonthlyTasks(startMilli: Long, endMilli: Long): List<TodoEntity>

    @Query("SELECT * FROM task WHERE isCompleted = 0")
    fun getAllTasksForWidget(): List<TodoEntity>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(todoEntity: TodoEntity)

    @Update
    suspend fun updateTask(todoEntity: TodoEntity)

    @Query("UPDATE task SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: Long, completed: Boolean)

    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}