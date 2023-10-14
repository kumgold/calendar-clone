package com.goldcompany.apps.todoapplication.addedittask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.compose.TitleTopAppBar
import com.goldcompany.apps.todoapplication.util.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TitleTopAppBar(
                title = R.string.add_task,
                navigateBack = navigateBack
            )
        }
    ) { paddingValues ->
        AddEditTaskContent(
            modifier = modifier.padding(paddingValues),
            loadingState = uiState.loadingState,
            isCompleted = uiState.isCompleted,
            onUpdateTaskCompleted = viewModel::updateTaskCompleted,
            title = uiState.title,
            description = uiState.description,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            updateTask = viewModel::updateTask,
            navigateBack = navigateBack
        )
    }

    LaunchedEffect(uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            navigateBack()
        }
    }
}

@Composable
private fun AddEditTaskContent(
    modifier: Modifier,
    loadingState: LoadingState,
    isCompleted: Boolean,
    onUpdateTaskCompleted: (Boolean) -> Unit,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    updateTask: () -> Unit,
    navigateBack: () -> Unit
) {
    when (loadingState) {
        LoadingState.INIT -> {}
        LoadingState.LOADING -> {
            LoadingAnimation(
                modifier = modifier
            )
        }
        LoadingState.SUCCESS -> {
            EditTaskScreen(
                modifier = modifier,
                title = title,
                isCompleted = isCompleted,
                onUpdateTaskCompleted = onUpdateTaskCompleted,
                description = description,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                updateTask = updateTask,
                navigateBack = navigateBack
            )
        }
        LoadingState.ERROR -> {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskScreen(
    modifier: Modifier = Modifier,
    isCompleted: Boolean,
    onUpdateTaskCompleted: (Boolean) -> Unit,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    updateTask: () -> Unit,
    navigateBack: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
            .verticalScroll(rememberScrollState())
            .fillMaxSize()

    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.title_hint),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = dimensionResource(id = R.dimen.vertical_margin)),
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = {
                Text(text = stringResource(id = R.string.description_hint))
            },
            textStyle = MaterialTheme.typography.bodyLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(id = R.dimen.horizontal_margin)),
                onClick = { updateTask() }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = { navigateBack() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }
}