package com.goldcompany.apps.data.repository

import com.goldcompany.apps.data.db.TaskDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

}