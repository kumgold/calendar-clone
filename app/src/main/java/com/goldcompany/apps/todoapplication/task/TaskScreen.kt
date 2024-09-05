package com.goldcompany.apps.todoapplication.task

import androidx.annotation.StringRes
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.data.util.convertMilliToDate
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.compose.TaskDetailAppBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskDetailAppBar(
                title = R.string.add_task,
                deleteTask = {
                    coroutineScope.launch {
                        viewModel.deleteTask()
                        navigateBack()
                    }
                },
                navigateBack = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },

    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        if (uiState.isLoading) {
            LoadingAnimation(modifier = modifier.padding(paddingValues))
        } else {
            EditTask(
                modifier = modifier.padding(paddingValues),
                state = uiState,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                saveTask = {
                    viewModel.saveTask()
                    navigateBack()
                },
                navigateBack = navigateBack,
                onDateSelected = viewModel::updateStartDate
            )
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
private fun EditTask(
    modifier: Modifier,
    state: TaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    saveTask: () -> Unit,
    navigateBack: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
            .fillMaxSize()
    ) {
        TaskTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.title,
            onTextChange = onTitleChange,
            titleResource = R.string.title,
            hintResource = R.string.title_hint,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.date))
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.vertical_margin)))
            SelectTaskDate(
                savedDate = state.date,
                onDateSelected = onDateSelected
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        TaskTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = state.description,
            onTextChange = onDescriptionChange,
            titleResource = R.string.description,
            hintResource = R.string.description_hint,
            isSingleLine = false
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { saveTask() }
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

@Composable
private fun SelectTaskDate(
    modifier: Modifier = Modifier,
    savedDate: String,
    onDateSelected: (Long) -> Unit
) {
    var date by remember { mutableStateOf(savedDate) }
    var isShowDatePickerDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(2.dp))
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.black)
            )
            .clickable {
                isShowDatePickerDialog = true
            }
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            textAlign = TextAlign.Center
        )
    }

    if (isShowDatePickerDialog) {
        TaskDatePickerDialog(
            onDateSelected = {
                println(it)
                date = convertMilliToDate(it)
                onDateSelected(it)
            },
            onDismiss = { isShowDatePickerDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    val currentTime = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

                    onDateSelected(selectedDate ?: currentTime)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun TaskTextInput(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    @StringRes titleResource: Int,
    @StringRes hintResource: Int,
    isSingleLine: Boolean

) {
    Text(
        text = stringResource(id = titleResource),
        style = MaterialTheme.typography.titleLarge
    )
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChange,
        placeholder = {
            Text(
                text = stringResource(id = hintResource),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = isSingleLine
    )
}
