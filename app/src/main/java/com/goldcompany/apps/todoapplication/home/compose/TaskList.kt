package com.goldcompany.apps.todoapplication.home.compose

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.widget.TaskWidget
import com.goldcompany.apps.todoapplication.widget.TaskWidgetReceiver
import kotlinx.coroutines.launch

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    loadingState: Boolean,
    tasks: List<Task>,
    goToTaskDetail: (String) -> Unit,
    updateTask: (String, Boolean) -> Unit
) {
    when (loadingState) {
        true -> {
            LoadingAnimation(
                modifier = modifier
            )
        }
        false -> {
            if (tasks.isEmpty()) {
                EmptyTask()
            } else {
                LazyColumn(
                    modifier = modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
                ) {
                    items(
                        items = tasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskItem(
                            task = task,
                            updateTask = updateTask,
                            goToTaskDetail = goToTaskDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTask() {
    val color = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
            .drawBehind {
                val height = size.height

                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 10f
                )
            }
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
        Text(text = "일정이 없습니다.")
    }
}

@Composable
private fun TaskItem(
    task: Task,
    updateTask: (String, Boolean) -> Unit,
    goToTaskDetail: (String) -> Unit
) {
    val isChecked = remember { mutableStateOf(task.isCompleted) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable { goToTaskDetail(task.id) }
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                coroutineScope.launch {
                    updateTaskWidget(context, task)
                }

                updateTask(task.id, it)
                isChecked.value = it
            }
        )
        Text(
            text = task.title,
            fontSize = 15.sp,
            textDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
        )
    }
}

private suspend fun updateTaskWidget(
    context: Context,
    task: Task
) {
    val manager = GlanceAppWidgetManager(context)
    manager.getGlanceIds(TaskWidget::class.java).forEach { id ->

        updateAppWidgetState(context, id) {
            val taskId = it[TaskWidgetReceiver.currentTaskId]

            if (taskId == task.id) {
                it[TaskWidgetReceiver.currentTaskState] = !task.isCompleted
                it[TaskWidgetReceiver.currentTaskTitle] = task.title
                it[TaskWidgetReceiver.currentTaskDescription] = task.description
            }
        }

        val appWidget = TaskWidget()
        appWidget.update(context, id)
    }
}