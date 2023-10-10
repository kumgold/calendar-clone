package com.goldcompany.apps.todoapplication.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.util.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    addTask: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = addTask) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_task))
            }
        }
    ) { paddingValues ->
        TaskScreen(
            modifier = modifier.padding(paddingValues),
            loadingState = uiState.loadingState,
            tasks = uiState.items
        )
    }
}

@Composable
private fun TaskScreen(
    modifier: Modifier,
    loadingState: LoadingState,
    tasks: List<Task>
) {
    when (loadingState) {
        LoadingState.INIT -> {

        }
        LoadingState.LOADING -> {

        }
        LoadingState.SUCCESS -> {
            LazyColumn(
                modifier = modifier
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onCheckChange = {},
                        onTaskClick = {}
                    )
                }
            }
        }
        LoadingState.ERROR -> {

        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onCheckChange: (Boolean) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 10.dp
            )
            .clickable { onTaskClick(task) }
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckChange
        )
        Text(text = task.title)
    }
}