package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.data.schedule.Schedule
import com.goldcompany.apps.data.data.schedule.toExternal
import com.goldcompany.apps.data.data.schedule.toLocal
import com.goldcompany.apps.data.db.schedule.ScheduleDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao
) {
    suspend fun getMonthlySchedules(startMilli: Long, endMilli: Long): List<Schedule> {
        return scheduleDao.getMonthlySchedules(startMilli, endMilli).map {
            it.toExternal()
        }
    }

    suspend fun getSchedule(id: String): Schedule? {
        return scheduleDao.getSchedule(id)?.toExternal()
    }

    suspend fun insertSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule.toLocal())
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule.toLocal())
    }

    suspend fun deleteSchedule(id: Long) {
        scheduleDao.deleteSchedule(id)
    }
}