package com.goldcompany.apps.todoapplication.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.data.util.convertMilliToDate
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.HomeTopAppBar
import com.goldcompany.apps.todoapplication.home.compose.AddSchedulesButton
import com.goldcompany.apps.todoapplication.home.compose.CalendarView
import com.goldcompany.apps.todoapplication.home.compose.TaskList
import com.goldcompany.apps.todoapplication.widget.TaskWidget
import com.goldcompany.apps.todoapplication.widget.TaskWidgetReceiver

@Composable
fun TaskActionBroadcastReceiver(
    action: String,
    onEvent: (intent: Intent?) -> Unit
) {
    val currentEvent by rememberUpdatedState(newValue = onEvent)
    val context = LocalContext.current

    DisposableEffect(context, currentEvent) {
        val filter = IntentFilter(action)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                currentEvent(intent)
            }
        }

        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    goToAddTask: (Long, String?) -> Unit,
    goToAddSchedule: () -> Unit
) {
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(key1 = lifecycleOwner, uiState.startLocalDate) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.getMonthlyTasks(
                        uiState.startLocalDate,
                        uiState.startLocalDate.plusMonths(1)
                    )
                }
                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(
                title = convertMilliToDate(uiState.selectedDateMilli)
            )
        },
        floatingActionButton = {
            AddSchedulesButton(
                goToAddTask = {
                    goToAddTask(uiState.selectedDateMilli, null)
                },
                goToAddSchedule = {
                    goToAddSchedule()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues),
        ) {
            CalendarView(
                selectedDateMilli = uiState.selectedDateMilli,
                monthlyTasks = uiState.monthlyTasks,
                selectDateMilli = { milli ->
                    viewModel.selectDateMilli(milli)
                },
                getMonthlyTasks = { startDate, endDate ->
                    viewModel.getMonthlyTasks(startDate, endDate)
                }
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
            TaskList(
                loadingState = uiState.isLoading,
                tasks = uiState.monthlyTasks[uiState.selectedDateMilli] ?: emptyList(),
                goToTaskDetail = { id ->
                    goToAddTask(uiState.selectedDateMilli, id)
                },
                updateTask = { id, isCompleted ->
                    viewModel.updateTask(id, isCompleted)
                }
            )
        }

    }

    TaskActionBroadcastReceiver(TaskWidgetReceiver.UPDATE_ACTION) { intent ->
        val id = intent?.getStringExtra(TaskWidget.KEY_TASK_ID).toString()
        val isCompleted = intent?.getBooleanExtra(TaskWidget.KEY_TASK_STATE, false) ?: false

        viewModel.updateTask(id, isCompleted)
    }
}

