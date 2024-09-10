package com.goldcompany.apps.todoapplication

import androidx.navigation.NavHostController
import com.goldcompany.apps.todoapplication.TodoDestinations.SCHEDULE

object TodoDestinations {
    const val HOME = "home_screen"
    const val TASK = "task_screen"
    const val SCHEDULE = "schedule_screen"
}

class TodoNavigation(private val navController: NavHostController) {
    fun navigateTaskDetail(currentDateMilli: Long, taskId: String?) {
        navController.navigate("${TodoDestinations.TASK}?currentDateMilli=$currentDateMilli&taskId=$taskId")
    }
    fun navigateScheduleDetail() {
        navController.navigate(SCHEDULE)
    }
}