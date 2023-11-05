package com.goldcompany.apps.todoapplication.addedittask

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.data.util.convertMilliToDate
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.compose.TitleTopAppBar

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
            state = uiState,
            onTitleChange = viewModel::updateTitle,
            onDescriptionChange = viewModel::updateDescription,
            updateTask = viewModel::saveTask,
            navigateBack = navigateBack,
            onStartDateSelected = viewModel::updateStartDate,
            onEndDateSelected = viewModel::updateEndDate
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
    state: AddEditTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    updateTask: () -> Unit,
    navigateBack: () -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit
) {
    when (state.isLoading) {
        true -> {
            LoadingAnimation(
                modifier = modifier
            )
        }
        false -> {
            EditTaskScreen(
                modifier = modifier,
                state = state,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                updateTask = updateTask,
                navigateBack = navigateBack,
                onStartDateSelected = onStartDateSelected,
                onEndDateSelected = onEndDateSelected
            )
        }
    }
}

@Composable
private fun EditTaskScreen(
    modifier: Modifier,
    state: AddEditTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    updateTask: () -> Unit,
    navigateBack: () -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
            .fillMaxSize()
    ) {
        TaskTextInputView(
            modifier = Modifier.fillMaxWidth(),
            text = state.title,
            onTextChange = onTitleChange,
            titleResource = R.string.title,
            hintResource = R.string.title_hint,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            SelectTaskDatesView(
                modifier = Modifier.weight(1f),
                savedDate = state.startDate,
                defaultText = stringResource(id = R.string.start_date),
                onDateSelected = onStartDateSelected
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
            SelectTaskDatesView(
                modifier = Modifier.weight(1f),
                savedDate = state.endDate,
                defaultText = stringResource(id = R.string.end_date),
                onDateSelected = onEndDateSelected
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        TaskTextInputView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = state.description,
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

@Composable
private fun SelectTaskDatesView(
    modifier: Modifier,
    savedDate: String?,
    defaultText: String,
    onDateSelected: (String) -> Unit
) {
    var date by remember {
        if (savedDate != null) mutableStateOf(savedDate)
        else mutableStateOf(defaultText)
    }
    var isShowDatePickerDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = colorResource(id = R.color.black),
            shape = RoundedCornerShape(2.dp)
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isShowDatePickerDialog = true
                },
            text = date,
            textAlign = TextAlign.Center
        )
    }

    if (isShowDatePickerDialog) {
        TaskDatePickerDialog(
            onDateSelected = {
                onDateSelected(it)
                date = it
            },
            onDismiss = { isShowDatePickerDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMilliToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(selectedDate)
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
