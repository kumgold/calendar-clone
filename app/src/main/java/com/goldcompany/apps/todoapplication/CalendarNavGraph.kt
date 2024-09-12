package com.goldcompany.apps.todoapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldcompany.apps.todoapplication.home.HomeScreen
import com.goldcompany.apps.todoapplication.schedule.ScheduleScreen
import com.goldcompany.apps.todoapplication.todo.TaskScreen
import com.goldcompany.apps.todoapplication.util.CURRENT_DATE_MILLI
import com.goldcompany.apps.todoapplication.util.TASK_ID

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
                modifier = modifier,
                goToAddTask = { milli, id ->
                    navActions.navigateTaskDetail(milli, id)
                },
                goToAddSchedule = {
                    navActions.navigateScheduleDetail()
                }
            )
        }
        dialog(
            route = TodoDestinations.SCHEDULE,
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            ScheduleScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        dialog(
            route = "${TodoDestinations.TASK}?currentDateMilli={$CURRENT_DATE_MILLI}&taskId={$TASK_ID}",
            arguments = listOf(
                navArgument(TASK_ID) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(CURRENT_DATE_MILLI) {
                    type = NavType.LongType
                }
            ),
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            TaskScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}