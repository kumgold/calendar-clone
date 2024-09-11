package com.goldcompany.apps.todoapplication.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goldcompany.apps.todoapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenAppBar(
    modifier: Modifier = Modifier,
    isEdit: Boolean,
    deleteTask: () -> Unit,
    saveTask: () -> Unit,
    navigateBack: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (isEdit) {
                TextButton(onClick = { deleteTask() }) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                IconButton(onClick = { saveTask() }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    title: String
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(text = title)
        }
    )
}

@Preview
@Composable
private fun TaskDetailAppBarPreview() {
    DetailScreenAppBar(
        isEdit = true,
        deleteTask = {},
        saveTask = {},
        navigateBack = {}
    )
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    HomeTopAppBar(
        title = "title"
    )
}