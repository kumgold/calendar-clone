package com.goldcompany.apps.todoapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goldcompany.apps.todoapplication.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    title: String,
    onFilterAllTasks: () -> Unit,
    onFilterActiveTasks: () -> Unit,
    onFilterCompletedTasks: () -> Unit,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu)
                )
            }
        },
        actions = {
            FilterTaskMenu(
                onFilterAllTasks = onFilterAllTasks,
                onFilterActiveTasks = onFilterActiveTasks,
                onFilterCompletedTasks = onFilterCompletedTasks
            )
        }
    )
}

@Composable
private fun FilterTaskMenu(
    onFilterAllTasks: () -> Unit,
    onFilterActiveTasks: () -> Unit,
    onFilterCompletedTasks: () -> Unit
) {
    TopAppBarDropDownMenu(
        iconContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_list_24),
                contentDescription = stringResource(id = R.string.menu)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.all_tasks)) },
            onClick = {
                onFilterAllTasks()
                closeMenu()
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.active_tasks)) },
            onClick = {
                onFilterActiveTasks()
                closeMenu()
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.completed_tasks)) },
            onClick = {
                onFilterCompletedTasks()
                closeMenu()
            }
        )
    }
}

@Composable
private fun TopAppBarDropDownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    HomeTopAppBar(
        title = "title",
        onFilterAllTasks = { },
        onFilterActiveTasks = { },
        onFilterCompletedTasks = { },
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
}