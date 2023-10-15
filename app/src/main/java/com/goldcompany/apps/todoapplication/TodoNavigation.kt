package com.goldcompany.apps.todoapplication

import androidx.navigation.NavHostController

object TodoDestinations {
    const val HOME = "home"
    const val ADD_EDIT_TASK = "addEditTask"
}

class TodoNavigation(private val navController: NavHostController) {
    fun navigateTaskDetail(taskId: Long) {
        navController.navigate("${TodoDestinations.ADD_EDIT_TASK}?taskId=$taskId")
    }
}