package com.goldcompany.apps.calendar

import androidx.navigation.NavHostController
import com.goldcompany.apps.calendar.TodoDestinations.SCHEDULE

object TodoDestinations {
    const val HOME = "home_screen"
    const val TODO = "todo_screen"
    const val SCHEDULE = "schedule_screen"
}

class TodoNavigation(private val navController: NavHostController) {
    fun navigateTodoDetail(currentDateMilli: Long, todoId: String?) {
        navController.navigate("${TodoDestinations.TODO}?currentDateMilli=$currentDateMilli&todoId=$todoId")
    }
    fun navigateScheduleDetail(currentDateMilli: Long, scheduleId: String?) {
        navController.navigate("${SCHEDULE}?currentDateMilli=$currentDateMilli&scheduleId=$scheduleId")
    }
}