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
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.goldcompany.apps.data.data.task.Todo
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.widget.TaskWidget
import com.goldcompany.apps.todoapplication.widget.TaskWidgetReceiver
import kotlinx.coroutines.launch

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    todos: List<Todo>,
    goToTaskDetail: (String) -> Unit,
    updateTask: (String, Boolean) -> Unit
) {
    if (todos.isEmpty()) {
        EmptyTask()
    } else {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(5.dp)
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
        ) {
            items(
                items = todos,
                key = { task -> task.id }
            ) { task ->
                TaskItem(
                    todo = task,
                    updateTask = updateTask,
                    goToTaskDetail = goToTaskDetail
                )
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
        Text(text = "일정이 없습니다.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TaskItem(
    todo: Todo,
    updateTask: (String, Boolean) -> Unit,
    goToTaskDetail: (String) -> Unit
) {
    val isChecked = remember { mutableStateOf(todo.isCompleted) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { goToTaskDetail(todo.id) }
            .padding(
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                coroutineScope.launch {
                    updateTaskWidget(context, todo)
                }

                updateTask(todo.id, it)
                isChecked.value = it
            },
            colors = CheckboxDefaults.colors().copy(
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = todo.title,
            textDecoration = if (todo.isCompleted) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            },
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private suspend fun updateTaskWidget(
    context: Context,
    todo: Todo
) {
    val manager = GlanceAppWidgetManager(context)
    manager.getGlanceIds(TaskWidget::class.java).forEach { id ->

        updateAppWidgetState(context, id) {
            val taskId = it[TaskWidgetReceiver.currentTaskId]

            if (taskId == todo.id) {
                it[TaskWidgetReceiver.currentTaskState] = !todo.isCompleted
                it[TaskWidgetReceiver.currentTaskTitle] = todo.title
                it[TaskWidgetReceiver.currentTaskDescription] = todo.description
            }
        }

        val appWidget = TaskWidget()
        appWidget.update(context, id)
    }
}