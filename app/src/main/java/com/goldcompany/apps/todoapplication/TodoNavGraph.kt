package com.goldcompany.apps.todoapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldcompany.apps.todoapplication.addedittask.EditTaskScreen
import com.goldcompany.apps.todoapplication.home.HomeScreen

@Composable
fun TodoNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = TodoDestinations.HOME,
    navActions: TodoNavigation = remember(navController) {
        TodoNavigation(navController)
    }
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = TodoDestinations.HOME
        ) {
            HomeScreen(
                addTask = {
                    navController.navigate(
                        TodoDestinations.ADD_EDIT_TASK
                    )
                },
                onTaskClick = { task ->
                    navActions.navigateTaskDetail(task.id)
                }
            )
        }
        composable(
            route = TodoDestinations.ADD_EDIT_TASK
        ) {
            EditTaskScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "${TodoDestinations.ADD_EDIT_TASK}?taskId={$TASK_ID}",
            arguments = listOf(
                navArgument(TASK_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            EditTaskScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

const val TASK_ID = "taskId"