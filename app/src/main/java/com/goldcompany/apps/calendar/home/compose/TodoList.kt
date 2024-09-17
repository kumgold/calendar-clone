package com.goldcompany.apps.calendar.home.compose

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.widget.TaskWidget
import com.goldcompany.apps.calendar.widget.TaskWidgetReceiver
import com.goldcompany.apps.data.data.todo.Todo
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoList(
    modifier: Modifier = Modifier,
    todos: List<Todo>,
    goToTodoDetail: (String) -> Unit,
    updateTodo: (String, Boolean) -> Unit
) {
    if (todos.isEmpty()) {
        EmptyTask()
    } else {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        LazyColumn(
            modifier = modifier
                .padding(horizontal = dimensionResource(id = R.dimen.default_margin))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            stickyHeader {
                Text(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.default_margin),
                        start = dimensionResource(id = R.dimen.default_margin)
                    ),
                    text = stringResource(id = R.string.todo),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 14.sp
                )
            }
            items(
                items = todos,
                key = { todo -> todo.id }
            ) { todo ->
                TodoItem(
                    todo = todo,
                    updateTodo = updateTodo,
                    goToTodoDetail = goToTodoDetail
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

@Composable
private fun TodoItem(
    todo: Todo,
    updateTodo: (String, Boolean) -> Unit,
    goToTodoDetail: (String) -> Unit
) {
    val isChecked = remember { mutableStateOf(todo.isCompleted) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { goToTodoDetail(todo.id) }
            .padding(
                vertical = dimensionResource(id = R.dimen.default_margin_small)
            )
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                coroutineScope.launch {
                    updateTodoWidget(context, todo)
                }

                updateTodo(todo.id, it)
                isChecked.value = it
            },
            colors = CheckboxDefaults.colors().copy(
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = todo.title,
            textDecoration = if (isChecked.value) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private suspend fun updateTodoWidget(
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