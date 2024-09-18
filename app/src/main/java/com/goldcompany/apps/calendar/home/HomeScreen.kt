package com.goldcompany.apps.calendar.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.compose.HomeTopAppBar
import com.goldcompany.apps.calendar.home.compose.AddSchedulesButton
import com.goldcompany.apps.calendar.home.compose.CalendarView
import com.goldcompany.apps.calendar.home.compose.ScheduleList
import com.goldcompany.apps.calendar.home.compose.TodoList
import com.goldcompany.apps.calendar.util.convertMilliToDate
import com.goldcompany.apps.calendar.widget.TaskWidget
import com.goldcompany.apps.calendar.widget.TaskWidgetReceiver

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

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    goToAddTodo: (Long, String?) -> Unit,
    goToAddSchedule: (Long, String?) -> Unit
) {
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(uiState.currentMonthLocalDate) {
        viewModel.getMonthlyTodos()
        viewModel.getSchedules()
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.getMonthlyTodos()
                    viewModel.getSchedules()
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
        floatingActionButton = {
            AddSchedulesButton(
                isExpanded = isExpanded,
                goToAddTodo = {
                    goToAddTodo(uiState.currentDateMilli, null)
                },
                goToAddSchedule = {
                    goToAddSchedule(uiState.currentDateMilli, null)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
        ) {
            HomeTopAppBar(title = uiState.currentDateMilli.convertMilliToDate())
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
            CalendarView(
                selectedDateMilli = uiState.currentDateMilli,
                monthlyTodos = uiState.monthlyTodos,
                schedules = uiState.schedules,
                setCurrentDateMilli = { milli ->
                    viewModel.setCurrentDateMilli(milli)
                },
                setCurrentMonth = { date ->
                    viewModel.setCurrentMonthDate(date)
                }
            )
            ScheduleList(
                schedules = uiState.schedules.filter { it.startDateTimeMilli == uiState.currentDateMilli },
                goToScheduleDetail = { id ->
                    goToAddSchedule(uiState.currentDateMilli, id)
                }
            )
            TodoList(
                todos = uiState.monthlyTodos[uiState.currentDateMilli] ?: emptyList(),
                goToTodoDetail = { id ->
                    goToAddTodo(uiState.currentDateMilli, id)
                },
                updateTodo = { id, isCompleted ->
                    viewModel.updateTodo(id, isCompleted)
                }
            )
        }

        if (isExpanded.value) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.7f))
                    .clickable { isExpanded.value = false }
            )
        }
    }

    TaskActionBroadcastReceiver(TaskWidgetReceiver.UPDATE_ACTION) { intent ->
        val id = intent?.getStringExtra(TaskWidget.KEY_TASK_ID).toString()
        val isCompleted = intent?.getBooleanExtra(TaskWidget.KEY_TASK_STATE, false) ?: false

        viewModel.updateTodo(id, isCompleted)
    }
}

@Composable
private fun EmptyTask() {
    val color = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            .drawBehind {
                val height = size.height

                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 15f
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
        Text(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.default_margin_large)),
            text = "일정이 없습니다.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}