package com.goldcompany.apps.todoapplication.addedittask

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.compose.TitleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TitleTopAppBar(
                title = R.string.add_task,
                navigateBack = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsState()

        AddEditTaskContent(
            modifier = modifier.padding(paddingValues),
            loadingState = uiState.isLoading,
            isCompleted = uiState.isCompleted,
            onUpdateTaskCompleted = viewModel::updateTaskCompleted,
            title = uiState.title,
            description = uiState.description,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            updateTask = viewModel::saveTask,
            navigateBack = navigateBack
        )

        LaunchedEffect(uiState.isTaskSaved) {
            if (uiState.isTaskSaved) {
                navigateBack()
            }
        }

        uiState.message?.let { message ->
            val snackBarMessage = stringResource(id = message)

            LaunchedEffect(key1 = message) {
                snackBarHostState.showSnackbar(
                    message = snackBarMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}

@Composable
private fun AddEditTaskContent(
    modifier: Modifier,
    loadingState: Boolean,
    isCompleted: Boolean,
    onUpdateTaskCompleted: () -> Unit,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    updateTask: () -> Unit,
    navigateBack: () -> Unit
) {
    when (loadingState) {
        true -> {
            LoadingAnimation(
                modifier = modifier
            )
        }
        false -> {
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
    }
}

@Composable
private fun EditTaskScreen(
    modifier: Modifier,
    isCompleted: Boolean,
    onUpdateTaskCompleted: () -> Unit,
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
            .fillMaxSize()
    ) {
        TaskTextInputView(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            onTextChange = onTitleChange,
            titleResource = R.string.title,
            hintResource = R.string.title_hint,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        TaskTextInputView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = description,
            onTextChange = onDescriptionChange,
            titleResource = R.string.description,
            hintResource = R.string.description_hint,
            isSingleLine = false
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { updateTask() }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { navigateBack() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTextInputView(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    @StringRes titleResource: Int,
    @StringRes hintResource: Int,
    isSingleLine: Boolean

) {
    val titleLargeStyle = MaterialTheme.typography.titleLarge
    val textMediumStyle = MaterialTheme.typography.bodyMedium

    Text(
        text = stringResource(id = titleResource),
        style = titleLargeStyle
    )
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChange,
        placeholder = {
            Text(
                text = stringResource(id = hintResource),
                style = textMediumStyle
            )
        },
        textStyle = textMediumStyle,
        singleLine = isSingleLine
    )
}
