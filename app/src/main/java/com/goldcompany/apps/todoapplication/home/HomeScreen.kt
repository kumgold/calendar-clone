package com.goldcompany.apps.todoapplication.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    addTask: () -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = modifier,
                title = {
                    Text(
                        text = stringResource(id = R.string.all_tasks)
                    )
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = stringResource(id = R.string.menu)
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.all_tasks))
                                },
                                onClick = {
                                    viewModel.setFiltering(TasksFilterType.ALL_TASKS)
                                    expanded = !expanded
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.active_tasks))
                                },
                                onClick = {
                                    viewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
                                    expanded = !expanded
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.completed_tasks))
                                },
                                onClick = {
                                    viewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
                                    expanded = !expanded
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = addTask) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_task))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TaskScreen(
            modifier = modifier.padding(paddingValues),
            loadingState = uiState.isLoading,
            tasks = uiState.items,
            onTaskClick = onTaskClick,
            updateTaskCompleted = viewModel::updateTaskCompleted
        )
    }
}

@Composable
private fun TaskScreen(
    modifier: Modifier,
    loadingState: Boolean,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    updateTaskCompleted: (Task, Boolean) -> Unit
) {
    when (loadingState) {
        true -> {
            LoadingAnimation(
                modifier = modifier
            )
        }
        false -> {
            LazyColumn(
                modifier = modifier
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onCheckChange = updateTaskCompleted,
                        onTaskClick = onTaskClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onCheckChange: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    val isCompleted = remember { mutableStateOf(task.isCompleted) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable { onTaskClick(task) }
    ) {
        Checkbox(
            checked = isCompleted.value,
            onCheckedChange = {
                isCompleted.value = it
                onCheckChange(task, it)
            }
        )
        Text(text = task.title)
    }
}