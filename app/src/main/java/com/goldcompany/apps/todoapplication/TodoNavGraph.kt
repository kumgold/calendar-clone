package com.goldcompany.apps.todoapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldcompany.apps.todoapplication.addedittask.AddEditTaskScreen
import com.goldcompany.apps.todoapplication.home.HomeScreen

@Composable
fun TodoNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = TodoDestinations.HOME
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = TodoDestinations.HOME,
            arguments = listOf(
                navArgument(TASK_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            HomeScreen(
                addTask = {
                    navController.navigate(
                        TodoDestinations.ADD_EDIT_TASK
                    )
                }
            )
        }
        composable(
            route = TodoDestinations.ADD_EDIT_TASK
        ) {
            AddEditTaskScreen()
        }
    }
}

const val TASK_ID = "taskId"