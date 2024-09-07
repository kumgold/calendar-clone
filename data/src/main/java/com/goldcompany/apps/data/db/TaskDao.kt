package com.goldcompany.apps.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE dateTimeMilli = :milli")
    suspend fun getDailyTasks(milli: Long): List<TaskEntity>

    @Query("SELECT * FROM task WHERE dateTimeMilli >= :startMilli AND dateTimeMilli < :endMilli")
    suspend fun getMonthlyTasks(startMilli: Long, endMilli: Long): List<TaskEntity>

    @Query("SELECT * FROM task WHERE isCompleted = 0")
    fun getAllTasksForWidget(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Query("UPDATE task SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: Long, completed: Boolean)

    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}