package com.goldcompany.apps.data.db.schedule

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule WHERE startDateMilli >= :startMilli AND endDateMilli <= :endMilli")
    suspend fun getMonthlySchedules(startMilli: Long, endMilli: Long): List<ScheduleEntity>

    @Query("SELECT * FROM schedule WHERE id = :id")
    suspend fun getSchedule(id: String): ScheduleEntity?

    @Insert
    suspend fun insertSchedule(scheduleEntity: ScheduleEntity)

    @Update
    suspend fun updateSchedule(scheduleEntity: ScheduleEntity)

    @Query("DELETE FROM schedule WHERE id = :id")
    suspend fun deleteSchedule(id: Long)
}